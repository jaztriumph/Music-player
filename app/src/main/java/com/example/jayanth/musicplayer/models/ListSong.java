package com.example.jayanth.musicplayer.models;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListSong {
    private String songName;
    private String artist;
    private long id;
    private String art;

    public ListSong(String name, String artist, String art, long id) {
        this.artist = artist;
        this.id = id;
        this.songName = name;
        this.art = art;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public long getId() {
        return id;
    }

    public String getArt() {
        return art;
    }
}
