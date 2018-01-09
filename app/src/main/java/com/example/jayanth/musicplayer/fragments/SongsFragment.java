package com.example.jayanth.musicplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.communicator.FragmentCommunicator;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Playlist;
import com.example.jayanth.musicplayer.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class SongsFragment extends Fragment implements ListRecycleAdapter
        .ListRecycleAdapterOnClickHandler {

    private RecyclerView recyclerView;
    private ListRecycleAdapter recycleAdapter;
    private List<ListSong> songList;
    private FragmentCommunicator comm;
    private ArrayList<Playlist> allPlaylists;

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance(ArrayList<ListSong> songs, ArrayList<Playlist>
            allPlaylists) {
        SongsFragment songsFragment = new SongsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.SONGS_LIST_KEY, songs);
        bundle.putParcelableArrayList(Constants.PLAYLIST_KEY, allPlaylists);
        songsFragment.setArguments(bundle);
        return songsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songList = getArguments().getParcelableArrayList(Constants.SONGS_LIST_KEY);
            allPlaylists = getArguments().getParcelableArrayList(Constants.PLAYLIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        songList = new ArrayList<>();
        comm = (FragmentCommunicator) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //noinspection ConstantConditions
        recyclerView = getView().findViewById(R.id.list_recycler_view);

        recycleAdapter = new ListRecycleAdapter(getActivity(), songList, allPlaylists, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycleAdapter);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(int position) {
        comm.onClickSongs(position);
//        Log.i("song path", song.getPath());
//        Toast.makeText(getContext(), song.getArt(), Toast.LENGTH_SHORT).show();
    }
}
