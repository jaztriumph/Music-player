package com.example.jayanth.musicplayer.models;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListSong {
    private String songName;
    private String artist;
    private long albumId;
    private long id;

    private String path;

    public ListSong(String name, String artist, String path, long albumId, long id) {
        this.artist = artist;
        this.albumId = albumId;
        this.songName = name;
        this.path = path;
        this.id = id;
    }

    public long getId() {
        return id;
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
