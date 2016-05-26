package coins.hansung.way.Destination;

import android.os.AsyncTask;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;

import coins.hansung.way.etc.APIKey;

/**
 * Created by sora on 2016-05-26.
 */
public class RouteTask extends AsyncTask<String, Void, String> {

    TMapPoint startPoint;
    TMapPoint endPoint;
    String type;

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];

        startPoint = new TMapPoint(Double.parseDouble(params[1]), Double.parseDouble(params[2]));
        endPoint = new TMapPoint(Double.parseDouble(params[3]), Double.parseDouble(params[4]));

        type = params[5];


        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // Header 설정
            conn.setRequestProperty("appkey", APIKey.ApiKey);
            //conn.setRequestProperty("Accept", "application/json");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            String data = makeData(startPoint, endPoint);

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

    private String makeData(TMapPoint start, TMapPoint end) throws IOException, ParserConfigurationException, SAXException {

        String data = new String();
        TMapData tMapData = new TMapData();

        data = URLEncoder.encode("startX", "UTF-8") + "=" + URLEncoder.encode(Double.toString(start.getLongitude()), "UTF-8")
                + "&" + URLEncoder.encode("startY", "UTF-8") + "=" + URLEncoder.encode(Double.toString(start.getLatitude()), "UTF-8")
                + "&" + URLEncoder.encode("startName", "UTF-8") + "=" + URLEncoder.encode(tMapData.reverseGeocoding(start.getLatitude(), start.getLongitude(), "A00").toString(), "UTF-8")
                + "&" + URLEncoder.encode("endX", "UTF-8") + "=" + URLEncoder.encode(Double.toString(end.getLongitude()), "UTF-8")
                + "&" + URLEncoder.encode("endY", "UTF-8") + "=" + URLEncoder.encode(Double.toString(end.getLatitude()), "UTF-8")
                + "&" + URLEncoder.encode("endName", "UTF-8") + "=" + URLEncoder.encode(tMapData.reverseGeocoding(end.getLatitude(), start.getLongitude(), "A00").toString(), "UTF-8")
                + "&" + URLEncoder.encode("reqCoordType", "UTF-8") + "=" + URLEncoder.encode("WGS84GEO", "UTF-8")
                + "&" + URLEncoder.encode("resCoordType", "UTF-8") + "=" + URLEncoder.encode("WGS84GEO", "UTF-8");

        return data;
    }
}
