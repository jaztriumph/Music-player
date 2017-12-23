package com.example.jayanth.musicplayer.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.models.ListSong;
import com.example.jayanth.musicplayer.models.Song;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by jayanth on 23/12/17.
 */

public class ListRecycleAdapter extends RecyclerView.Adapter<ListRecycleAdapter.MyViewHolder> {
    private Context mContext;
    private List<ListSong> songList;
    final private ListRecycleAdapter.ListRecycleAdapterOnClickHandler mOnClickHandler;

    public interface ListRecycleAdapterOnClickHandler {
        void onClick(Song song);
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ListSong song = songList.get(position);
        holder.listArtist.setText(song.getArtist());
        holder.listSong.setText(song.getSongName());
//        Bitmap bm = BitmapFactory.decodeFile(song.getArt());
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,
                song.getId());
//        if(isImageFile(uri.toString()))
        Picasso.with(mContext).load(uri).into(holder.listSideImage);
//        holder.listSideImage.setImageBitmap(getAlbumart(song.getId()));


    }

    @Override
    public int getItemCount() {
        return songList.size();
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
        View view;

        MyViewHolder(View view) {
            super(view);
            this.view = view;
            listSong = view.findViewById(R.id.list_song_name);
            listArtist = view.findViewById(R.id.list_artist_name);
            listSideImage = view.findViewById(R.id.list_side_image);
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
