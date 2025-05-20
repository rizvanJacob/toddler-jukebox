package com.example.toddlerjukebox;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

public class SongButton extends AppCompatButton {
    private final SpotifyClient client;
    private final String trackUrl;
    public SongButton(Context context, SpotifyClient client, SongItem songItem) {
        super(context);
        this.client = client;
        this.trackUrl = parseSpotifyUrl(songItem.trackId());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                300
        );
        params.setMargins(0, 24, 0, 24);
        setLayoutParams(params);

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setCornerRadius(100f);
        backgroundDrawable.setStroke(8, 0xFFB2EBF2); // Light blue border
        backgroundDrawable.setColor(0xFFFFFFFF); // White background

        setBackground(backgroundDrawable);

        setText("");
        setPadding(30, 30, 30, 30);

        int resId = context.getResources().getIdentifier(songItem.picture().replace(".png", ""), "drawable", context.getPackageName());
        if (resId != 0) {
            android.graphics.drawable.Drawable icon = context.getDrawable(resId);
            if (icon != null) {
                icon.setBounds(0, 0, 200, 200); // scale icon to desired bounds
                setCompoundDrawables(null, icon, null, null);
                setGravity(android.view.Gravity.CENTER);
            }
        }

        setOnClickListener(this::handleClick);
    }

    private void handleClick(View ignored) {
        if (client == null) {
            Log.e("Main", "Attempted to play song but client is null!");
            return;
        }
        Log.d("SongButton", "Song button was clicked!");
        client.play(trackUrl);
    }

    private String parseSpotifyUrl(String trackId) {
        return String.format("spotify:track:%s", trackId);
    }
}
