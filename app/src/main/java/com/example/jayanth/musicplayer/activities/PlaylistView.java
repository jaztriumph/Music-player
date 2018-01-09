package com.example.jayanth.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.services.NotificationActionService;

public class PlaylistView extends AppCompatActivity implements ListRecycleAdapter
        .ListRecycleAdapterOnClickHandler {

    RecyclerView recyclerView;
    ListRecycleAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_view);
        recyclerView = findViewById(R.id.playlist_recycler_view);
//        adapter = new ListRecycleAdapter(this, MainActivity.allPlaylists.getAllPlaylists().get(0)
//                .getSongList(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
            Toast.makeText(PlaylistView.this, "stopped",
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(PlaylistView.this,
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

//    @Override
//    public void onClick(List<ListSong> song) {
//        MainActivity ac=(MainActivity)getApplicationContext();
//        mBoundService.initializePlayer(song, MainActivity.vw);
//    }

    @Override
    public void onClick(int position) {

    }
}
