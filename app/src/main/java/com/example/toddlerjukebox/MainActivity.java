package com.example.toddlerjukebox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Config config;
    private SpotifyClient client;
    private NfcReader nfcReader;
    private UiLocker uiLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new Config();
        client = new SpotifyClient(
                config.clientId,
                config.redirectUri
        );

        nfcReader = new NfcReader(this, client);
        uiLocker = new UiLocker(this);

        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiLocker.lock();
        nfcReader.enable();
        Uri data = getIntent().getData();
        if (data != null) {
            Log.d(TAG, "Redirected with URI: " + data.toString());
            // Optional: handle URI here if you want to extract tokens or validate
        }
        client.connect(this, ()->{}, ()->{});
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.buttonContainer);
        config.getSongs().forEach(song -> {
            var songButton = new SongButton(this, client, song);
            container.addView(songButton);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.disable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null) {
            client.pause();
            client.disconnect();
        }
        uiLocker.unlock();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // So getIntent() returns the new one
    }
}
