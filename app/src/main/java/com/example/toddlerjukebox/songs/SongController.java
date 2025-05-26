package com.example.toddlerjukebox;

import android.app.Activity;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.TagLostException;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class NfcReader {
    private static final String TAG = "NfcReader";
    private final Activity activity;
    private final SpotifyClient client;
    private final NfcAdapter nfcAdapter;
    private String previousId;
    private Instant pausedAt;

    public NfcReader(Activity activity, SpotifyClient client) {
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
        try {
            final var ndef = Ndef.get(tag);
            if (ndef == null) {
                Log.e(TAG, "Failed to read tag as NDEF");
                return;
            }
            ndef.connect();
            final var trackId = readTrackId(ndef);

            activity.runOnUiThread(() -> onTrackTagDetected(trackId));
            new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        ndef.getNdefMessage(); // throws when tag removed
                    }
                } catch (TagLostException e) {
                    Log.d(TAG, "Tag removed");
                    activity.runOnUiThread(this::onTrackTagRemoved);
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected NFC error", e);
                } finally {
                    try {
                        ndef.close();
                    } catch (Exception ignored) {
                    }
                }
            }).start();
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
        if (Objects.equals(trackId, previousId) &&
                Instant.now()
                        .minus(10, ChronoUnit.SECONDS)
                        .isBefore(pausedAt)) {
            client.resume();
        } else {
            client.play(trackId);
        }
        previousId = trackId;
    }

    private void onTrackTagRemoved() {
        pausedAt = Instant.now();
        client.pause();
    }
}
