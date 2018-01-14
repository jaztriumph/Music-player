package com.example.jayanth.musicplayer.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.models.AllPlaylists;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.utils.NotificationUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import static com.example.jayanth.musicplayer.MusicPlayerApp.getBus;
import static com.example.jayanth.musicplayer.utils.NotificationUtil.NOTIFY_USER_ID;

/**
 * Created by jayanth on 21/12/17.
 */

public class MusicActionService extends Service implements AudioManager
        .OnAudioFocusChangeListener {

    public SimpleExoPlayer player;
    private long playbackPosition;
    private SimpleExoPlayerView playerView;
    private int currentWindow;
    public boolean playWhenReady = true;
    private View mView;
    private ImageButton imageButton;
    private ImageView exoBackground;
    private TextView songName;
    private TextView artistName;
    private ImageView slideCoverImage;
    private PlaybackControlView controls;
    private ArrayList<ListSong> songList;
    private AllPlaylists allPlaylists;
    private SeekBar musicSeekBar;
    public boolean initialised;
    private AudioFocus audioFocus;
    private Handler handler;
    private ImageButton playlistBtn;
    private boolean isPlaylistFragmentVisible = false;
    Gson gson;


    @Override
    public void onAudioFocusChange(int audioFocus) {
        switch (audioFocus) {
            case AudioManager.AUDIOFOCUS_GAIN:
                handleAudioFocusGain();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                handleAudioFocusLoss();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                handleAudioFocusDuck();
                break;

            default:
                break;
        }
    }

    private void handleAudioFocusLoss() {
        if (playWhenReady) {
            playWhenReady = false;
            player.setPlayWhenReady(false);
            this.audioFocus = AudioFocus.LOSS;
        }
    }

    private void handleAudioFocusGain() {
        if (playWhenReady) {
            player.setVolume(1);
        } else {
            if (audioFocus == AudioFocus.LOSS) {
                playWhenReady = true;
                player.setPlayWhenReady(true);
            }
        }

        this.audioFocus = AudioFocus.GAIN;
    }

    private void handleAudioFocusDuck() {
        if (playWhenReady) {
            tearDownAudioVolume();
        }
    }

    private void tearDownAudioVolume() {
        player.setVolume(0.3f);
    }


    public class LocalBinder extends Binder {
        public MusicActionService getService() {
            return MusicActionService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();
    private NotificationUtil notificationUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
//        serviceRunning = true;
        notificationUtil = new NotificationUtil();

//        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNM.cancelAll();
        gson = new Gson();
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(), new DefaultLoadControl());
        }
        player.addListener(new Player.EventListener() {
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
                updateProgressBar();
                if (playWhenReady) {
                    onPlayClicked(songList.get(currentWindow));
                } else {
                    onPauseClicked(songList.get(currentWindow));
                }

//                musicSeekBar.setMax((int) player.getDuration());
//                musicSeekBar.setProgress((int) player.getContentPosition());
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(MusicActionService.this, error.toString(), Toast
                        .LENGTH_SHORT)
                        .show();
                Log.i("error", error.toString());
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                if (reason == 2 || reason == 0) {
                    Toast.makeText(MusicActionService.this, String.valueOf(songList.size()),
                            Toast
                                    .LENGTH_SHORT)
                            .show();
                    Log.e("playerlength", String.valueOf(songList.size()));
                    int index = player.getCurrentPeriodIndex();
                    currentWindow = index;
                    ListSong song = songList.get(index);
                    updateUi(song);
                }
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
//        startForeground(NOTIFY_USER_ID, notificationUtil.notification);

    }

    private void updateProgressBar() {
        handler = new Handler();
        long duration = player == null ? 0 : player.getDuration();
        long position = player == null ? 0 : player.getCurrentPosition();
//        if (!dragging) {
        musicSeekBar.setMax((int) duration);
        musicSeekBar.setProgress((int) position);
//            musicSeekBar.setProgress(progressBarValue(position));

        // Remove scheduled updates.
        handler.removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgressBar();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
//        else{
//            Toast.makeText(this, "intent is null", Toast.LENGTH_SHORT).show();
//        }
        if (action != null && player != null) {
            if (action.equals(NotificationUtil.ACTION_PLAY_PAUSE))
                onPlayPauseClicked(songList.get(currentWindow));
            if (action.equals(NotificationUtil.ACTION_PLAY_NEXT))
                playNext();
            if (action.equals(NotificationUtil.ACTION_PLAY_PREVIOUS))
                playPrevious();

            if (action.equals(NotificationUtil.ACTION_CLOSE)) {
                stopForeground(true);
                notificationUtil.notificationManager.cancelAll();
            }
        }
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void initializePlayer(final ArrayList<ListSong> songList,
                                 View view, int
                                         position,
                                 boolean state) {
        initialised = true;
        this.songList = songList;

        currentWindow = position;
        mView = view;
        playWhenReady = state;
        musicSeekBar = view.findViewById(R.id.music_seek);
        exoBackground = view.findViewById(R.id.art_exo);
        imageButton = view.findViewById(R.id.play_btn);
        songName = view.findViewById(R.id.song_name);
        artistName = view.findViewById(R.id.artist_name);
        slideCoverImage = view.findViewById(R.id.slide_cover);
        playerView = view.findViewById(R.id.player_background_view);
        playlistBtn = view.findViewById(R.id.playlist_btn);

//        playlistBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isPlaylistFragmentVisible) {
//                    isPlaylistFragmentVisible = true;
//                    playerView.setVisibility(View.INVISIBLE);
//                } else {
//                    isPlaylistFragmentVisible = false;
//                    playerView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    onPlayPauseClicked(songList.get(currentWindow));
            }
        });
        if (playWhenReady)
            onPlayClicked(songList.get(currentWindow));

        //making seekbar not changeable
        musicSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        playerView.setPlayer(player);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        playerView.setControllerHideOnTouch(false);
        playerView.setShowShuffleButton(true);

        String finalUrl;
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "Music Player"), null);

        DynamicConcatenatingMediaSource mediaSource = new DynamicConcatenatingMediaSource();

        for (ListSong song : songList) {
            finalUrl = "file:///" + song.getPath();
            Uri uri = Uri.parse(finalUrl);
            MediaSource mediaSource1 = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            mediaSource.addMediaSource(mediaSource1);
        }
        player.prepare(mediaSource, true, true);
        player.seekTo(position, 0);
        player.setPlayWhenReady(playWhenReady);
