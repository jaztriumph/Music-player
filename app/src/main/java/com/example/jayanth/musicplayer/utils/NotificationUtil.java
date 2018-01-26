package com.example.jayanth.musicplayer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.services.MusicActionService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

//import android.widget.RemoteViews;

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


//    public RemoteViews bigNotificationView;
//    public RemoteViews smallNotificationView;

    public NotificationManager notificationManager;

    private NotificationCompat.Builder notificationBuilder;
    public Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notifyUser(Context context, ListSong song, boolean isPlaying) {
//        bigNotificationView = new RemoteViews(context.getPackageName(),
//                R.layout.expand_custom_notification);
//        bigNotificationView.setOnClickPendingIntent(R.id.big_play_pause_btn, playPause(context));
//        bigNotificationView.setOnClickPendingIntent(R.id.big_next_btn, playNext(context));
//        bigNotificationView.setOnClickPendingIntent(R.id.big_previous_btn, playPrevious(context));
//        bigNotificationView.setOnClickPendingIntent(R.id.big_close_notification_btn, close
//                (context));
//        bigNotificationView.setTextViewText(R.id.notification_title, song.getSongName());
//        bigNotificationView.setTextViewText(R.id.notification_text, song.getArtist());
////        bigNotificationView.setImageViewIcon();
//
//
//        smallNotificationView = new RemoteViews(context.getPackageName(),
//                R.layout.small_custom_notification);
//        smallNotificationView.setOnClickPendingIntent(R.id.small_play_pause_btn, playPause
//                (context));
//        smallNotificationView.setOnClickPendingIntent(R.id.small_next_btn, playNext(context));
//        smallNotificationView.setOnClickPendingIntent(R.id.small_previous_btn, playPrevious
//                (context));
//        smallNotificationView.setOnClickPendingIntent(R.id.small_close_notification_btn, close
//                (context));

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
        int playIcon;
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri songCover = ContentUris.withAppendedId(sArtworkUri,
                song.getAlbumId());

        if (isPlaying)
            playIcon = R.drawable.pause_button_notification_svg;
        else
            playIcon = R.drawable.play_button_notification_svg;
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(songCover.toString());
        if(bitmap==null)
        {
            bitmap= BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.music_player_svg);
        }
        //creates notification
        notificationBuilder = new NotificationCompat.Builder(context, NOTIFY_USER_CHANNEL_ID)
                .addAction(R.drawable.back_svg, "pause", playPrevious(context))
                .addAction(playIcon, "pause", playPause(context))
                .addAction(R.drawable.next_svg, "pause", playNext(context))
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.music_player_svg)
                .setLargeIcon(bitmap)
//                .setContent(smallNotificationView)
                .setContentIntent(pendingIntent(context))
//                .setCustomBigContentView(bigNotificationView)
                .setContentTitle(song.getSongName())
                .setContentText(song.getArtist())
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(isPlaying);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            notificationBuilder.addAction(R.drawable.cancel_svg,"close",close(context));
        }


        //setting priority high for android versions less than jellybean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        }

        if (notificationManager != null) {
            notification = notificationBuilder.build();
//            notification.priority;
//            setBitmapLargeIcon(songCover.toString());
//
//            DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
//                    .showImageOnFail(R.drawable.music_player_svg).build();
//            ImageLoader.getInstance().displayImage(songCover.toString(), R.id
//                    .expand_cover_image,bigNotificationView, imageOptions);

            ImageLoader.getInstance().loadImage(songCover.toString(), new
                    SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap
                                loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
//                    bigNotificationView.setImageViewBitmap(R.id
//                            .expand_cover_image, loadedImage);
//                    notificationManager.notify(NOTIFY_USER_ID, notification);
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
            notificationManager.notify(NOTIFY_USER_ID, notification);


        }
    }



    //creates a pendingIntent
    private PendingIntent pendingIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
//        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, NOTIFY_USER_PENDING_INTENT_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
    }


    private PendingIntent playNext(Context context) {

        Intent playPauseIntent = new Intent(context, MusicActionService.class);
        playPauseIntent.setAction(ACTION_PLAY_NEXT);
        //        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R
        // .drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent playPrevious(Context context) {

        Intent playPauseIntent = new Intent(context, MusicActionService.class);
        playPauseIntent.setAction(ACTION_PLAY_PREVIOUS);
        //        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R
        // .drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent playPause(Context context) {

        Intent playPauseIntent = new Intent(context, MusicActionService.class);
        playPauseIntent.setAction(ACTION_PLAY_PAUSE);
        //        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R
        // .drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent close(Context context) {

        Intent playPauseIntent = new Intent(context, MusicActionService.class);
        playPauseIntent.setAction(ACTION_CLOSE);
        //        NotificationCompat.Action pausePlayAction = new NotificationCompat.Action(R
        // .drawable
//                .pause_button_svg,"",pausePlayPendingIntent );
        return PendingIntent.getService(context, 100,
                playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
