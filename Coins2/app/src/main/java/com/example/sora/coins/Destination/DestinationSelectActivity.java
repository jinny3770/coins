package com.example.sora.coins.Destination;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sora.coins.etc.APIKey;
import com.example.sora.coins.etc.MyInfo;
import com.example.sora.coins.R;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by sora on 2016-04-21.
 */
public class DestinationSelectActivity extends AppCompatActivity implements View.OnClickListener {

    static final String walkURL = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1";
    static final String bicycleURL = "https://apis.skplanetx.com/tmap/routes/bicycle?version=1";
    static final String carURL = "https://apis.skplanetx.com/tmap/routes?version=1";
    static final String resistURL = "http://52.79.124.54/destinationsResist.php";

    LinearLayout mapLayout;
    TMapView mapView;

    MyInfo myInfo;
    ImageButton desButton, sendButton;

    EditText searchText;
    ImageButton searchButton;

    DestinationInfo destinationInfo;
    Date currentTime;

    TMapData tMapData;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_select);

        myInfo = MyInfo.getInstance();

        pref = getSharedPreferences("pref", 0);
        prefEditor = pref.edit();

        currentTime = new Date();
        tMapData = new TMapData();

        mapLayout = (LinearLayout) findViewById(R.id.destinationView);
        mapView = new TMapView(this);
        mapView.setZoomLevel(14);

        mapView.setCenterPoint(myInfo.getPoint().getLongitude(), myInfo.getPoint().getLatitude());
        mapLayout.addView(mapView);

        Bitmap departure = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.departure);
        Bitmap arrival = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.arrival);

        mapView.setTMapPathIcon(departure, arrival);

        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        desButton = (ImageButton) findViewById(R.id.desButton);
        sendButton = (ImageButton) findViewById(R.id.sendButton);

        searchButton.setOnClickListener(this);
        desButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        AppCompatDialog dialog;

        switch (v.getId()) {
            case R.id.desButton:
                dialog = selectDialogMaker();
                dialog.show();
                break;

            case R.id.sendButton:
                dialog = sendDialogMaker();
                dialog.show();
                break;

            case R.id.searchButton:
                if (searchText.getText().toString().length() > 0) {
                    TMapData tmapData = new TMapData();

                    tmapData.findAllPOI(searchText.getText().toString(), new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                            mapView.removeAllTMapPOIItem();
                            mapView.addTMapPOIItem(arrayList);

                            // 4.1.100 -> poi라벨 클릭에 대한 인터페이스 함수수
                            TMapPOIItem item0 = arrayList.get(0);

                            mapView.setCenterPoint(item0.getPOIPoint().getLongitude(), item0.getPOIPoint().getLatitude());

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                        }
                    });
                }

                break;
        }
    }

    private AppCompatDialog sendDialogMaker() {

        TextView time, distance;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.destination_alert, null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DestinationSelectActivity.this);

        builder.setTitle("test");
        builder.setView(dialogView);

        try {
            time = (TextView) dialogView.findViewById(R.id.timeView);
            distance = (TextView) dialogView.findViewById(R.id.distanceView);

            time.setText("소요 시간은 " + destinationInfo.getStringTime() + "초 이며,");
            distance.setText("예상 이동거리는 " + destinationInfo.getStringDistance() + "m 입니다.");

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(pref.getBoolean("destinationSet", false)) {

                        Log.d("destinationSet", Boolean.toString(pref.getBoolean("destinationSet", false)));

                        ResistDestination resistDestination = new ResistDestination();
                        resistDestination.execute(destinationInfo);

                    }
                    else{
                        Log.d("destinationSet", Boolean.toString(pref.getBoolean("destinationSet", false)));
                        Toast.makeText(getApplicationContext(), "이미 등록 된 목적지가 있습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });

            AppCompatDialog dialog = builder.create();
            return dialog;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    private AppCompatDialog selectDialogMaker() {

        final RadioGroup rGroup;
        final RadioButton walk, bicycle, car;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.destination_select, null);

        rGroup = (RadioGroup) dialogView.findViewById(R.id.radioGroup);
        walk = (RadioButton) dialogView.findViewById(R.id.walkRadio);
        bicycle = (RadioButton) dialogView.findViewById(R.id.bicycleRadio);
        car = (RadioButton) dialogView.findViewById(R.id.carRadio);

        rGroup.check(walk.getId()); // 기본 체크 => 도보

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DestinationSelectActivity.this);

        builder.setTitle("select");
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TMapPoint startPoint, endPoint;

                startPoint = myInfo.getPoint();
                endPoint = mapView.getCenterPoint();

                String url = null;
                String type = null;
                String sLati = Double.toString(startPoint.getLatitude());
                String sLong = Double.toString(startPoint.getLongitude());
                String eLati = Double.toString(endPoint.getLatitude());
                String eLong = Double.toString(endPoint.getLongitude());


                switch (rGroup.getCheckedRadioButtonId()) {
                    case R.id.walkRadio:
                        url = walkURL;
                        type = "walk";
                        break;

                    case R.id.bicycleRadio:
                        url = bicycleURL;
                        type = "bicycle";
                        break;

                    case R.id.carRadio:
                        url = carURL;
                        type = "car";
                        break;
                }

                RouteTask routeTask = new RouteTask();
                routeTask.execute(url, sLati, sLong, eLati, eLong, type);
            }
        });

        AppCompatDialog dialog = builder.create();
        return dialog;
    }


    class RouteTask extends AsyncTask<String, Void, String> {
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


        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject topObj = new JSONObject(s);

                // features의 배열 받아옴.
                JSONArray featureArray = topObj.getJSONArray("features");

                //feature obj 중 properties obj만 가져옴.
                JSONObject featObj = featureArray.getJSONObject(0).getJSONObject("properties");

                int time = featObj.getInt("totalTime");
                int dis = featObj.getInt("totalDistance");
                destinationInfo = new DestinationInfo(time, dis, type);
                destinationInfo.initLinePoint();


                //feature obj 중 geometry obj만 가져옴 -> 경로 표시를 위해서
                for (int k = 0; k < featureArray.length(); k++) {
                    JSONObject geoObj = featureArray.getJSONObject(k).getJSONObject("geometry");
                    JSONArray cooArray = geoObj.getJSONArray("coordinates");
                    String type = geoObj.getString("type");

                    if (type.equals("Point")) {

                        TMapPoint point = new TMapPoint(cooArray.getDouble(1), cooArray.getDouble(0));

                        if(!destinationInfo.compareLastPoint(point)) {
                            destinationInfo.addLinePoint(point);
                        }

                    } else if (type.equals("LineString")) {

                        for (int i = 0; i < cooArray.length(); i++) {

                            JSONArray obj = cooArray.getJSONArray(i);
                            TMapPoint point = new TMapPoint(obj.getDouble(1), obj.getDouble(0));

                            if(!destinationInfo.compareLastPoint(point)) {
                                destinationInfo.addLinePoint(point);
                            }

                        }
                    }
                }
                mapView.addTMapPath(destinationInfo.getLine());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class ResistDestination extends AsyncTask<DestinationInfo, Void, String> {

        BufferedReader reader = null;



        @Override
        protected String doInBackground(DestinationInfo... params) {

            DestinationInfo info = params[0];

            try {

                URL url = new URL(resistURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(makeData(info, myInfo));
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {

             if (s.equals("fail")) {
                Toast.makeText(getApplicationContext(), "등록에 실패했습니다.", Toast.LENGTH_LONG).show();

            } else {

                prefEditor.putBoolean("destinationSet", true);
                prefEditor.putInt("desCode", Integer.parseInt(s));
                prefEditor.putInt("pointOrder", 2);
                prefEditor.commit();

                Log.d("prefCheck", Boolean.toString(pref.getBoolean("destinationSet", false)) + ", "
                        + Integer.toString(pref.getInt("desCode", 0)));
                Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private String makeData(TMapPoint start, TMapPoint end) throws IOException, ParserConfigurationException, SAXException {

        String data = new String();

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


    private String makeData(DestinationInfo info, MyInfo myInfo) throws JSONException, UnsupportedEncodingException {

        String data = null;

        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        ArrayList<TMapPoint> points = info.getLinePoint();

        obj.put("ID", myInfo.getID());
        obj.put("CODE", myInfo.getGroupCode());
        obj.put("TIME", info.getTime());
        obj.put("DISTANCE", info.getDistance());
        obj.put("TYPE", info.getType());

        for (int i = 0; i < points.size()-1; i++) {

            TMapPoint p = points.get(i);
            JSONArray tmp = new JSONArray();

            tmp.put(p.getLatitude());
            tmp.put(p.getLongitude());

            arr.put(tmp);
        }

        obj.put("POINTS", arr);
        Log.i("dataLog", obj.toString());

        data = URLEncoder.encode("json", "UTF-8") + "=" + URLEncoder.encode(obj.toString(), "UTF-8");
        return data;
    }
}
