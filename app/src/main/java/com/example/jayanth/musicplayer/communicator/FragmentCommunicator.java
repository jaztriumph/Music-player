package com.example.jayanth.musicplayer.communicator;

import com.example.jayanth.musicplayer.models.ListSong;

import java.util.List;

/**
 * Created by jayanth on 18/12/17.
 */

public interface FragmentCommunicator {

    void onClickSongs(int position);
    void onClickPlaylistSongs(int position,int playlistIndex);
    void onClickRecentSongs(int position);
}
