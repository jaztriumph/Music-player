package com.example.jayanth.musicplayer.activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.adapters.ViewPagerAdapter;
import com.example.jayanth.musicplayer.communicator.SlidePanelCommunicator;
import com.example.jayanth.musicplayer.fragments.AllSongsFragment;
import com.example.jayanth.musicplayer.fragments.PlaylistsFragment;
import com.example.jayanth.musicplayer.fragments.RecentFragment;
import com.example.jayanth.musicplayer.fragments.SongsFragment;
import com.example.jayanth.musicplayer.helper.RedirectLocation;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Song;
import com.example.jayanth.musicplayer.services.NotificationActionService;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SlidePanelCommunicator {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ImageView slideCoverImage;
    private TextView songName;
    private long playbackPosition;
    private ImageButton imageButton;
    private int currentWindow;
    private boolean playWhenReady = true;
    public static List<ListSong> totalSongList;
//    private String toResume = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
//        startService(new Intent(this,NotificationActionService.class));


//        NotificationManager notificationManager = (NotificationManager) this.getSystemService
//                (Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
        totalSongList = new ArrayList<>();
        loadSongList();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        slideCoverImage = findViewById(R.id.slide_cover);
        songName = findViewById(R.id.song_name);
        imageButton = findViewById(R.id.play_btn);
        toolbar.setTitle("Music player");

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        playerView = findViewById(R.id.player_background_view);
        SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState
                    previousState, SlidingUpPanelLayout.PanelState newState) {

                //  Toast.makeText(getApplicationContext(),newState.name().toString(),Toast
                // .LENGTH_SHORT).show();
                if (newState.name().equalsIgnoreCase("Collapsed")) {

                    //action when collapsed

                } else if (newState.name().equalsIgnoreCase("Expanded")) {

                }

            }
        });

    }

    private void loadSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int songNameColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int dataColumn=musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
//            int artColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.p);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisSongName = musicCursor.getString(songNameColumn);
                String thisArtist = musicCursor.getString(artistColumn);
//                String art = musicCursor.getString(artColumn);
                String art="";
                String data=musicCursor.getString(dataColumn);
                totalSongList.add(new ListSong(thisSongName, thisArtist, data, thisId));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

    }

    private NotificationActionService mBoundService;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((NotificationActionService.LocalBinder) service).getService();

            // Tell the user about this for our demo.
//            Toast.makeText(MainActivity.this,"started",
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            Toast.makeText(MainActivity.this, "stopped",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this,
                NotificationActionService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        startService(new Intent(this,NotificationActionService.class));

        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
        doUnbindService();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AllSongsFragment(), "stream Songs");
        adapter.addFrag(new SongsFragment(), "Songs");
        adapter.addFrag(new PlaylistsFragment(), "Playlist");
        adapter.addFrag(new RecentFragment(), "Recent");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(Song song) {
        mBoundService.initializePlayer(song, this.findViewById(android.R.id.content).getRootView());
//        playWhenReady = true;
//        if(player!=null)
//            player.setPlayWhenReady(true);
//        setPauseButton();
////        toResume = song.getUrl();
//        initializePlayer(song.getUrl());
//        Toast.makeText(this, "new", Toast.LENGTH_SHORT).show();
//        Picasso.with(this).load(song.getCoverImage()).into(slideCoverImage);
//        songName.setText(song.getSong());
//
    }

    @Override
    public void onClick(ListSong song) {
        mBoundService.initializePlayer(song, this.findViewById(android.R.id.content).getRootView());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Toast.makeText(this, "backPressed", Toast.LENGTH_SHORT).show();
        moveTaskToBack(true);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return true;
//    }

    private void initializePlayer(String url) {
//        toResume = url;
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(), new DefaultLoadControl());

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
            finalUrl = new RedirectLocation().execute(url).get();
            Uri uri = Uri.parse(finalUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("exoplayer-codelab"),
                new DefaultExtractorsFactory(), null, null);
    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23) {
////            releasePlayer();
//        }
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
////        hideSystemUi();
//        if ((Util.SDK_INT <= 23 || player == null)&&(toResume!=null)) {
////            initializePlayer(toResume);
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23) {
////            releasePlayer();
//        }
//    }


    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
//
//    public void onClickPlay(View view) {
//        if (player != null)
//            changeIcon();
//    }

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

    public void setPauseButton() {
        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .pause_button_svg));
    }

    public void setPlayButton() {
        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .play_button_svg));
    }
}