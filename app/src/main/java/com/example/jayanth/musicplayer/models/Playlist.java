package com.example.jayanth.musicplayer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayanth on 27/12/17.
 */

public class Playlist {
    private List<ListSong> songList;
    private String playlistName;

   public Playlist(String playlistName) {
        this.playlistName = playlistName;
        songList=new ArrayList<>();
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

    public String getPlaylistName() {
        return playlistName;
    }

    public void addSong(ListSong song) {
        songList.add(song);
    }

}
