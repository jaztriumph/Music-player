package com.example.jayanth.musicplayer.adapters;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.activities.MainActivity;
import com.example.jayanth.musicplayer.fragments.PlaylistsFragment;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Playlist;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static com.example.jayanth.musicplayer.activities.MainActivity.allPlaylists;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListRecycleAdapter extends RecyclerView.Adapter<ListRecycleAdapter.MyViewHolder> {
    private Context mContext;
    private List<ListSong> songList;
    private ListRecycleAdapterOnClickHandler mOnClickHandler;
    private RecyclerView playlistDialogListRecyclerView;
    private PlaylistDialogListAdapter playlistDialogListAdapter;
    public static Dialog dialog;

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
                showPopupMenu(view, song);
            }
        });

        int px=(int) (50 * Resources.getSystem().getDisplayMetrics().density);
//        Bitmap bm = BitmapFactory.decodeFile(song.getArt());
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,
                song.getAlbumId());
//        if(isImageFile(uri.toString()))
        File file = new File(uri.getPath());
        if (file.exists()) {
            //Do something
            Log.i("path","exists");
            Picasso.with(mContext).load(uri).resize(200,200).centerCrop().into(holder
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
        }
//        Picasso.with(mContext).load(uri).resize(200,200).centerCrop().into(holder
//                        .listSideImage,
//                new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                        Picasso.with(mContext).load(R.drawable.music_player_svg).into(holder
//                                .listSideImage);
//                    }
//                });
//        holder.listSideImage.setImageBitmap(getAlbumart(song.getId()));


    }


    @Override
    public int getItemCount() {
        return songList.size();
    }


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


    private void showPopupMenu(View view, final ListSong song) {
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
                        showPlaylistDialog(song);
                        return true;
                    default:
                }
                return false;
            }
        });
        popup.show();
    }


    private void showPlaylistDialog(final ListSong song) {
        dialog = new Dialog(mContext, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.playlist_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView createPlaylistText = dialog.findViewById(R.id.create_playlist_text);


        createPlaylistText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final Dialog createPlaylistDialog = new Dialog(mContext, R.style.FullHeightDialog);
                createPlaylistDialog.getWindow().setBackgroundDrawableResource(android.R.color
                        .transparent);
                createPlaylistDialog.setContentView(R.layout.create_playlist_dialog);
                Button canclePlaylistBtn = createPlaylistDialog.findViewById(R.id
                        .cancel_playlist_btn);
                Button addPlaylistBtn = createPlaylistDialog.findViewById(R.id
                        .add_playlist_btn);
                final EditText newPlaylistText = createPlaylistDialog.findViewById(R.id
                        .new_playlist_Text);

                canclePlaylistBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createPlaylistDialog.dismiss();
                    }
                });
                addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String playlistName = newPlaylistText.getText().toString();
                        if (addNewPlaylist(playlistName, song))
                            createPlaylistDialog.dismiss();
                    }
                });
                createPlaylistDialog.show();
            }
        });


        playlistDialogListRecyclerView = dialog.findViewById(R.id.playlist_dialog_list);
        playlistDialogListAdapter = new PlaylistDialogListAdapter(mContext,
                allPlaylists, song);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        playlistDialogListRecyclerView.setLayoutManager(layoutManager);
        playlistDialogListRecyclerView.setAdapter(playlistDialogListAdapter);
        dialog.show();
    }

    private boolean addNewPlaylist(String playlistName, ListSong song) {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, playlistName);
        Uri returnUri = resolver.insert(uri, values);
        if (returnUri == null) {
            Toast.makeText(mContext, "Playlist already exists", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String[] val = returnUri.toString().split("/");
            long playlist_id = Long.parseLong(val[val.length - 1]);
            Playlist newPlaylist = new Playlist(playlistName, playlist_id);
            newPlaylist.addSong(song);

//            Uri playlistUri = MediaStore.Audio.Playlists.Members.getContentUri(
//                    "external", playlist_id);

            ContentValues newPlaylistValues = new ContentValues();
            newPlaylistValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 0);
            newPlaylistValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, song.getId());
            newPlaylistValues.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
                    playlist_id);

            Uri returnSongUri =resolver.insert(returnUri, newPlaylistValues);
            if(returnSongUri==null)
            {
                Toast.makeText(mContext, "failed", Toast.LENGTH_SHORT).show();
            }
            //updating the loaded allPlaylists and user view
            allPlaylists.addPlaylist(newPlaylist);
            PlaylistsFragment.adapter.notifyDataSetChanged();
            return true;
        }

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


}
