package com.example.jayanth.musicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.helper.RedirectLocation;
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
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

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

    public class LocalBinder extends Binder {
        public NotificationActionService getService() {
            return NotificationActionService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action=intent.getAction();
        if(action!=null && player!=null && action.equals("first")){
            changeIcon();
        }
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }



    public void initializePlayer(Song song, View view) {
        mView = view;
        imageButton = view.findViewById(R.id.play_btn);
        songName=view.findViewById(R.id.song_name);
        slideCoverImage=view.findViewById(R.id.slide_cover);
        songName.setText(song.getSong());
        Picasso.with(this).load(song.getCoverImage()).into(slideCoverImage);
        playWhenReady = true;
        if(player!=null)
            player.setPlayWhenReady(true);
        setPauseButton();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    changeIcon();
            }
        });
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

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

        String finalUrl;
        try {
            finalUrl = new RedirectLocation().execute(song.getUrl()).get();
            Uri uri = Uri.parse(finalUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            NotificationUtil.notifyUser(this, song);
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("exoplayer-codelab"),
                new DefaultExtractorsFactory(), null, null);
    }

    public void changeIcon() {
        if (playWhenReady) {
            playWhenReady = false;
            player.setPlayWhenReady(false);
            setPlayButton();
        } else {
            playWhenReady = true;
            player.setPlayWhenReady(true);
            setPauseButton();
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

        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .pause_button_svg));
    }

    public void setPlayButton() {
        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .play_button_svg));
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "stopped", Toast.LENGTH_SHORT).show();

    }


    private final IBinder mBinder = new LocalBinder();
}
