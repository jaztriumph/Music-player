package com.example.jayanth.musicplayer.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.models.Playlist;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by jayanth on 28/12/17.
 */

public class PlaylistRecycleAdapter extends RecyclerView.Adapter<PlaylistRecycleAdapter
        .PlaylistViewHolder> {

    private Context mContext;
    private ArrayList<Playlist> allPlaylists;
    private PlaylistRecycleAdapterOnClickHandler mOnclickHandler;

    public interface PlaylistRecycleAdapterOnClickHandler {
        void onPlaylistClick(long id);
    }

    public PlaylistRecycleAdapter(Context mContext, ArrayList<Playlist> allPlaylists,
                                  PlaylistRecycleAdapterOnClickHandler mOnClickHandler) {
        this.mContext = mContext;
        this.allPlaylists = allPlaylists;
        this.mOnclickHandler = mOnClickHandler;
    }


    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_playlist_view_row, parent, false);
        return new PlaylistRecycleAdapter.PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, int position) {
        final int index = position;
        final long playlist_id = allPlaylists.get(index).getPlaylistId();
        final String playlist_Name = allPlaylists.get(index).getPlaylistName();
        long albumId = allPlaylists.get(index).
                getSongList().get(0).getAlbumId();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnclickHandler.onPlaylistClick(index);
            }
        });
        holder.overflowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, playlist_id, playlist_Name, index);
            }
        });
        holder.playlistTitle.setText(playlist_Name);

        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,
                albumId);

//        Picasso.with(mContext).load(uri).fit().centerCrop()
//                .error(R.drawable.music_player_svg)
//                .into(holder.playlistThumbnail);


        ImageLoader.getInstance().cancelDisplayTask(holder.playlistThumbnail);
        holder.playlistThumbnail.setImageResource(R.drawable.music_player_svg);
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.music_player_svg).build();
        ImageLoader.getInstance().displayImage(uri.toString(), holder.playlistThumbnail,
                imageOptions);


    }

    @Override
    public int getItemCount() {
        return allPlaylists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private TextView playlistTitle;
        private ImageButton overflowBtn;
        private ImageView playlistThumbnail;
        View view;

        PlaylistViewHolder(View view) {
            super(view);
            this.view = view;
            overflowBtn = view.findViewById(R.id.all_playlist_overflow);
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

    private void showPopupMenu(View view, final long playlistId, final String playlistName, final
    int index) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.all_playlist_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_playlist:
                        deletePlaylist(playlistId, playlistName, index);
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }

    private void deletePlaylist(long playlistId, String playlistName, int index) {
        ContentResolver resolver = mContext.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {String.valueOf(playlistId)};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
        Toast toast = Toast.makeText(mContext, playlistName + " Deleted", Toast.LENGTH_SHORT);
        toast.show();
        allPlaylists.remove(index);
        this.notifyDataSetChanged();
    }


}
