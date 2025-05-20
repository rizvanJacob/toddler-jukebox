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

    private String parseSpotifyUrl(String trackId) {
        return String.format("spotify:track:%s", trackId);
    }

    public void play(String trackId) {
        if (!isReady()) {
            return;
        }
        spotifyAppRemote.getPlayerApi().play(parseSpotifyUrl(trackId));
    }

    public void resume(){
        if (!isReady()) {
            return;
        }
        spotifyAppRemote.getPlayerApi().resume();
    }

    public void pause(){
        if (!isReady()) {
            return;
        }
        spotifyAppRemote.getPlayerApi().pause();
    }

    private boolean isReady(){
        boolean isReady = spotifyAppRemote != null && spotifyAppRemote.isConnected();
        if (!isReady) {
            Log.w(TAG, "Is not connected!");
        }
        return isReady;
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
