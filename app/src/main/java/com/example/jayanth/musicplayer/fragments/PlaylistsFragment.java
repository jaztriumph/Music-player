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
import android.widget.TextView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.adapters.PlaylistRecycleAdapter;
import com.example.jayanth.musicplayer.communicator.SlidePanelCommunicator;
import com.example.jayanth.musicplayer.models.ListSong;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public class PlaylistsFragment extends Fragment implements PlaylistRecycleAdapter
        .PlaylistRecycleAdapterOnClickHandler, ListRecycleAdapter.ListRecycleAdapterOnClickHandler {

    private SlidePanelCommunicator comm;
    RecyclerView recyclerView;
    public static PlaylistRecycleAdapter adapter;

    RecyclerView PlaylistRecyclerView;
    ListRecycleAdapter PlaylistAdapter;
    RelativeLayout allPlaylistLayout;
    RelativeLayout playlistLayout;
    ImageButton homePlaylistBtn;
    TextView playlistNameText;
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
        playlistNameText = getView().findViewById(R.id.playlist_name_text);

        homePlaylistBtn = getView().findViewById(R.id.home_playlists_btn);
        homePlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allPlaylistLayout.setVisibility(View.VISIBLE);
                playlistLayout.setVisibility(View.INVISIBLE);
            }
        });

        recyclerView = getView().findViewById(R.id.all_playlist_recycler_view);
        adapter = new PlaylistRecycleAdapter(getContext(), MainActivity.allPlaylists, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        PlaylistRecyclerView = getView().findViewById(R.id.playlist_recycler_view);
    }

//    @Override
//    public void onClick(List<ListSong> song) {
//        comm.onClick(song);
//    }

    @Override
    public void onClick(int position) {

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
//        Toast.makeText(getContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
        playlistNameText.setText(MainActivity.allPlaylists
                .getAllPlaylists()
                .get((int) id).getPlaylistName());
        allPlaylistLayout.setVisibility(View.INVISIBLE);
        playlistLayout.setVisibility(View.VISIBLE);
    }
}
