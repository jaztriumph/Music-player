package com.example.jayanth.musicplayer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayanth on 27/12/17.
 */

public class Playlist {
    private List<ListSong> songList;
    private String playlistName;
    private long playlistId;

    public Playlist(String playlistName, long id) {
        this.playlistName = playlistName;
        this.playlistId = id;
        songList = new ArrayList<>();
    }

    public List<ListSong> getSongList() {
        return songList;
    }

    public void setSongList(List<ListSong> songList) {
        this.songList = songList;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setPlaylistId(long id) {
        this.playlistId = id;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void addSong(ListSong song) {
        songList.add(song);
    }

}
