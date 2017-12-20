package com.example.jayanth.musicplayer.helper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RedirectLocation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            Log.i("test", httpconn.getHeaderFields().get("Location").get(0));
            return httpconn.getHeaderFields().get("Location").get(0);

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