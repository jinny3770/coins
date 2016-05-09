package com.example.sora.coins.Destination;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sora on 2016-05-07.
 */
public class LoadDestinationsList extends AsyncTask <String, Void, String>{

    String gcode;
    BufferedReader reader = null;

    final static String loadURL = "http://52.79.124.54/loadDestinationsList.php";

    @Override
    protected String doInBackground(String... params) {
        gcode = params[0];

        try {
            URL url = new URL(loadURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            String data = URLEncoder.encode("CODE", "utf8")  + "=" + URLEncoder.encode(gcode, "utf8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder answer = new StringBuilder();
            String str;

            while ((str = reader.readLine()) != null) {
                answer.append(str);
            }

            str = answer.toString();

            Log.i("dataLog", str);
            return str;

        } catch (Exception e) {
            return e.getMessage().toString();
        }
    }
}
