package com.example.jayanth.musicplayer.adapters;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.models.ListSong;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListRecycleAdapter extends RecyclerView.Adapter<ListRecycleAdapter.MyViewHolder> {
    private Context mContext;
    private List<ListSong> songList;
    private ListRecycleAdapterOnClickHandler mOnClickHandler;
    private RecyclerView playlistDialogListRecyclerView;
    private PlaylistDialogListAdapter playlistDialogListAdapter;
    public interface ListRecycleAdapterOnClickHandler {
        void onClick(ListSong song);
    }


    public ListRecycleAdapter(Context mContext, List<ListSong> songList, ListRecycleAdapter
            .ListRecycleAdapterOnClickHandler
            mOnClickHandler) {
        this.mContext = mContext;
        this.songList = songList;
        this.mOnClickHandler = mOnClickHandler;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_list_view_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ListSong song = songList.get(position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickHandler.onClick(song);
            }
        });
        holder.listArtist.setText(song.getArtist());
        holder.listSong.setText(song.getSongName());
        holder.songListOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });


//        Bitmap bm = BitmapFactory.decodeFile(song.getArt());
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,
                song.getAlbumId());
//        if(isImageFile(uri.toString()))
        Picasso.with(mContext).load(uri).resize(1000, 1000).centerCrop().into(holder
                        .listSideImage,
                new Callback() {
                    @Override
                    public void onSuccess() {


                    }

                    @Override
                    public void onError() {

                        Picasso.with(mContext).load(R.drawable.music_player_svg).into(holder
                                .listSideImage);
                    }
                });
//        holder.listSideImage.setImageBitmap(getAlbumart(song.getId()));


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.song_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_favourite:
                        Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_play_next:
                        Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_add_to_playlist:
//                        Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                        showPlaylistDialog();
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }

    private void showPlaylistDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.FullHeightDialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.playlist_dialog);
//        dialog.setTitle("Add to Playlist");
        playlistDialogListRecyclerView=dialog.findViewById(R.id.playlist_dialog_list);
        playlistDialogListAdapter = new PlaylistDialogListAdapter(mContext, MainActivity.allPlaylists);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(mContext);
        playlistDialogListRecyclerView.setLayoutManager(layoutManager);
        playlistDialogListRecyclerView.setAdapter(playlistDialogListAdapter);
        dialog.show();
    }

    private static boolean isImageFile(String path) {
        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType != null && mimeType.startsWith("image");
    }

//    public Bitmap getAlbumart(Long album_id) {
//        Bitmap bm = null;
//        try {
//            final Uri sArtworkUri = Uri
//                    .parse("content://media/external/audio/albumart");
//
//            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
//
//            ParcelFileDescriptor pfd = mContext.getContentResolver()
//                    .openFileDescriptor(uri, "r");
//
//            if (pfd != null) {
//                FileDescriptor fd = pfd.getFileDescriptor();
//                bm = BitmapFactory.decodeFileDescriptor(fd);
//            }
//        } catch (Exception e) {
//        }
//        return bm;
//    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView listSong, listArtist;

        private ImageView listSideImage;
        private ImageButton songListOverflow;
        View view;

        MyViewHolder(View view) {
            super(view);
            this.view = view;
            listSong = view.findViewById(R.id.list_song_name);
            listArtist = view.findViewById(R.id.list_artist_name);
            songListOverflow = view.findViewById(R.id.song_list_overflow);
            listSideImage = view.findViewById(R.id.list_side_image);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(mContext, "adapter", Toast.LENGTH_SHORT).show();
//            int adapterPosition = getAdapterPosition();
//            mOnClickHandler.onClick(songList.get(adapterPosition));

        }
    }
}
