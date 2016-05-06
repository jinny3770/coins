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

public class LoadFamilyList extends AsyncTask<String, Void, String>
{
    private static String loadFamilyURL = "http://52.79.124.54/loadFamilyList.php";
    private String code, data, result;
    private BufferedReader reader = null;

    @Override
    protected String doInBackground(String... params)
    {
        code = params[0];

        try
        {
            URL url = new URL(loadFamilyURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            data = URLEncoder.encode("CODE", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = reader.readLine();
            return result;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}