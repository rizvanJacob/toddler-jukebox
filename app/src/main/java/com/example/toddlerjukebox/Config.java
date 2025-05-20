package com.example.toddlerjukebox;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Config {
    public final String clientId,
            clientSecret,
            redirectUri;
    public final List<SongItem> songs;
    public Config() {
        clientId = BuildConfig.SPOTIFY_CLIENT_ID;
        clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET;
        redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI;

        songs = List.of();
    }

    private List<SongItem> loadSongs(){
        ArrayList<SongItem> songs = new ArrayList<>();
        songs.add(new SongItem("train.png", "7kM7JiA5Ak58dj6V52onnc"));
        songs.add(new SongItem("big_god.png", "6bjmxa25hThyR7MSqWMPPm"));
        songs.add(new SongItem("fish.png", "601M9QaiJ2Kmx0h0qaMwXx"));
        songs.add(new SongItem("bus.png", "6ZvjMcDpXI72hK4AnNrS6P"));
        
        return List.copyOf(songs);
    }



}
