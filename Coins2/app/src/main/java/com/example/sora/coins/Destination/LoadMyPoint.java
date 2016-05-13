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
 * Created by sora on 2016-05-12.
 */
public class LoadMyPoint extends AsyncTask <Integer, Void, String> {

    String loadMyPointURL = "http://52.79.124.54/loadMyPoint.php";
    BufferedReader reader = null;

    @Override
    protected String doInBackground(Integer... params) {

        int code = params[0];

        try {
            URL url  = new URL(loadMyPointURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoInput(true);

            String data = URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(code), "UTF-8");

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


            return str;

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}
