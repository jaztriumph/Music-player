package com.example.jayanth.musicplayer.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.helper.RedirectLocation;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Song;
import com.example.jayanth.musicplayer.utils.NotificationUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

import static com.example.jayanth.musicplayer.utils.NotificationUtil.bigNotificationView;
import static com.example.jayanth.musicplayer.utils.NotificationUtil.notification;
import static com.example.jayanth.musicplayer.utils.NotificationUtil.notificationBuilder;
import static com.example.jayanth.musicplayer.utils.NotificationUtil.notificationManager;
import static com.example.jayanth.musicplayer.utils.NotificationUtil.smallNotificationView;

/**
 * Created by jayanth on 21/12/17.
 */

public class NotificationActionService extends Service {

    public SimpleExoPlayer player;
    private long playbackPosition;
    private SimpleExoPlayerView playerView;
    private int currentWindow;
    private boolean playWhenReady = true;
    private View mView;
    private ImageButton imageButton;
    private TextView songName;
    private ImageView slideCoverImage;
    private Song currentSong;
    private SimpleExoPlayer mExoPlayer;

    public class LocalBinder extends Binder {
        public NotificationActionService getService() {
            return NotificationActionService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();
    private NotificationManager mNM;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancelAll();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null && player != null) {
            if (action.equals("playPause"))
                changeIcon();
            if (action.equals("close"))
                notificationManager.cancelAll();
        }
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }


    public void initializePlayer(ListSong song, View view) {
        mView = view;
        imageButton = view.findViewById(R.id.play_btn);
        songName = view.findViewById(R.id.song_name);
        slideCoverImage = view.findViewById(R.id.slide_cover);
        songName.setText(song.getSongName());
        setPauseButton();
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri songCover = ContentUris.withAppendedId(sArtworkUri,
                song.getAlbumId());
        Picasso.with(this).load(songCover).resize(1000,1000).centerCrop().into(slideCoverImage);
        setPauseButton();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    changeIcon();
            }
        });
        playWhenReady = true;
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(), new DefaultLoadControl());


            playerView = view.findViewById(R.id.player_background_view);
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
                    trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    setPauseButton();
                } else {
                    setPlayButton();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(NotificationActionService.this, error.toString(), Toast
                        .LENGTH_SHORT)
                        .show();
                Log.i("error", error.toString());
            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        String finalUrl;
        finalUrl = "file:///" + song.getPath();
        Uri uri = Uri.parse(finalUrl);
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "Music Player"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource =
                new ExtractorMediaSource(
                        uri, dataSourceFactory, extractorsFactory, null, null);
        player.prepare(mediaSource, true, true);
        player.setPlayWhenReady(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            NotificationUtil.notifyUser(this, song);
        }

    }