//        playerView.setRepeatToggleModes(Player.REPEAT_MODE_ONE );
//        playerView.setRepeatToggleModes(Player.REPEAT_MODE_ALL );
//        playerView.setRepeatToggleModes(Player.REPEAT_MODE_OFF );

//        player.setRepeatMode(Player.REPEAT_MODE_ONE);


        ListSong song = songList.get(position);
        updateUi(song);
    }


    private void updateUi(ListSong song) {
        songName.setText(song.getSongName());
        artistName.setText(song.getArtist());
        final Context context = this;
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri songCover = ContentUris.withAppendedId(sArtworkUri,
                song.getAlbumId());

        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.music_player_svg).build();
        ImageLoader.getInstance().displayImage(songCover.toString(), slideCoverImage, imageOptions);

        DisplayImageOptions imageOptionsExo = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.music_background_living1).build();
        ImageLoader.getInstance().displayImage(songCover.toString(), exoBackground,
                imageOptionsExo);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationUtil.notifyUser(this, song, playWhenReady);
            startForeground(NOTIFY_USER_ID, notificationUtil.notification);

        }

        getBus().post(song);
    }


    public void updateMainActivityUi(View view) {
        mView = view;
//        playWhenReady = state;
        exoBackground = view.findViewById(R.id.art_exo);
        musicSeekBar = view.findViewById(R.id.music_seek);
        imageButton = view.findViewById(R.id.play_btn);
        songName = view.findViewById(R.id.song_name);
        slideCoverImage = view.findViewById(R.id.slide_cover);
        playerView = view.findViewById(R.id.player_background_view);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    onPlayPauseClicked(songList.get(currentWindow));
            }
        });

        if (playWhenReady)
            onPlayClicked(songList.get(currentWindow));
        else
            onPauseClicked(songList.get(currentWindow));


        playerView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        playerView.setControllerAutoShow(true);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        playerView.setControllerHideOnTouch(false);
        playerView.setShowShuffleButton(true);
        ListSong song = songList.get(currentWindow);
        updateUi(song);


    }


    private void playNext() {
        currentWindow = (currentWindow + 1) % songList.size();
        player.seekTo(currentWindow, 0);
        ListSong song = songList.get(currentWindow);
        updateUi(song);
    }

    private void playPrevious() {
        currentWindow = (currentWindow + songList.size() - 1) % songList.size();
        player.seekTo(currentWindow, 0);
        ListSong song = songList.get(currentWindow);
        updateUi(song);
    }


    public void onPlayPauseClicked(ListSong song) {
        if (playWhenReady) {
            playWhenReady = false;
            onPauseClicked(song);
            player.setPlayWhenReady(false);
        } else {
            playWhenReady = true;
            onPlayClicked(song);
            player.setPlayWhenReady(true);
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


    public void onPlayClicked(ListSong song) {
        playWhenReady = true;
        imageButton.setImageResource(R.drawable
                .pause_button_svg);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager
                    .AUDIOFOCUS_GAIN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationUtil.notifyUser(this, song, playWhenReady);
            startForeground(NOTIFY_USER_ID, notificationUtil.notification);
        }

    }

    public void onPauseClicked(ListSong song) {
        playWhenReady = false;
        imageButton.setImageResource(R.drawable.play_button_svg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationUtil.notifyUser(this, song, playWhenReady);

//            startForeground(NOTIFY_USER_ID, notificationUtil.notification);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        stopForeground(true);
    }

    private enum AudioFocus {
        GAIN, LOSS
    }


}
