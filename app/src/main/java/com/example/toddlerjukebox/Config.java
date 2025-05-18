package com.example.toddlerjukebox;

import lombok.Getter;

@Getter
public class Config {
    public final String clientId,
            clientSecret,
            redirectUri;

    public Config() {
        clientId = BuildConfig.SPOTIFY_CLIENT_ID;
        clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET;
        redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI;
    }
}
