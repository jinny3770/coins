package com.example.sora.coins;

import android.app.ActionBar;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.LogManager;

/**
 * Created by sora on 2016-04-21.
 */
public class DestinationActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout mapLayout;
    TMapView mapView;

    MyInfo myInfo;
    ActionBar actionBar;
    ImageButton desButton;

    EditText searchText;
    Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        myInfo = MyInfo.getInstance();

        mapLayout = (LinearLayout) findViewById(R.id.destinationView);
        mapView = new TMapView(this);
        mapView.setZoomLevel(14);

        mapView.setCenterPoint(myInfo.getPoint().getLongitude(), myInfo.getPoint().getLatitude());
        mapLayout.addView(mapView);


        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);
        desButton = (ImageButton) findViewById(R.id.desButton);

        desButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        //drawMapPath();

    }

    public void drawMapPath(TMapPoint endPoint) {
        TMapPoint startPoint = myInfo.getPoint();
        //TmapPoint endPoint = randomTMapPoint();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathData(startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {

            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                mapView.addTMapPath(polyLine);
            }
        });
    }

    /*
    public void drawMapPath2() {
        TMapPoint startPoint = myInfo.getPoint();
        TMapPoint endPoint = randomTMapPoint();


        drawMapPath(startPoint, endPoint);

        TMapData tmapData = new TMapData();

        HashMap<String, String> pathInfo = new HashMap<String, String>();


        pathInfo.put("rStName", "MyLocation");
        pathInfo.put("rStlat", Double.toString(startPoint.getLatitude()));
        pathInfo.put("rStlon", Double.toString(startPoint.getLongitude()));
        pathInfo.put("rGoName", "MyDestination");
        pathInfo.put("rGolat", Double.toString(endPoint.getLatitude()));
        pathInfo.put("rGolon", Double.toString(endPoint.getLongitude()));
        pathInfo.put("type", "arrival");


        Date currentTime = new Date();
        //Document doc = tmapData.findTimeMachineCarPath(pathInfo, currentTime, null);

        tmapData.findTimeMachineCarPath(pathInfo, currentTime, null, new TMapData.FindTimeMachineCarPathListenerCallback() {
            @Override
            public void onFindTimeMachineCarPath(Document document) {

                try {
                    JSONArray jsonArray = new JSONArray(document);


                    JSONObject topObj = jsonArray.getJSONObject(0);
                    JSONObject featureObj = topObj.getJSONObject("feature");
                    JSONObject propertyObj = featureObj.getJSONObject("properties");

                    String type = propertyObj.getString("pointType");

                    timeView.setText(Integer.toString(propertyObj.getInt("totalTime")));
                    distanceView.setText(Integer.toString(propertyObj.getInt("totalDistance")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    */

    public TMapPoint randomTMapPoint() {
        double latitude = ((double) Math.random()) * (37.575113 - 37.483086) + 37.483086;
        double longitude = ((double) Math.random()) * (127.027359 - 126.878357) + 126.878357;

        latitude = Math.min(37.575113, latitude);
        latitude = Math.max(37.483086, latitude);

        longitude = Math.min(127.027359, longitude);
        longitude = Math.max(126.878357, longitude);

        TMapPoint point = new TMapPoint(latitude, longitude);

        return point;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.desButton :
                TMapPoint endPoint = mapView.getCenterPoint();

                drawMapPath(endPoint);
                break;


            case R.id.searchButton :
                TMapData tmapData = new TMapData();

                // 4.9.11
                // 4.1.34 addTMapPOIItem
                tmapData.findTitlePOI(searchText.getText().toString(), new TMapData.FindTitlePOIListenerCallback() {
                    @Override
                    public void onFindTitlePOI(ArrayList<TMapPOIItem> arrayList) {
                        mapView.removeAllTMapPOIItem();
                        mapView.addTMapPOIItem(arrayList);
                    }
                });
        }
    }
}
