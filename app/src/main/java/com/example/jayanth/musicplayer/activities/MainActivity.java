package com.example.jayanth.musicplayer.activities;

import android.net.Uri;
import android.os.Bundle;
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
import com.example.jayanth.musicplayer.fragments.DownloadsFragment;
import com.example.jayanth.musicplayer.fragments.PlaylistsFragment;
import com.example.jayanth.musicplayer.fragments.RecentFragment;
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
import com.google.android.exoplayer2.util.Util;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SlidePanelCommunicator{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ImageView slideCoverImage;
    private TextView songName;
    private long playbackPosition;
    private ImageButton imageButton;
    private int currentWindow;
    private boolean playWhenReady = true;
    private String toResume=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);

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




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AllSongsFragment(), "All Songs");
        adapter.addFrag(new DownloadsFragment(), "Downloads");
        adapter.addFrag(new PlaylistsFragment(), "Playlist");
        adapter.addFrag(new RecentFragment(), "Recent");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(Song song) {
        playWhenReady = true;
        setPauseButton();
        toResume=song.getUrl();
        initializePlayer(song.getUrl());
        Toast.makeText(this, "new", Toast.LENGTH_SHORT).show();
        Picasso.with(this).load(song.getCoverImage()).into(slideCoverImage);
        songName.setText(song.getSong());
        NotificationUtil.notifyUser(this,song);
    }



    private void initializePlayer(String url) {
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
                if(playWhenReady){
                    setPauseButton();
                }
                else{
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
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

    public void onClickPlay(View view) {
        if(player!=null)
        changeIcon();
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
    public void setPauseButton(){
        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .pause_button_svg));
    }

    public void setPlayButton(){
        imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .play_button_svg));
    }
}