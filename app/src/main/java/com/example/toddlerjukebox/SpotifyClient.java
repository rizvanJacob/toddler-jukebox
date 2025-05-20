package com.example.toddlerjukebox;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Repeat;

import java.util.Optional;

public class SpotifyClient {
    private static final String TAG = "SpotifyClient";
    private final String clientId, redirectUri;
    private SpotifyAppRemote spotifyAppRemote;
    private Integer previousRepeatMode = null;

    public SpotifyClient(String clientId, String redirectUri) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;

        Log.d(TAG, String.format("Instantiated Client %s; redirects to %s", clientId, redirectUri));
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
                        spotifyAppRemote.getPlayerApi().getPlayerState()
                                .setResultCallback(playerState -> {
                                    SpotifyClient.this.previousRepeatMode = playerState.playbackOptions.repeatMode;
                                    Log.d(TAG, "Captured previous repeat mode: " + previousRepeatMode);
                                    spotifyAppRemote.getPlayerApi().setRepeat(Repeat.ONE);
                                });
                        Log.i(TAG, "Connected!");
                        Optional.ofNullable(onConnected)
                                .ifPresent(Runnable::run);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.e(TAG, "Connection failed", error);
                        Optional.ofNullable(onFailure)
                                .ifPresent(Runnable::run);
                    }
                });
    }

    public void play(String spotifyUri) {
        if (!isReady()) {
            Log.e(TAG, "Is not connected!");
            return;
        }
        spotifyAppRemote.getPlayerApi().play(spotifyUri);
    }

    public void stop(){
        if (!isReady()) {
            Log.e(TAG, "Is not connected!");
            return;
        }
        spotifyAppRemote.getPlayerApi().pause();
    }

    private boolean isReady(){
        return spotifyAppRemote != null && spotifyAppRemote.isConnected();
    }

    public void disconnect() {
        if (spotifyAppRemote != null) {
            Optional.ofNullable(previousRepeatMode)
                    .ifPresent(spotifyAppRemote.getPlayerApi()::setRepeat);
            SpotifyAppRemote.disconnect(spotifyAppRemote);
            spotifyAppRemote = null;
        }
    }
}
