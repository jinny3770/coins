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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.daum.mf.map.api.*;

public class MainActivity extends AppCompatActivity {
    private static final int defaultZoomLevel = 3;

    // 상단 액션바 관련 변수
    ActionBar actionBar;
    ImageButton chatButton, locaButton, shareButton;

    // GPS 지도 관련 변수
    MapView mapView;
    LinearLayout mapLayout;

    // 사이드바 관련 변수
    DrawerLayout sideDrawer;
    ActionBarDrawerToggle sideToggle;
    ListView sideList;
    FrameLayout sideContainer;
    CustomMapViewEventListener mapViewListener;
    //boolean trackingMode = false;

    // 하단 가족 리스트뷰
    ListView familyList;

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

        // 상단 액션바
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF8080));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater actionBarInflater = LayoutInflater.from(this);
        View customView = actionBarInflater.inflate(R.layout.actionbar_layout, null);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);

        // 액션바 리스너
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

        // GPS 지도 및 관련 리스너
        mapLayout = (LinearLayout) findViewById(R.id.mapView);
        mapView = new MapView(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setDaumMapApiKey(APIKey.ApiKey);
        mapView.setZoomLevel(defaultZoomLevel, true);
        mapViewListener = new CustomMapViewEventListener();
        mapView.setMapViewEventListener(mapViewListener);

        // 드로어 & 사이드바 리스너
        sideList = (ListView) findViewById(R.id.list_activity_main);
        sideContainer = (FrameLayout) findViewById(R.id.frame_activity_main);
        sideDrawer = (DrawerLayout) findViewById(R.id.drawer_activity_main);
        String sideItems[] = {"그룹 설정", "비상연락망 설정", "환경 설정"};
        sideList.setAdapter(new ArrayAdapter <String> (this, android.R.layout.simple_list_item_1, sideItems));

        sideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                sideDrawer.closeDrawer(sideList);

                switch (position) {
                    case 0: // 그룹 설정
                        intent = new Intent(getApplicationContext(), SideGroup.class);
                        startActivity(intent);
                        break;

                    case 1: // 비상연락망 설정
                        intent = new Intent(getApplicationContext(), SideWarning.class);
                        startActivity(intent);
                        break;

                    case 2: // 환경 설정
                        intent = new Intent(getApplicationContext(), SideSetting.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        // 드로어 -> 액션바 토글
        sideToggle = new ActionBarDrawerToggle(this, sideDrawer, R.string.app_name, R.string.app_name)
        {
            public void onDrawerClosed(View view)           { super.onDrawerClosed(view); }
            public void onDrawerOpened(View drawerView)     { super.onDrawerOpened(drawerView); }
        };

        sideDrawer.setDrawerListener(sideToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        sideToggle.syncState();

        mapLayout.addView(mapView);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        sideToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sideToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (sideToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}