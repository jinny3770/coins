package com.example.sora.coins;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.daum.mf.map.api.*;

public class MainActivity extends AppCompatActivity {

    private static final int defaultZoomLevel = 3;

    ActionBar actionBar;
    MapView mapView;
    LinearLayout mapLayout;
    DrawerLayout sideMenuLayout;
    ActionBarDrawerToggle drawerToggle;

    ImageButton chatButton;
    ImageButton locaButton;
    ImageButton shareButton;

    CustomMapViewEventListener mapViewListener;


    //boolean trackingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
        LocationManager locaManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locaManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Toast.makeText(MainActivity.this, "Sorry", Toast.LENGTH_LONG);
        }
        */


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF8080));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater actionBarInflater = LayoutInflater.from(this);
        View customView = actionBarInflater.inflate(R.layout.actionbar_layout, null);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);

        mapLayout = (LinearLayout) findViewById(R.id.mapView);


        mapView = new MapView(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setDaumMapApiKey(APIKey.ApiKey);
        mapView.setZoomLevel(defaultZoomLevel, true);

        mapViewListener = new CustomMapViewEventListener();
        mapView.setMapViewEventListener(mapViewListener);


        sideMenuLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, sideMenuLayout, R.string.open, R.string.close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        sideMenuLayout.setDrawerListener(drawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();


        chatButton = (ImageButton) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });

        shareButton = (ImageButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, Integer.toString(mapView.getZoomLevel()), Toast.LENGTH_LONG).show();
            }
        });

        locaButton = (ImageButton) findViewById(R.id.locaButton);
        locaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

                /*
                if (!trackingMode) {
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    //mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.ic_action_place, new MapPOIItem.ImageOffset(20, 0));
                    trackingMode = true;
                } else {
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                    trackingMode = false;
                }
                */

            }
        });


        mapLayout.addView(mapView);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


}