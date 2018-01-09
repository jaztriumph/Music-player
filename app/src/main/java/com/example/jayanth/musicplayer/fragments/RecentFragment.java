package com.example.jayanth.musicplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.communicator.FragmentCommunicator;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Playlist;
import com.example.jayanth.musicplayer.models.RecentPlayed;
import com.example.jayanth.musicplayer.utils.Constants;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.jayanth.musicplayer.MusicPlayerApp.getBus;


public class RecentFragment extends Fragment implements ListRecycleAdapter
        .ListRecycleAdapterOnClickHandler {

    public Bus bus;
    private RecyclerView recyclerView;
    public ListRecycleAdapter recycleAdapter;
    private ArrayList<ListSong> recentSongList;
    private ArrayList<Playlist> allPlaylists;
    private FragmentCommunicator comm;
    Context context;
    public SharedPreferences mPrefs;
    public SharedPreferences.Editor prefsEditor;
    private Gson gson;

    public RecentFragment() {
        // Required empty public constructor
    }

    public static RecentFragment newInstance(ArrayList<ListSong> songs, ArrayList<Playlist>
            allPlaylists) {
        RecentFragment recentFragment = new RecentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.SONGS_LIST_KEY, songs);
        bundle.putParcelableArrayList(Constants.PLAYLIST_KEY, allPlaylists);
        recentFragment.setArguments(bundle);
        return recentFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        comm = (FragmentCommunicator) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recentSongList = getArguments().getParcelableArrayList(Constants.SONGS_LIST_KEY);
            allPlaylists = getArguments().getParcelableArrayList(Constants.PLAYLIST_KEY);
        }
        if (recentSongList == null) {
            recentSongList = new ArrayList<>();
        }
        gson = new Gson();
        bus = getBus();
        bus.register(this);
        mPrefs = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        prefsEditor = mPrefs.edit();

        String json = mPrefs.getString("recentPlayed", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getView().findViewById(R.id.recent_played_recycler_view);

        recycleAdapter = new ListRecycleAdapter(getActivity(), recentSongList, allPlaylists, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycleAdapter);

    }


    @Override
    public void onClick(int position) {
        comm.onClickRecentSongs(position);
    }


    @Subscribe
    public void updateDataset(ListSong song) {
        if (recentSongList == null) {
            recentSongList = new ArrayList<>();
        }
        recentSongList.add(0,song);
        if(recentSongList.size()>500)
        {
            recentSongList.remove(recentSongList.size()-1);
        }
        recycleAdapter.notifyDataSetChanged();
        RecentPlayed tempRecent = new RecentPlayed();
        tempRecent.setRecentPlayed(recentSongList);
        String json = gson.toJson(tempRecent);
        prefsEditor.putString("recentPlayed", json);
        prefsEditor.apply();
//        Toast.makeText(context, "recent", Toast.LENGTH_SHORT).show();

    }


}
