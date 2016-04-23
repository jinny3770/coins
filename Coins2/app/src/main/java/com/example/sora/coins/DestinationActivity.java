package com.example.sora.coins;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.LogManager;

/**
 * Created by sora on 2016-04-21.
 */
public class DestinationActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mapLayout;
    TMapView mapView;

    MyInfo myInfo;
    ImageButton desButton;

    EditText searchText;
    Button searchButton;

    String time, distance;

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

    public void drawMapPath(TMapPoint startPoint, TMapPoint endPoint) {
        TMapData tmapdata = new TMapData();

        tmapdata.findPathData(startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {

            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                mapView.addTMapPath(polyLine);
            }
        });
    }


    public void drawMapPath2() {
        TMapPoint startPoint = myInfo.getPoint();
        TMapPoint endPoint = mapView.getCenterPoint();

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

        tmapData.findTimeMachineCarPath(pathInfo, currentTime, null, new TMapData.FindTimeMachineCarPathListenerCallback() {
            @Override
            public void onFindTimeMachineCarPath(Document document) {

                if (document == null) {
                    Log.e("doc error", "Document is null");
                } else {
                    //NodeList topNode = document.getElementsByTagName("kml");

                    Element top = document.getDocumentElement();
                    NodeList topList = top.getChildNodes();

                    Element docuEle = (Element) topList.item(1);
                    Node docuNode = topList.item(1);

                    NodeList docuList = docuNode.getChildNodes();

                    NodeList disList = docuEle.getElementsByTagName("tmap:totalDistance");
                    NodeList timeList = docuEle.getElementsByTagName("tmap:totalTime");


                    Element disEle = (Element) disList.item(0);
                    Node disNode = disEle.getFirstChild();
                    distance = disNode.getNodeValue();
                    Log.i("nodeLog", distance);


                    Element timeEle = (Element) timeList.item(0);
                    Node timeNode = timeEle.getFirstChild();
                    time = timeNode.getNodeValue();
                    Log.i("nodeLog", time);

                }

            }
        });


    }


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
        switch (v.getId()) {
            case R.id.desButton:
                drawMapPath2();
                break;

            case R.id.searchButton:

                if(searchText.getText().toString().length() > 0) {

                    TMapData tmapData = new TMapData();

                    tmapData.findAllPOI(searchText.getText().toString(), new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                            mapView.removeAllTMapPOIItem();
                            mapView.addTMapPOIItem(arrayList);

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


}
