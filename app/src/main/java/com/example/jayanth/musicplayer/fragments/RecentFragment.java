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
import android.widget.ListView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.adapters.ListRecycleAdapter;
import com.example.jayanth.musicplayer.adapters.SongListAdapter;
import com.example.jayanth.musicplayer.communicator.SlidePanelCommunicator;
import com.example.jayanth.musicplayer.models.ListSong;

import java.util.ArrayList;
import java.util.List;


public class RecentFragment extends Fragment implements ListRecycleAdapter
        .ListRecycleAdapterOnClickHandler {

    private RecyclerView recyclerView;
    public static ListRecycleAdapter recycleAdapter;
    private List<ListSong> recentSongList;
    private SlidePanelCommunicator comm;
    ListView listView;
    private static SongListAdapter adapter;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        recentSongList = new ArrayList<>();
        this.context = context;
        if (MainActivity.recentPlayed != null) {
            recentSongList = MainActivity.recentPlayed.getRecentPlayed();
        }
        if (recentSongList == null) {
            recentSongList = new ArrayList<>();
        }
        comm = (SlidePanelCommunicator) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getView().findViewById(R.id.recent_played_recycler_view);

        recycleAdapter = new ListRecycleAdapter(getActivity(), recentSongList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycleAdapter);

//        listView = getView().findViewById(R.id.list);
//        adapter = new SongListAdapter(MainActivity.totalSongList, getActivity());
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
//                ListSong song = MainActivity.totalSongList.get(i);
//                comm.onClick(song);
//            }
//        });
    }

    @Override
    public void onClick(int position) {

    }

//
//    @Override
//    public void onClick(List<ListSong> song) {
//        List<ListSong> oneSong = new ArrayList<>();
//        oneSong.add(song.get(0));
//        comm.onClick(oneSong);
//    }
//

}
