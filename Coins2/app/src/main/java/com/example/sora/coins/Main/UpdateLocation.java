package com.example.sora.coins.Main;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016-05-05.
 */

public class UpdateLocation extends AsyncTask<String, Void, String>
{
    private static String updateLocationURL = "http://52.79.124.54/updateLocation.php";
    String data;

    @Override
    protected String doInBackground(String... params)
    {
        String ID = params[0];
        String lat = params[1];
        String lon = params[2];
        URL url = null;

        BufferedReader reader;
        try
        {
            url = new URL(updateLocationURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                    + "&" + URLEncoder.encode("lati", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8")
                    + "&" + URLEncoder.encode("long", "UTF-8") + "=" + URLEncoder.encode(lon, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String str = reader.readLine();
            return str;
        }

        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

    }
}
