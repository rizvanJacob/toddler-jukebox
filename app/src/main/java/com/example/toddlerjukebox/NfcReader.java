package com.example.toddlerjukebox;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

public class NfcReader {
    private final Activity activity;
    private final Consumer<String> onTagDetected;
    private final NfcAdapter nfcAdapter;

    public NfcReader(Activity activity, Consumer<String> onTagDetected) {
        this.activity = activity;
        this.onTagDetected = onTagDetected;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
    }

    public void enable() {
        if (nfcAdapter != null) {
            Intent intent = new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            nfcAdapter.enableForegroundDispatch(
                    activity,
                    PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_MUTABLE),
                    null,
                    null
            );
        }
    }

    public void disable() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public void handleIntent(Intent intent) {
        if (intent == null || !NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            return;
        }

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null && rawMsgs.length > 0) {
            NdefMessage message = (NdefMessage) rawMsgs[0];
            NdefRecord[] records = message.getRecords();
            if (records.length > 0) {
                try {
                    String payload = readText(records[0]);
                    Log.d("NfcReader", "Read NFC payload: " + payload);
                    onTagDetected.accept(payload);
                } catch (Exception e) {
                    Log.e("NfcReader", "Error reading NFC tag", e);
                }
            }
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0x3F;
        return new String(payload, languageCodeLength + 1,
                payload.length - languageCodeLength - 1, textEncoding);
    }
}
