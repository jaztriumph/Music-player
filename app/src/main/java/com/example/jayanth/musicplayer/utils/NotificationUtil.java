package com.example.jayanth.musicplayer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Song;
import com.example.jayanth.musicplayer.services.NotificationActionService;
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

    public static RemoteViews bigNotificationView;
    public static RemoteViews smallNotificationView;

    public static NotificationManager notificationManager;

    public static NotificationCompat.Builder notificationBuilder;
    public static Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void notifyUser(Context context, ListSong song) {
        bigNotificationView = new RemoteViews(context.getPackageName(),
                R.layout.expand_custom_notification);
        bigNotificationView.setOnClickPendingIntent(R.id.big_play_pause_btn, playPause(context));
        bigNotificationView.setOnClickPendingIntent(R.id.big_close_notification_btn, close
                (context));
        bigNotificationView.setTextViewText(R.id.notification_title, song.getSongName());
        bigNotificationView.setTextViewText(R.id.notification_text, song.getArtist());
//        bigNotificationView.setImageViewIcon();


        smallNotificationView = new RemoteViews(context.getPackageName(),
                R.layout.small_custom_notification);
        smallNotificationView.setOnClickPendingIntent(R.id.small_play_pause_btn, playPause
                (context));
        smallNotificationView.setOnClickPendingIntent(R.id.small_close_notification_btn, close
                (context));

        notificationManager = (NotificationManager) context.getSystemService
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
        notificationBuilder = new NotificationCompat.Builder(context,
                NOTIFY_USER_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.music_player_svg)
                .setContent(smallNotificationView)
                .setContentIntent(pendingIntent(context))
                .setAutoCancel(false)
                .setOngoing(true);

        //setting priority high for android versions less than jellybean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notification = notificationBuilder.build();
            Picasso.with(context).load(song.getArt()).resize(200, 200).into
                    (bigNotificationView, R.id
                            .cover_image, NOTIFY_USER_ID, notification);
//            final RemoteViews contentView = notification.contentView;
//            final int iconId = android.R.id.icon;
//            Picasso.with(context).load(song.getCoverImage()).resize(150, 150).into
//                    (contentView, iconId, NOTIFY_USER_ID, notification);
            notification.bigContentView = bigNotificationView;
            notificationManager.notify(NOTIFY_USER_ID, notification);


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


    private static PendingIntent playPause(Context context) {

        Intent playPauseIntent = new Intent(context, NotificationActionService.class);
        playPauseIntent.setAction("playPause");
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R.drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return playPausePendingIntent;
    }

    private static PendingIntent close(Context context) {

        Intent playPauseIntent = new Intent(context, NotificationActionService.class);
        playPauseIntent.setAction("close");
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R.drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return playPausePendingIntent;
    }
}
