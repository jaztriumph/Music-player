package com.example.jayanth.musicplayer;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.otto.Bus;

/**
 * Created by jayanth on 5/1/18.
 */

public class MusicPlayerApp extends Application {
    private static Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
//        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                .cacheInMemory(true)
//                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .defaultDisplayImageOptions(imageOptions)
                .build();
        ImageLoader.getInstance().init(config);

    }

    public static Bus getBus() {
        if (bus == null)
            bus = new Bus();
        return bus;
    }

}
