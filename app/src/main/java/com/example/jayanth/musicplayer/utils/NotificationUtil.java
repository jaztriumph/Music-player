package com.example.jayanth.musicplayer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.models.Song;
import com.squareup.picasso.Picasso;

/**
 * Created by jayanth on 20/12/17.
 */

public class NotificationUtil {
    //This pending intent id is used to uniquely reference the pending intent

    private static final int NOTIFY_USER_PENDING_INTENT_ID = 123;
    private static final int NOTIFY_USER_ID = 987;

    //This notification channel id is used to link notifications to this channel

    private static final String NOTIFY_USER_CHANNEL_ID = "user_notify_channel";

    public static void notifyUser(Context context,Song song) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);

        //check for android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFY_USER_CHANNEL_ID,
                    "music notification", NotificationManager
                    .IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        //creates notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,
                NOTIFY_USER_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.music_player_svg)
                .setContentTitle(song.getSong())
                .setContentText(song.getArtists())
                .setContentIntent(pendingIntent(context))
                .setAutoCancel(false);

        //setting priority high for android versions less than jellybean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notificationManager.notify(NOTIFY_USER_ID, notificationBuilder.build());
        }

    }

    //creates a pendingIntent
    private static PendingIntent pendingIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, NOTIFY_USER_PENDING_INTENT_ID,
                startActivityIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT
        );
    }
}
