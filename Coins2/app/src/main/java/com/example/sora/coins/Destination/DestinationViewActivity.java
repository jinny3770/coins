package com.example.sora.coins.Destination;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sora.coins.R;
import com.example.sora.coins.etc.APIKey;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by sora on 2016-04-29.
 */

// listActivity에서 intent로 받아올 것
public class DestinationViewActivity extends AppCompatActivity {

    TMapView mapView;
    TMapPoint centerPoint;
    TMapPolyLine polyLine;
    TMapPolyLine myLine;

    String arriveTimeString;
    String departureTimeString;
    int time;
    int code;


    TextView arriveTimeText;
    TextView departureTimeText;
    TextView aroundTime;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_view);


        departureTimeText = (TextView) findViewById(R.id.departureTimeText);
        arriveTimeText = (TextView) findViewById(R.id.arriveTimeText);
        aroundTime = (TextView) findViewById(R.id.aroundTime);
        linearLayout = (LinearLayout) findViewById(R.id.desMapView);


        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(APIKey.ApiKey);

        Intent intent = getIntent();

        try {

            System.out.println(intent.getStringExtra("lineString"));
            polyLine = lineParsing(intent.getStringExtra("lineString"));
            //myLine = lineParsing(intent.getStringExtra("realLineString"));
            departureTimeString = intent.getStringExtra("departureTime");
            arriveTimeString = intent.getStringExtra("arriveTime");
            time = intent.getIntExtra("time", 0);
            code = intent.getIntExtra("code", 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //polyLine.setLineColor(Color.rgb(255,42, 00));
        // myLine.setLineColor(Color.rgb(255, 225, 33));
        mapView.addTMapPath(polyLine);
        //mapView.addTMapPolyLine("myLine", myLine);
        //mapView.addTMapPath(myLine);
        mapView.setZoomLevel(15);
        mapView.setCenterPoint(centerPoint.getLongitude(), centerPoint.getLatitude());

        departureTimeText.setText("출발 시간 : " + departureTimeString);
        arriveTimeText.setText("도착 예정 시간 : " + arriveTimeString);
        aroundTime.setText("소요 시간 : " + (time / 60) + "분 예상");

        linearLayout.addView(mapView);

    }

    @Override
    protected void onResume() {

        myLine = new TMapPolyLine();

        // 비교 line생성
        LoadMyPoint loadMyPoint = new LoadMyPoint();
        try {
            String str = loadMyPoint.execute(code).get();
            Log.d("loadMyPoint", str);

            JSONArray jsonArray = new JSONArray(str);

            for(int i=0; i<jsonArray.length(); i++) {
                JSONArray arr = jsonArray.getJSONArray(i);

                Log.d("loadMyPoint", Integer.toString(i) + " : " + Double.toString(arr.getDouble(0)) + ", " + Double.toString(arr.getDouble(1)));
                TMapPoint point = new TMapPoint(arr.getDouble(0), arr.getDouble(1));
                myLine.addLinePoint(point);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        myLine.setLineColor(Color.YELLOW);
        myLine.setLineWidth(5);
        mapView.addTMapPolyLine("myLine", myLine);

        super.onResume();
    }

    TMapPolyLine lineParsing(String s) throws JSONException {

        Log.i("lineParsing", s);
        TMapPolyLine line = new TMapPolyLine();

        JSONArray pointArray = new JSONArray(s);

        for (int i = 0; i < pointArray.length(); i++) {
            JSONArray point = pointArray.getJSONArray(i);

            TMapPoint p = new TMapPoint(point.getDouble(0), point.getDouble(1));
            if (i == 0) centerPoint = p;
            line.addLinePoint(p);
        }

        return line;

    }
}
