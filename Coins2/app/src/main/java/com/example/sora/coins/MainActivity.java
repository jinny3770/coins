package com.example.sora.coins;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
    DrawerLayout sideMenuLayout;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF8080));
        //actionBar.setIcon(R.drawable.chat);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater actionBarInflater = LayoutInflater.from(this);
        //View customView = actionBarInflater.inflate(R.layout.actionbar_layout, null);
        //actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);

        //mapView = (MapView)findViewById(R.id.mapView);
        mapLayout = (LinearLayout) findViewById(R.id.mapView);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(APIKey.ApiKey);
        mapLayout.addView(mapView);


        sideMenuLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, sideMenuLayout, R.string.open, R.string.close)
        {
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };


        sideMenuLayout.setDrawerListener(drawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


}