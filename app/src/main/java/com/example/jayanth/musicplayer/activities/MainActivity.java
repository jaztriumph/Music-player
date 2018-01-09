package com.example.jayanth.musicplayer.activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.adapters.ViewPagerAdapter;
import com.example.jayanth.musicplayer.communicator.FragmentCommunicator;
import com.example.jayanth.musicplayer.fragments.PlaylistsFragment;
import com.example.jayanth.musicplayer.fragments.RecentFragment;
import com.example.jayanth.musicplayer.fragments.SongsFragment;
import com.example.jayanth.musicplayer.helper.RedirectLocation;
import com.example.jayanth.musicplayer.models.AllPlaylists;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Playlist;
import com.example.jayanth.musicplayer.models.RecentPlayed;
import com.example.jayanth.musicplayer.services.MusicActionService;
import com.example.jayanth.musicplayer.utils.Constants;
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
import com.google.gson.Gson;
import com.marcoscg.easypermissions.EasyPermissions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements FragmentCommunicator {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ImageView slideCoverImage;
    private TextView songName;
    private long playbackPosition;
    private ImageButton playPauseButton;
    public ImageButton playlistButton;
    private int currentWindow;
    private boolean playWhenReady = true;
    public ArrayList<ListSong> totalSongList;
    public AllPlaylists allPlaylists;
    public ViewPagerAdapter adapter;
    public RecentPlayed recentPlayed;
    public SharedPreferences mPrefs;

    private Gson gson;
    private MusicActionService mBoundService;
    private boolean mIsBound;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    View view;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MusicActionService.LocalBinder) service).getService();
            if (recentPlayed.getRecentPlayed().size() > 0 && !mBoundService.initialised) {
                mBoundService.initializePlayer(recentPlayed.getRecentPlayed(),
                        view, 0, false);
//                mBoundService.startForeground(recentPlayed.getRecentPlayed().get(0));
            }
            if(mBoundService.initialised)
                mBoundService.updateMainActivityUi(view);
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
    int requestCode = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        if(mBoundService==null) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService
                    (Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }

            view = this.findViewById(android.R.id.content).getRootView();
            toolbar = findViewById(R.id.toolbar);
            slideCoverImage = findViewById(R.id.slide_cover);
            songName = findViewById(R.id.song_name);
            playPauseButton = findViewById(R.id.play_btn);
            viewPager = findViewById(R.id.viewpager);
            tabLayout = findViewById(R.id.tabs);
            slidingUpPanelLayout = findViewById(R.id.sliding_layout);
            playerView = findViewById(R.id.player_background_view);
            playlistButton = findViewById(R.id.playlist_btn);
            LinearLayout slideLayout=findViewById(R.id.slide_layout);

            setSupportActionBar(toolbar);
            toolbar.setTitle("Music player");
            viewPager.setOffscreenPageLimit(2);
            tabLayout.setupWithViewPager(viewPager);
            slideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(slidingUpPanelLayout.getPanelState()==SlidingUpPanelLayout.PanelState.COLLAPSED)
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    else
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                }
            });
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
//                    if(mBoundService.playWhenReady)
//                    {
//                        mBoundService.setPauseButton();
//                    }else{
//                        mBoundService.setPlayButton();
//                    }
                        playlistButton.setVisibility(View.INVISIBLE);
                        playPauseButton.setVisibility(View.VISIBLE);


                    } else if (newState.name().equalsIgnoreCase("Expanded")) {

                        playlistButton.setVisibility(View.VISIBLE);
                        playPauseButton.setVisibility(View.INVISIBLE);
//                    playPauseButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable
//                            .pause_button_svg));
                    }

                }
            });

            mPrefs = getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
            gson = new Gson();
            recentPlayed = new RecentPlayed();
            totalSongList = new ArrayList<>();
            allPlaylists = new AllPlaylists();


            checkPerm();
        }

    }

    private void checkPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] permissions = {EasyPermissions.WRITE_EXTERNAL_STORAGE};
            EasyPermissions.requestPermissions(this, permissions, requestCode);
        } else {
            loadPlayer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.requestCode == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(EasyPermissions.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "WRITE_EXTERNAL_STORAGE Granted", Toast
                                .LENGTH_LONG).show();
                        loadPlayer();
                    }
                }
            }
        }
    }

    private void loadPlayer() {
        loadSongList();
        loadAllPlaylist();
        loadRecentPlayed();
        setupViewPager(viewPager);
    }


    private void loadRecentPlayed() {
        try {

            String json = mPrefs.getString("recentPlayed", "");
            recentPlayed = gson.fromJson(json, RecentPlayed.class);

        } catch (Exception e) {
            Log.e("preferences", e.toString());
        }
        if(recentPlayed==null)
        {
            recentPlayed = new RecentPlayed();
        }
    }

    private void loadSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int songNameColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int albumIdColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);

            do {
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                String thisSongName = musicCursor.getString(songNameColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String data = musicCursor.getString(pathColumn);
                long id = musicCursor.getLong(idColumn);
                totalSongList.add(new ListSong(thisSongName, thisArtist, data, thisAlbumId, id));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        Collections.sort(totalSongList, new Comparator<ListSong>() {
            public int compare(ListSong a, ListSong b) {
                return a.getSongName().compareTo(b.getSongName());
            }
        });

    }

    private void loadAllPlaylist() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        Cursor musicCursor = musicResolver.query(musicUri, projection, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int playlistNameColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.NAME);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._ID);

            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisPlaylistName = musicCursor.getString(playlistNameColumn);
                Playlist playlist = new Playlist(thisPlaylistName, thisId);
                Uri playlistUri = MediaStore.Audio.Playlists.Members.getContentUri(
                        "external", thisId);
                Cursor playlistCursor = musicResolver
                        .query(playlistUri, null, null, null, null);
                if (playlistCursor != null && playlistCursor.moveToFirst()) {

                    int playlistSongNameColumn = playlistCursor.getColumnIndex
                            (MediaStore.Audio.Playlists.Members.TITLE);
                    int playlistArtistNameColumn = playlistCursor.getColumnIndex
                            (MediaStore.Audio.Playlists.Members.ARTIST);
                    int playlistSongAlbumIdColumn = playlistCursor.getColumnIndex
                            (MediaStore.Audio.Playlists.Members.ALBUM_ID);
                    int playlistSongPathColumn = playlistCursor.getColumnIndex
                            (MediaStore.Audio.Playlists.Members.DATA);
                    int playlistSongAudioIdColumn = playlistCursor.getColumnIndex
                            (MediaStore.Audio.Playlists.Members.AUDIO_ID);
                    do {
                        String name = playlistCursor.getString(playlistSongNameColumn);
                        String artist = playlistCursor.getString(playlistArtistNameColumn);
                        String path = playlistCursor.getString(playlistSongPathColumn);
                        long albumId = playlistCursor.getLong(playlistSongAlbumIdColumn);
                        long audioId = playlistCursor.getLong(playlistSongAudioIdColumn);

                        ListSong song = new ListSong(name, artist, path, albumId, audioId);
                        playlist.addSong(song);

                    } while (playlistCursor.moveToNext());
                    playlistCursor.close();

                }
                allPlaylists.addPlaylist(playlist);
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

    }


    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this,
                MusicActionService.class), mConnection, Context.BIND_AUTO_CREATE);
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
        startService(new Intent(this,MusicActionService.class));

        doBindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
