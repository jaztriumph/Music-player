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
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.adapters.PlaylistRecycleAdapter;
import com.example.jayanth.musicplayer.communicator.FragmentCommunicator;
import com.example.jayanth.musicplayer.models.Playlist;
import com.example.jayanth.musicplayer.utils.Constants;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import static com.example.jayanth.musicplayer.MusicPlayerApp.getBus;

public class PlaylistsFragment extends Fragment implements PlaylistRecycleAdapter
        .PlaylistRecycleAdapterOnClickHandler, ListRecycleAdapter.ListRecycleAdapterOnClickHandler {

    public Bus bus;

    private FragmentCommunicator comm;
    RecyclerView recyclerView;
    public PlaylistRecycleAdapter adapter;

    RecyclerView playlistRecyclerView;
    ListRecycleAdapter playlistAdapter;
    RelativeLayout allPlaylistLayout;
    RelativeLayout playlistLayout;
    ImageButton homePlaylistBtn;
    TextView playlistNameText;
    ArrayList<Playlist> allPlaylists;
    long lastClickedPlaylist;
    SlidingUpPanelLayout slidingUpPanelLayout;

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    public static PlaylistsFragment newInstance(ArrayList<Playlist> allPlaylists) {
        PlaylistsFragment playlistsFragment = new PlaylistsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.PLAYLIST_KEY, allPlaylists);
        playlistsFragment.setArguments(bundle);
        return playlistsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm = (FragmentCommunicator) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            allPlaylists = getArguments().getParcelableArrayList(Constants.PLAYLIST_KEY);
        }
        bus = getBus();
        bus.register(this);
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
        adapter = new PlaylistRecycleAdapter(getContext(), allPlaylists, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        playlistRecyclerView = getView().findViewById(R.id.playlist_recycler_view);
    }

//    @Override
//    public void onClick(List<ListSong> song) {
//        comm.onClick(song);
//    }

    @Subscribe
    public void updatedDataset(String s) {
        if (s.equals(Constants.UPDATE_PLAYlIST_KEY)) {
            if (adapter != null)
                adapter.notifyDataSetChanged();

            if (playlistAdapter != null)
                playlistAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "bus called", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(int position) {
        comm.onClickPlaylistSongs(position, (int) lastClickedPlaylist);
    }

    @Override
    public void onPlaylistClick(long id) {
        lastClickedPlaylist = id;
        playlistAdapter = new ListRecycleAdapter(getContext(), allPlaylists
                .get((int) id)
                .getSongList(), allPlaylists, this);
        RecyclerView.LayoutManager playlistLayoutManager = new LinearLayoutManager(getContext());
        playlistRecyclerView.setLayoutManager(playlistLayoutManager);
        playlistRecyclerView.setAdapter(playlistAdapter);
//        Toast.makeText(getContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
        playlistNameText.setText(allPlaylists
                .get((int) id).getPlaylistName());
        allPlaylistLayout.setVisibility(View.INVISIBLE);
        playlistLayout.setVisibility(View.VISIBLE);
    }
}
