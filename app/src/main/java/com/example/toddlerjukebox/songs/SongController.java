package com.example.toddlerjukebox.songs;

import android.app.Activity;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.TagLostException;
import android.util.Log;

import com.example.toddlerjukebox.SpotifyClient;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SongController {
    private static final String TAG = "NfcReader";
    private final Activity activity;
    private final SpotifyClient client;
    private final NfcAdapter nfcAdapter;
    private final AtomicReference<String> previousId = new AtomicReference<>(null);
    private final AtomicReference<Instant> pausedAt = new AtomicReference<>(null);
    public SongController(Activity activity, SpotifyClient client) {
        this.activity = activity;
        this.client = client;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
    }

    public void enable() {
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(
                    activity,
                    this::onRead,
                    NfcAdapter.FLAG_READER_NFC_A,
                    null
            );
            Log.d(TAG, "Enabled NFC Reader in ReaderMode");
        }
    }

    private void onRead(Tag tag) {
        Log.d(TAG, "Read tag: " + tag.toString());
        try (Ndef ndef = Ndef.get(tag)) {
            if (ndef == null) {
                Log.e(TAG, "Failed to read tag as NDEF");
                return;
            }
            ndef.connect();
            final var trackId = readTrackId(ndef);
            onTrackTagDetected(trackId);

            while (true) {
                ndef.getNdefMessage();
                Thread.sleep(500);
            }
        } catch (TagLostException e) {
            Log.d(TAG, "Tag removed!");
            onTrackTagRemoved();
        } catch (Exception e) {
            Log.e(TAG, "NFC read error", e);
        }
    }

    public void disable() {
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(activity);
            activity.runOnUiThread(this::onTrackTagRemoved);
        }
    }

    private static String readTrackId(Ndef tag) throws IOException, FormatException {
        NdefMessage message = tag.getNdefMessage();
        final var records = message.getRecords();
        if (records == null || records.length == 0) {
            throw new IOException("Found no records on tag!");
        }
        NdefRecord trackIdRecord = records[0];
        byte[] payload = trackIdRecord.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0x3F;
        return new String(payload, languageCodeLength + 1,
                payload.length - languageCodeLength - 1, textEncoding);
    }

    private void onTrackTagDetected(String trackId) {
        if (Objects.equals(trackId, previousId.get()) &&
                Instant.now()
                        .minus(10, ChronoUnit.SECONDS)
                        .isBefore(pausedAt.get())) {
            client.resume();
        } else {
            client.play(trackId);
        }
        previousId.set(trackId);
    }

    private void onTrackTagRemoved() {
        pausedAt.set(Instant.now());
        client.pause();
    }
}
