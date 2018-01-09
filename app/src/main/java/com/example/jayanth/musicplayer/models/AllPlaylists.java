package com.example.jayanth.musicplayer.models;

import java.util.ArrayList;

/**
 * Created by jayanth on 27/12/17.
 */

public class AllPlaylists {
    private ArrayList<Playlist> allPlaylists;

    public AllPlaylists() {
        allPlaylists = new ArrayList<>();
    }

    public ArrayList<Playlist> getAllPlaylists() {
        return allPlaylists;
    }

    public void setAllPlaylists(ArrayList<Playlist> allPlaylists) {
        this.allPlaylists = allPlaylists;
    }

    public void addPlaylist(Playlist playlist) {
        allPlaylists.add(playlist);
    }
}
