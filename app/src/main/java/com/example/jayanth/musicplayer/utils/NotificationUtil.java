package com.example.jayanth.musicplayer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.services.NotificationActionService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

/**
 * Created by jayanth on 20/12/17.
 */

public class NotificationUtil {
    //This pending intent id is used to uniquely reference the pending intent

    private static final int NOTIFY_USER_PENDING_INTENT_ID = 123;
    public static final int NOTIFY_USER_ID = 987;

    //This notification channel id is used to link notifications to this channel

    private static final String NOTIFY_USER_CHANNEL_ID = "user_notify_channel";
    public static final String ACTION_PLAY_PAUSE = "play_pause";
    public static final String ACTION_CLOSE = "close";
    public static final String ACTION_PLAY_NEXT = "play_next";
    public static final String ACTION_PLAY_PREVIOUS = "play_previous";


    public RemoteViews bigNotificationView;
    public RemoteViews smallNotificationView;

    public NotificationManager notificationManager;

    public NotificationCompat.Builder notificationBuilder;
    public Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyUser(Context context, ListSong song) {
        bigNotificationView = new RemoteViews(context.getPackageName(),
                R.layout.expand_custom_notification);
        bigNotificationView.setOnClickPendingIntent(R.id.big_play_pause_btn, playPause(context));
        bigNotificationView.setOnClickPendingIntent(R.id.big_next_btn, playNext(context));
        bigNotificationView.setOnClickPendingIntent(R.id.big_previous_btn, playPrevious(context));
        bigNotificationView.setOnClickPendingIntent(R.id.big_close_notification_btn, close
                (context));
        bigNotificationView.setTextViewText(R.id.notification_title, song.getSongName());
        bigNotificationView.setTextViewText(R.id.notification_text, song.getArtist());
//        bigNotificationView.setImageViewIcon();


        smallNotificationView = new RemoteViews(context.getPackageName(),
                R.layout.small_custom_notification);
        smallNotificationView.setOnClickPendingIntent(R.id.small_play_pause_btn, playPause
                (context));
        smallNotificationView.setOnClickPendingIntent(R.id.small_next_btn, playNext(context));
        smallNotificationView.setOnClickPendingIntent(R.id.small_previous_btn, playPrevious
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
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true);

        //setting priority high for android versions less than jellybean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notification = notificationBuilder.build();
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri songCover = ContentUris.withAppendedId(sArtworkUri,
                    song.getAlbumId());
//
//            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                    .showImageOnFail(R.drawable.music_player_svg).build();
//            ImageLoader.getInstance().displayImage(songCover.toString(), R.id
//                    .expand_cover_image,bigNotificationView, imageOptions);

            ImageLoader.getInstance().loadImage(songCover.toString(), new
                    SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    bigNotificationView.setImageViewBitmap(R.id
                            .expand_cover_image, loadedImage);

                }
            });



//            Picasso.with(context).load(songCover).resize(1000, 1000).into
//                    (bigNotificationView, R.id
//                            .expand_cover_image, NOTIFY_USER_ID, notification);
//
//            Picasso.with(context).load(songCover).resize(1000, 1000).centerCrop().error(R
//                    .drawable.music_player_svg)
//                    .into(smallNotificationView, R.id
//                            .small_cover_image, NOTIFY_USER_ID, notification);

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
                        .FLAG_NO_CREATE
        );
    }


    private static PendingIntent playNext(Context context) {

        Intent playPauseIntent = new Intent(context, NotificationActionService.class);
        playPauseIntent.setAction(ACTION_PLAY_NEXT);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R.drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return playPausePendingIntent;
    }

    private static PendingIntent playPrevious(Context context) {

        Intent playPauseIntent = new Intent(context, NotificationActionService.class);
        playPauseIntent.setAction(ACTION_PLAY_PREVIOUS);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R.drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return playPausePendingIntent;
    }

    private static PendingIntent playPause(Context context) {

        Intent playPauseIntent = new Intent(context, NotificationActionService.class);
        playPauseIntent.setAction(ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R.drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return playPausePendingIntent;
    }

    private static PendingIntent close(Context context) {

        Intent playPauseIntent = new Intent(context, NotificationActionService.class);
        playPauseIntent.setAction(ACTION_CLOSE);
        PendingIntent playPausePendingIntent = PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R.drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return playPausePendingIntent;
    }
}