//        doUnbindService();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFrag(new AllSongsFragment(), "stream Songs");
        adapter.addFrag(SongsFragment.newInstance(totalSongList, allPlaylists.getAllPlaylists()), "Songs");
        adapter.addFrag(PlaylistsFragment.newInstance(allPlaylists.getAllPlaylists()), "Playlist");
        adapter.addFrag(RecentFragment.newInstance(recentPlayed.getRecentPlayed(), allPlaylists
                        .getAllPlaylists()),
                "Recent");
        viewPager.setAdapter(adapter);
    }

//    @Override
//    public void onClick(Song song) {
//        mBoundService.initializePlayer(song, this.findViewById(android.R.id.content)
// .getRootView());
////        playWhenReady = true;
////        if(player!=null)
////            player.setPlayWhenReady(true);
////        setPauseButton();
//////        toResume = song.getUrl();
////        initializePlayer(song.getUrl());
////        Toast.makeText(this, "new", Toast.LENGTH_SHORT).show();
////        Picasso.with(this).load(song.getCoverImage()).into(slideCoverImage);
////        songName.setText(song.getSong());
////
//    }


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
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }


            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

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
            setPlayButton();
        } else {
            setPauseButton();
        }

    }

    public void setPauseButton() {
        playPauseButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .pause_button_svg));
    }

    public void setPlayButton() {
        playPauseButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable
                .play_button_svg));
    }

    @Override
    public void onClickSongs(int position) {
        mBoundService.initializePlayer(totalSongList, view, position, true);
    }

    @Override
    public void onClickPlaylistSongs(int position, int index) {
        mBoundService.initializePlayer(allPlaylists.getAllPlaylists().get(index).getSongList(),
                view,
                position, true);
    }

    @Override
    public void onClickRecentSongs(int position) {
        mBoundService.initializePlayer(recentPlayed.getRecentPlayed(), view, position, true);
    }
}