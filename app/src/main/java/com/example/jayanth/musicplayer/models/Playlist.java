package com.example.jayanth.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayanth on 27/12/17.
 */

public class Playlist implements Parcelable {
    private ArrayList<ListSong> songList;
    private String playlistName;
    private long playlistId;

    public Playlist(String playlistName, long id) {
        this.playlistName = playlistName;
        this.playlistId = id;
        songList = new ArrayList<>();
    }

    protected Playlist(Parcel in) {
        songList = in.createTypedArrayList(ListSong.CREATOR);
        playlistName = in.readString();
        playlistId = in.readLong();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public ArrayList<ListSong> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<ListSong> songList) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(songList);
        parcel.writeString(playlistName);
        parcel.writeLong(playlistId);
    }
}
