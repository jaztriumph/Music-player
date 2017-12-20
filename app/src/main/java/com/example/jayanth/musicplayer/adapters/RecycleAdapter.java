package com.example.jayanth.musicplayer.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayanth.musicplayer.R;
import com.example.jayanth.musicplayer.models.Song;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by jayanth on 18/12/17.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {


    private Context mContext;
    private List<Song> songList;
    final private RecycleAdapterOnClickHandler mOnClickHandler;

    public interface RecycleAdapterOnClickHandler {
        void onClick(Song song);
    }


    public RecycleAdapter(Context mContext, List<Song> songList,RecycleAdapterOnClickHandler
            mOnClickHandler) {
        this.mContext = mContext;
        this.songList = songList;
        this.mOnClickHandler=mOnClickHandler;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, artist;
        public ImageView thumbnail, overflow;
        View view;

        public MyViewHolder(View view) {
            super(view);
            this.view=view;
            title = view.findViewById(R.id.title);
            artist = view.findViewById(R.id.artist);
            thumbnail = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, "adapter", Toast.LENGTH_SHORT).show();
            int adapterPosition =getAdapterPosition();
            mOnClickHandler.onClick(songList.get(adapterPosition));

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "adapter", Toast.LENGTH_SHORT).show();
                int adapterPosition =position;
                mOnClickHandler.onClick(songList.get(adapterPosition));
            }
        });
        Song song = songList.get(position);
        holder.title.setText(song.getSong());
        holder.artist.setText(song.getArtists());


        // loading album cover using Glide library
//        Picasso.Builder builder = new Picasso.Builder(mContext);
//        builder.downloader(new OkHttpDownloader(mContext));
//        builder.build().load(song.getUrl()).into(holder.thumbnail);
//        OkHttpClient client = new OkHttpClient();
//        client.setFollowRedirects(false);
//                Picasso picasso = new Picasso.Builder(mContext)
//                .downloader(new OkHttp3Downloader(mContext))
//                .build();
//                picasso.load(song.getUrl()).into(holder.thumbnail);
//        Glide.with(mContext).load("https://s3-ap-southeast-1.amazonaws
// .com/he-public-data/afreen2ac5a33.jpg").into(holder.thumbnail);
//        new Connect(mContext,holder).execute(song.getUrl());
//        okhttp3.OkHttpClient okHttp3Client = new okhttp3.OkHttpClient();
//        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttp3Client);
//        Picasso picasso = new Picasso.Builder(mContext)
//                .downloader(new OkHttp3Downloader(client))
//                .build();
        Picasso.with(mContext).load(song.getCoverImage()).into(holder.thumbnail);




//        String finalUrl=null;
//        try {
//             finalUrl = new Connect().execute(song.getCoverImage()).get();
//            Picasso.with(mContext).load(new Connect().execute(song.getCoverImage()).get()).into(holder
//                    .thumbnail);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }


        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
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
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    private static class Connect extends AsyncTask<String, Void, String> {
        final StringBuilder response = new StringBuilder();
//        Context context=null;
//        MyViewHolder holder;
//         Connect(Context context,MyViewHolder holder){
//            this.context=context;
//            this.holder=holder;
//
//        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
                Log.i("test", httpconn.getHeaderFields().get("Location").get(0));
                return httpconn.getHeaderFields().get("Location").get(0);
//                Picasso.with(context).load(finalUrl).into(holder.thumbnail);
//                    return httpconn.getHeaderFields().toString();
//                return finalUrl;
            } catch (
                    MalformedURLException e)

            {
                e.printStackTrace();
//                    return null;
            } catch (
                    IOException e)

            {
                e.printStackTrace();
//                    return null;

            }
            return null;
        }
    }

}
