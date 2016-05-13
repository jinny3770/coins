package com.example.sora.coins.Main;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sora on 2016-05-12.
 */
public class UpdateForDestination extends AsyncTask<String, Void, Void> {

    private static final String resistURL = "http://52.79.124.54/myPointResist.php";

    int code, order;
    double latitude, longitude;

    @Override
    protected Void doInBackground(String... params) {

        code = Integer.parseInt(params[0]);
        order = Integer.parseInt(params[1]);

        latitude = Double.parseDouble(params[2]);
        longitude = Double.parseDouble(params[3]);

        try {
            URL url = new URL(resistURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoInput(true);

            String data = URLEncoder.encode("json", "UTF-8") + "=" + URLEncoder.encode(makeJson(), "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private String makeJson() throws JSONException {

        JSONObject obj = new JSONObject();

        obj.put("code", code);
        obj.put("order", order);
        obj.put("latitude", latitude);
        obj.put("longitude", longitude);

        return obj.toString();
    }

}
