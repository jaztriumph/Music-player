package com.example.jayanth.musicplayer.models;

import java.util.ArrayList;

/**
 * Created by jayanth on 3/1/18.
 */

public class RecentPlayed {
    private ArrayList<ListSong> recentPlayed;

    public RecentPlayed() {
        recentPlayed = new ArrayList<>();
    }

    public ArrayList<ListSong> getRecentPlayed() {
        return recentPlayed;
    }

    public void setRecentPlayed(ArrayList<ListSong> recentPlayed) {
        this.recentPlayed = recentPlayed;
    }

    public void addSong(ListSong song) {
        recentPlayed.add(0, song);
    }

}
