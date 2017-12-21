package com.example.jayanth.musicplayer.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by jayanth on 21/12/17.
 */

public class NotificationActionService extends IntentService {


    public NotificationActionService() {
        super("NotificationActionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action =intent.getAction();

    }
}
