package com.example.jayanth.musicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.PlaylistView;
import com.example.jayanth.musicplayer.models.AllPlaylists;
import com.squareup.picasso.Picasso;

/**
 * Created by jayanth on 28/12/17.
 */

public class PlaylistRecycleAdapter extends RecyclerView.Adapter<PlaylistRecycleAdapter
        .PlaylistViewHolder> {

    private Context mContext;
    private AllPlaylists allPlaylists;
    private PlaylistRecycleAdapterOnClickHandler mOnclickHandler;
    public interface PlaylistRecycleAdapterOnClickHandler{
        public void onPlaylistClick(long id);
    }

    public PlaylistRecycleAdapter(Context mContext, AllPlaylists allPlaylists,
                                  PlaylistRecycleAdapterOnClickHandler mOnClickHandler) {
        this.mContext = mContext;
        this.allPlaylists = allPlaylists;
        this.mOnclickHandler=mOnClickHandler;
    }


    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_playlist_view_row, parent, false);
        return new PlaylistRecycleAdapter.PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        final int index=position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnclickHandler.onPlaylistClick(index);
            }
        });
        holder.playlistTitle.setText(allPlaylists.getAllPlaylists().get(position).getPlaylistName
                ());
        Picasso.with(mContext).load(R.drawable.music_player_pic).resize(1000,1000).centerCrop()
                .into(holder
                .playlistThumbnail);
    }

    @Override
    public int getItemCount() {
        return allPlaylists.getAllPlaylists().size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private TextView playlistTitle;

        private ImageView playlistThumbnail;
        View view;

        PlaylistViewHolder(View view) {
            super(view);
            this.view = view;
            playlistThumbnail = view.findViewById(R.id.all_playlist_thumbnail);
            playlistTitle = view.findViewById(R.id.all_playlist_title);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(mContext, "adapter", Toast.LENGTH_SHORT).show();
//            int adapterPosition = getAdapterPosition();
//            mOnClickHandler.onClick(songList.get(adapterPosition));

        }
    }
}
