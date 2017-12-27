package com.example.jayanth.musicplayer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayanth on 27/12/17.
 */

public class AllPlaylists {
    private List<Playlist> allPlaylists;

    public AllPlaylists() {
        allPlaylists = new ArrayList<>();
    }

    public List<Playlist> getAllPlaylists() {
        return allPlaylists;
    }

    public void setAllPlaylists(List<Playlist> allPlaylists) {
        this.allPlaylists = allPlaylists;
    }

    public void addPlaylist(Playlist playlist) {
        allPlaylists.add(playlist);
    }
}
