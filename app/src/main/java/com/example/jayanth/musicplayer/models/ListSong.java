package com.example.jayanth.musicplayer.models;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListSong {
    private String songName;
    private String artist;
    private long albumId;
    private String path;

    public ListSong(String name, String artist, String path, long albumId) {
        this.artist = artist;
        this.albumId = albumId;
        this.songName = name;
        this.path = path;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getPath() {
        return path;
    }
}
