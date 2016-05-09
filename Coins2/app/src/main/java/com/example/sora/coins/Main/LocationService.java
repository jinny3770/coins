package com.example.sora.coins.Main;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sora.coins.R;
import com.example.sora.coins.etc.APIKey;
import com.example.sora.coins.etc.MyInfo;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.lang.Runnable;
import java.util.Timer;

import android.os.Handler;

import java.util.logging.LogRecord;

public class LocationService extends Service implements Runnable {

    TMapView mapView;
    TMapPoint curLoca;
    TMapGpsManager tMapGpsManager;

    MyInfo myInfo;

    private int mStartId;
    private Handler mHandler;
    private boolean mIsRunning = false;
    private static final int TIMER_PERIOD = 2 * 1000;
    private static final int COUNT = 1;
    private int mCounter;

    public void onCreate() {
        Log.d("111111Service", "onCreate() Call.");
        super.onCreate();
        myInfo = MyInfo.getInstance();
        mHandler = new Handler();
        mIsRunning = false;
    }

    public int onStartCommand(Intent service, int flags, int startId) {
        Log.d("111111Service", "onStartCommand() Call.");
        mStartId = startId;
        mCounter = COUNT;
        if (!mIsRunning) {
            mHandler.postDelayed(this, TIMER_PERIOD);
            mIsRunning = true;
        }
        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        Log.d("111111Service", "onDestroy() Call.");
        mIsRunning = false;
        super.onDestroy();
    }

    @Nullable

    @Override
    public void run() {
        if (!mIsRunning) {
            Log.d("111111Service", "run after destroy");
            return;
        } else if (mCounter > 50) {
            Log.d("111111Service", "The count is " + mCounter + ", stop service");
            stopSelf(mStartId);
        } else {
            tMapGpsManager = new TMapGpsManager(this);
            tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
            tMapGpsManager.setMinTime(1000);
            tMapGpsManager.setMinDistance(3);
            tMapGpsManager.OpenGps();
            curLoca = MainActivity.tMapGpsManager2.getLocation();

            mapView = new TMapView(this);
            mapView.setSKPMapApiKey(APIKey.ApiKey);
            mapView.setZoomLevel(15);
            mapView.setMapPosition(TMapView.POSITION_DEFAULT);
            mapView.setTrackingMode(true);

            double lon = curLoca.getLongitude();
            double lat = curLoca.getLatitude();

            myInfo.setPoint(curLoca);

            UpdateLocation updateLocation = new UpdateLocation();
            updateLocation.execute(myInfo.getID(), Double.toString(lat), Double.toString(lon));

            Log.d("111111Long", String.valueOf(lon));
            Log.d("111111Lati", String.valueOf(lat));
            Log.d("111111Service", "" + mCounter);
            mCounter++;
            mHandler.postDelayed(this, TIMER_PERIOD);
        }
    }

    @Override
    public IBinder onBind(Intent service) {
        return null;
    }
}

/**
 * Created by sora on 2016-05-01.
 *//*
 *
public class LocationService extends Service {
    TMapView mapView;
    TMapPoint curLoca;
    TMapGpsManager tMapGpsManager;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent Service) {
        return null;
    }

    public int onStartCommand(Intent Service, int flags, int startId)
    {
        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(APIKey.ApiKey);
        mapView.setTrackingMode(true);
        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        tMapGpsManager.setMinTime(1000);
        tMapGpsManager.setMinDistance(3);
        tMapGpsManager.OpenGps();
        curLoca = tMapGpsManager.getLocation();


        Log.d("111111111111Long", String.valueOf(curLoca.getLongitude()));
        Log.d("111111111111Lati", String.valueOf(curLoca.getLatitude()));
        return START_REDELIVER_INTENT;
    }

}*/
