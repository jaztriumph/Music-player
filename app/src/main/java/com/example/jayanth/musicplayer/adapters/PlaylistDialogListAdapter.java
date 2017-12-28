package com.example.jayanth.musicplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.models.AllPlaylists;

/**
 * Created by jayanth on 29/12/17.
 */

public class PlaylistDialogListAdapter extends RecyclerView.Adapter<PlaylistDialogListAdapter
        .MyViewHolder> {

    private Context mContext;
    private AllPlaylists allPlaylists;

    public PlaylistDialogListAdapter(Context context, AllPlaylists allPlaylists) {
        this.mContext = context;
        this.allPlaylists = allPlaylists;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .playlist_dialog_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.playlistName.setText(allPlaylists.getAllPlaylists().get(position).getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return allPlaylists.getAllPlaylists().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView playlistName;
        View view;

        private MyViewHolder(View view) {
            super(view);
            this.view = view;
            playlistName = view.findViewById(R.id.playlist_dialog_Playlist_name);

        }

    }


}
