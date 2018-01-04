package com.example.jayanth.musicplayer.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.models.ListSong;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jayanth on 4/1/18.
 */

public class SongListAdapter extends ArrayAdapter<ListSong> {
    private List<ListSong> listSongs;
    Context context;


    private static class ViewHolder {
        private TextView listSongName, listArtist;

        private ImageView listSideImage;
        private ImageButton songListOverflow;
    }


    public SongListAdapter(List<ListSong> data, Context context) {
        super(context, R.layout.recycle_list_view_row);
        this.listSongs = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listSongs.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListSong song = listSongs.get(position);
        final ViewHolder holder;
        final View result;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflator = LayoutInflater.from(parent.getContext());
            convertView = inflator.inflate(R.layout.recycle_list_view_row, parent, false);
            holder.listSongName = convertView.findViewById(R.id.list_song_name);
            holder.listArtist = convertView.findViewById(R.id.list_artist_name);
            holder.songListOverflow = convertView.findViewById(R.id.song_list_overflow);
            holder.listSideImage = convertView.findViewById(R.id.list_side_image);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        if (song != null) {
            holder.listArtist.setText(song.getArtist());
            holder.listSongName.setText(song.getSongName());
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri,
                    song.getAlbumId());
//        if(isImageFile(uri.toString()))
            Picasso.with(context).load(uri).resize(1000, 1000).centerCrop().into(holder
                            .listSideImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError() {

                            Picasso.with(context).load(R.drawable.music_player_svg).into(holder
                                    .listSideImage);
                        }
                    });
        }
        return convertView;
    }
}
