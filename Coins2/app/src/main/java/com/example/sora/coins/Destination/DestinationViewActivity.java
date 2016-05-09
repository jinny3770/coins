package com.example.sora.coins.Destination;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by sora on 2016-04-29.
 */

// listActivity에서 intent로 받아올 것
public class DestinationViewActivity extends AppCompatActivity {

    TMapView mapView;
    TMapPoint centerPoint;
    TMapPolyLine polyLine;

    String arriveTimeString;
    String departureTimeString;
    int time;


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
            departureTimeString = intent.getStringExtra("departureTime");
            arriveTimeString = intent.getStringExtra("arriveTime");
            time = intent.getIntExtra("time", 0);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        mapView.addTMapPath(polyLine);
        mapView.setZoomLevel(15);
        mapView.setCenterPoint(centerPoint.getLongitude(), centerPoint.getLatitude());

        departureTimeText.setText("출발 시간 : " +  departureTimeString);
        arriveTimeText.setText("도착 예정 시간 : " + arriveTimeString);
        aroundTime.setText("소요 시간 : " + (time/60) + "분 예상");

        linearLayout.addView(mapView);

    }

    TMapPolyLine lineParsing(String s) throws JSONException {

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
