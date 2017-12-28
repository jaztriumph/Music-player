package com.example.jayanth.musicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.adapters.PlaylistRecycleAdapter;
import com.example.jayanth.musicplayer.communicator.SlidePanelCommunicator;
import com.example.jayanth.musicplayer.models.ListSong;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class PlaylistsFragment extends Fragment implements ListRecycleAdapter
        .ListRecycleAdapterOnClickHandler, PlaylistRecycleAdapter
        .PlaylistRecycleAdapterOnClickHandler {

    private SlidePanelCommunicator comm;
    RecyclerView recyclerView;
    PlaylistRecycleAdapter adapter;

    RecyclerView PlaylistRecyclerView;
    ListRecycleAdapter PlaylistAdapter;
    RelativeLayout allPlaylistLayout;
    RelativeLayout playlistLayout;
    SlidingUpPanelLayout slidingUpPanelLayout;

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm = (SlidePanelCommunicator) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allPlaylistLayout = getView().findViewById(R.id.all_playlist_layout);
        playlistLayout = getView().findViewById(R.id.playlist_layout);
        recyclerView = getView().findViewById(R.id.all_playlist_recycler_view);
        adapter = new PlaylistRecycleAdapter(getContext(), MainActivity.allPlaylists, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        ImageButton igbtn = getView().findViewById(R.id.home_playlists_btn);
        igbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allPlaylistLayout.setVisibility(View.VISIBLE);
                playlistLayout.setVisibility(View.INVISIBLE);
            }
        });

        PlaylistRecyclerView = getView().findViewById(R.id.playlist_recycler_view);
    }

    @Override
    public void onClick(ListSong song) {
        comm.onClick(song);
    }

    @Override
    public void onPlaylistClick(long id) {
        PlaylistAdapter = new ListRecycleAdapter(getContext(), MainActivity.allPlaylists
                .getAllPlaylists()
                .get((int) id)
                .getSongList(), this);
        RecyclerView.LayoutManager PlaylistLayoutManager = new LinearLayoutManager(getContext());
        PlaylistRecyclerView.setLayoutManager(PlaylistLayoutManager);
        PlaylistRecyclerView.setAdapter(PlaylistAdapter);
//        slidingUpPanelLayout = getView().findViewById(R.id
//                .playlist_sliding_layout);
        Toast.makeText(getContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
//        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        allPlaylistLayout.setVisibility(View.INVISIBLE);
        playlistLayout.setVisibility(View.VISIBLE);
    }
}
