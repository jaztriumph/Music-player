package com.example.jayanth.musicplayer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayanth on 3/1/18.
 */

public class RecentPlayed {
    private List<ListSong> recentPlayed;

    public RecentPlayed() {
        recentPlayed = new ArrayList<>();
    }

    public List<ListSong> getRecentPlayed() {
        return recentPlayed;
    }

    public void setRecentPlayed(List<ListSong> recentPlayed) {
        this.recentPlayed = recentPlayed;
    }

    public void addSong(ListSong song) {
        recentPlayed.add(0, song);
    }

}
