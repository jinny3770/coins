package com.example.sora.coins;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import net.daum.mf.map.api.*;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    MapView mapView;
    LinearLayout mapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapLayout = (LinearLayout) findViewById(R.id.mapView);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey("API");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mapLayout.addView(mapView);


    }
}