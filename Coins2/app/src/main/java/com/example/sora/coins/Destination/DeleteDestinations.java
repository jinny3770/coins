package com.example.sora.coins.Destination;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sora on 2016-05-07.
 */
public class DeleteDestinations extends AsyncTask<String, Void, String > {

    static final String deleteURL = "http://52.79.124.54/deleteDestinations.php";
    String data, result;
    BufferedReader reader = null;

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(deleteURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return result = reader.readLine();


        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage().toString();
        }
    }
}
