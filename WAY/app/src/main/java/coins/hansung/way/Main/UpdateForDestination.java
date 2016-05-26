package coins.hansung.way.Main;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import coins.hansung.way.etc.Links;

/**
 * Created by sora on 2016-05-19.
 */

public class UpdateForDestination extends AsyncTask<String, Void, String> {

    int code, order;
    double latitude, longitude;
    BufferedReader reader = null;

    @Override
    protected String doInBackground(String... params) {

        code = Integer.parseInt(params[0]);
        order = Integer.parseInt(params[1]);

        latitude = Double.parseDouble(params[2]);
        longitude = Double.parseDouble(params[3]);

        try {
            URL url = new URL(Links.myPointResistURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoInput(true);

            String data = URLEncoder.encode("json", "UTF-8") + "=" + URLEncoder.encode(makeJson(), "UTF-8");
            Log.d("updateForDestination", data);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = reader.readLine();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }



    private String makeJson() throws JSONException {

        JSONObject obj = new JSONObject();

        obj.put("code", code);
        obj.put("order", order);
        obj.put("latitude", latitude);
        obj.put("longitude", longitude);

        Log.d("updateForDestination", obj.toString());
        return obj.toString();
    }

}
