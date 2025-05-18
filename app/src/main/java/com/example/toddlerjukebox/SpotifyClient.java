package com.example.toddlerjukebox;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpotifyClient {
    private final String clientId, clientSecret, redirectUri;
    private SpotifyAppRemote spotifyAppRemote;

    public void connect(Context context, Runnable onConnected, Runnable onFailure) {
        if (spotifyAppRemote != null && spotifyAppRemote.isConnected()) {
            onConnected.run();
            return;
        }

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(clientId)
                        .setRedirectUri(redirectUri)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote remote) {
                        spotifyAppRemote = remote;
                        Log.d("SpotifyClient", "Connected!");
                        onConnected.run();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.e("SpotifyClient", "Connection failed", error);
                        onFailure.run();
                    }
                });
    }

    public void play(String spotifyUri) {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().play(spotifyUri);
        }
    }

    public void disconnect() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
            spotifyAppRemote = null;
        }
    }
}
