package com.example.jayanth.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListSong implements Parcelable {
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

    protected ListSong(Parcel in) {
        songName = in.readString();
        artist = in.readString();
        albumId = in.readLong();
        id = in.readLong();
        path = in.readString();
    }

    public static final Creator<ListSong> CREATOR = new Creator<ListSong>() {
        @Override
        public ListSong createFromParcel(Parcel in) {
            return new ListSong(in);
        }

        @Override
        public ListSong[] newArray(int size) {
            return new ListSong[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(songName);
        parcel.writeString(artist);
        parcel.writeLong(albumId);
        parcel.writeLong(id);
        parcel.writeString(path);
    }
}
