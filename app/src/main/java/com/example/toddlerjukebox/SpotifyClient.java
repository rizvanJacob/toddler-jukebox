package com.example.toddlerjukebox;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyClient {
    private final String clientId, clientSecret, redirectUri;
    private SpotifyAppRemote spotifyAppRemote;

    public SpotifyClient(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

        Log.d("SpotifyClient", String.format("Instantiated Client %s; redirects to %s", clientId, redirectUri));
    }

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
        if (spotifyAppRemote == null) {
            Log.e("SpotifyClient", "Is not connected!");
            return;
        }
        spotifyAppRemote.getPlayerApi().play(spotifyUri);
    }

    public void disconnect() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
            spotifyAppRemote = null;
        }
    }
}