//    public void initializePlayer(Song song, View view) {
////        String source = "file:///storage/sdcard0/Sounds/Music/02---Yo-Yo-Honey-Singh---Angreji" +
////                "-Beat-Ft.-Gippy.mp3";
////        Context mContext = getApplicationContext();
////        if (mExoPlayer == null) {
////            mExoPlayer =
////                    ExoPlayerFactory.newSimpleInstance(
////                            new DefaultRenderersFactory(this), new DefaultTrackSelector(), new
////                                    DefaultLoadControl());
////        }
////        mExoPlayer.setPlayWhenReady(true);
////        DataSource.Factory dataSourceFactory =
////                new DefaultDataSourceFactory(
////                        mContext, Util.getUserAgent(mContext, "Music Player"), null);
////        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
////        // The MediaSource represents the media to be played.
////        MediaSource mediaSource =
////                new ExtractorMediaSource(
////                        Uri.parse(source), dataSourceFactory, extractorsFactory, null, null);
////        mExoPlayer.prepare(mediaSource, true, true);
//
//
//
//        currentSong = song;
//        mView = view;
//        imageButton = view.findViewById(R.id.play_btn);
//        songName = view.findViewById(R.id.song_name);
//        slideCoverImage = view.findViewById(R.id.slide_cover);
//        songName.setText(song.getSong());
//        Picasso.with(this).load(song.getCoverImage()).resize(1000,1000).centerCrop().into
//                (slideCoverImage);
//        playWhenReady = true;
//        if (player != null)
//            player.setPlayWhenReady(true);
//        setPauseButton();
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (player != null)
//                    changeIcon();
//            }
//        });
//        if (player == null) {
//            player = ExoPlayerFactory.newSimpleInstance(
//                    new DefaultRenderersFactory(this),
//                    new DefaultTrackSelector(), new DefaultLoadControl());
//
//
//            playerView = view.findViewById(R.id.player_background_view);
//            playerView.setPlayer(player);
//            player.setPlayWhenReady(playWhenReady);
//            player.seekTo(currentWindow, playbackPosition);
//        }
//
//        player.addListener(new ExoPlayer.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline, Object manifest) {
//
//            }
//
//            @Override
//            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray
//                    trackSelections) {
//
//            }
//
//            @Override
//            public void onLoadingChanged(boolean isLoading) {
//
//            }
//
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                if (playWhenReady) {
//                    setPauseButton();
//                } else {
//                    setPlayButton();
//                }
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//                Toast.makeText(NotificationActionService.this, error.toString(), Toast
// .LENGTH_SHORT)
//                        .show();
//                Log.i("error", error.toString());
//            }
//
//            @Override
//            public void onPositionDiscontinuity() {
//
//            }
//
//            @Override
//            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//            }
//        });
//
//        String finalUrl;
//        try {
//            finalUrl = new RedirectLocation().execute(song.getUrl()).get();
//            Uri uri = Uri.parse(finalUrl);
//            MediaSource mediaSource = buildMediaSource(uri);
//            player.prepare(mediaSource, true, true);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            NotificationUtil.notifyUser(this, song);
//        }
//    }


//    private MediaSource buildMediaSource(Uri uri) {
//
//        return new ExtractorMediaSource(uri,
//                new DefaultHttpDataSourceFactory("exoplayer-codelab"),
//                new DefaultExtractorsFactory(), null, null);
//    }

    public void changeIcon() {
        if (playWhenReady) {
            playWhenReady = false;
            setPlayButton();
            player.setPlayWhenReady(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                bigNotificationView.setImageViewResource(R.id.big_play_pause_btn, R.drawable
                        .play_button_notification_svg);
                smallNotificationView.setImageViewResource(R.id.small_play_pause_btn, R.drawable
                        .play_button_notification_svg);
                notificationBuilder.setContent(smallNotificationView);
                notification.bigContentView = bigNotificationView;
                notificationManager.notify(987, notification);
            }

        } else {
            playWhenReady = true;
            setPauseButton();
            player.setPlayWhenReady(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                bigNotificationView.setImageViewResource(R.id.big_play_pause_btn, R.drawable
                        .pause_button_notification_svg);
                smallNotificationView.setImageViewResource(R.id.small_play_pause_btn, R.drawable
                        .pause_button_notification_svg);
                notificationBuilder.setContent(smallNotificationView);
                notification.bigContentView = bigNotificationView;
                notificationManager.notify(987, notification);
            }
        }

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    public void setPauseButton() {

        imageButton.setImageResource(R.drawable
                .pause_button_svg);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                bigNotificationView.setImageViewResource(R.id.big_play_pause_btn, R.drawable
                        .pause_button_notification_svg);
                smallNotificationView.setImageViewResource(R.id.small_play_pause_btn, R.drawable
                        .pause_button_notification_svg);
                notificationBuilder.setContent(smallNotificationView);
                notification.bigContentView = bigNotificationView;
                notificationManager.notify(987, notification);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void setPlayButton() {
        imageButton.setImageResource(R.drawable.play_button_svg);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                bigNotificationView.setImageViewResource(R.id.big_play_pause_btn, R.drawable
                        .play_button_notification_svg);
                smallNotificationView.setImageViewResource(R.id.small_play_pause_btn, R.drawable
                        .play_button_notification_svg);
                notificationBuilder.setContent(smallNotificationView);
                notification.bigContentView = bigNotificationView;
                notificationManager.notify(987, notification);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        stopSelf();
//        Toast.makeText(this, "stopped", Toast.LENGTH_SHORT).show();

    }


}
