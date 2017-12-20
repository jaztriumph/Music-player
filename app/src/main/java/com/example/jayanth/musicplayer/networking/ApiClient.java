package com.example.jayanth.musicplayer.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jayanth on 07-11-2017.
 */

public class ApiClient {
    public static String BASE_URL = "http://starlord.hackerearth.com/";
    public static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
}
