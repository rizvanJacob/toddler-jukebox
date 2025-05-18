package com.example.toddlerjukebox;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.graphics.drawable.GradientDrawable;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Config config;
    private SpotifyClient client;

    private final List<SongItem> songItems = List.of(
            new SongItem(Color.valueOf(Color.RED), "spotify:track:1234567890abcdef"), // Red
            new SongItem(Color.valueOf(Color.GREEN), "spotify:track:abcdef1234567890"), // Green
            new SongItem(Color.valueOf(Color.BLUE), "spotify:track:fedcba0987654321")  // Blue
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new Config();
        client = new SpotifyClient(
                config.clientId,
                config.clientSecret,
                config.redirectUri
        );
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.buttonContainer);

        for (SongItem item : songItems) {
            Button button = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    300
            );
            params.setMargins(0, 24, 0, 24);
            button.setLayoutParams(params);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(item.color().toArgb());
            drawable.setCornerRadius(100f); // rounded corners
            button.setBackground(drawable);

            button.setText("");
            button.setOnClickListener(v -> playSong(item.spotifyUrl()));
            container.addView(button);
        }
    }

    private void playSong(String spotifyUri) {
        if (client == null) {
            Log.e("Main", "Attempted to play song but client is null!");
            return;
        }
        client.connect(this,
                () -> client.play(spotifyUri),
                () -> {
                    // Optional: show error to user
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (client != null) {
            client.disconnect();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // So getIntent() returns the new one
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri data = getIntent().getData();
        if (data != null) {
            Log.d("MainActivity", "Redirected with URI: " + data.toString());
            // Optional: handle URI here if you want to extract tokens or validate
        }
    }
}
