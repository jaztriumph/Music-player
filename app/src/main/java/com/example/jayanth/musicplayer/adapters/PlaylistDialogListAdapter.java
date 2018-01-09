package com.example.jayanth.musicplayer.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Playlist;
import com.example.jayanth.musicplayer.utils.Constants;

import java.util.ArrayList;

import static com.example.jayanth.musicplayer.MusicPlayerApp.getBus;

/**
 * Created by jayanth on 29/12/17.
 */

public class PlaylistDialogListAdapter extends RecyclerView.Adapter<PlaylistDialogListAdapter
        .MyViewHolder> {

    private Context mContext;
    private ListSong song;
    private ArrayList<Playlist> allPlaylists;

    public PlaylistDialogListAdapter(Context context, ArrayList<Playlist> allPlaylists, ListSong
            song) {
        this.mContext = context;
        this.allPlaylists = allPlaylists;
        this.song = song;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .playlist_dialog_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        holder.playlistName.setText(allPlaylists.get(position).getPlaylistName());

        //on click adding song to playlist
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long playlist_id = allPlaylists.get(pos).getPlaylistId();

                Uri newUri = MediaStore.Audio.Playlists.Members.getContentUri(
                        "external", playlist_id);
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues values = new ContentValues();

                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 0);
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.getId());
                values.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
                        playlist_id);

                resolver.insert(newUri, values);

                //updating the loaded allPlaylists and user view
                allPlaylists.get(pos).addSong(song);
                ListRecycleAdapter.dialog.dismiss();
                getBus().post(Constants.UPDATE_PLAYlIST_KEY);
//                MainActivity.adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return allPlaylists.size();
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
