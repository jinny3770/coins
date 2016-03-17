package com.example.sora.coins;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
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


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF8080));
        actionBar.setTitle("");
        //actionBar.setIcon(R.drawable.chat);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater actionBarInflater = LayoutInflater.from(this);

        View customView = actionBarInflater.inflate(R.layout.actionbar_layout, null);

        //ImageButton chatButton = (ImageButton) customView.findViewById(R.id.menuButton);

        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);

        //mapView = (MapView)findViewById(R.id.mapView);
        mapLayout = (LinearLayout) findViewById(R.id.mapView);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(APIKey.ApiKey);
        //mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapLayout.addView(mapView);



        mapView.setShowCurrentLocationMarker(true);

    }

    public boolean onCreateOptionMenu(Menu menu){

        return true;
    }
}