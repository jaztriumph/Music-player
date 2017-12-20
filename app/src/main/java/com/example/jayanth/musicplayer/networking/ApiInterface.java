package com.example.jayanth.musicplayer.networking;

import com.example.jayanth.musicplayer.models.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by jayanth on 07-11-2017.
 */

public interface ApiInterface {
    @GET("/studio")
    Call<List<Song>> getSongs();

}
