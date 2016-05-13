package com.example.sora.coins.Main;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.sora.coins.etc.APIKey;
import com.example.sora.coins.etc.MyInfo;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.lang.Runnable;
import java.util.concurrent.ExecutionException;

import android.os.Handler;

public class LocationService extends Service implements Runnable {

    TMapView mapView;
    TMapPoint curLoca;
    TMapGpsManager tMapGpsManager;

    MyInfo myInfo;

    private int mStartId;
    private Handler mHandler;
    private boolean mIsRunning = false;
    private static final int TIMER_PERIOD = 5 * 1000;
    private static final int COUNT = 1;

    private static TMapPoint T_MAP_POINT = null;
    private int mCounter;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    public void onCreate() {
        Log.d("111111Service", "onCreate() Call.");
        super.onCreate();
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

        myInfo = MyInfo.getInstance();

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

            // 생략 가능
            double lon = curLoca.getLongitude();
            double lat = curLoca.getLatitude();


            if (curLoca.getLatitude() != 0 && curLoca.getLongitude() != 0) {

                if (myInfo.getID() != null && !curLoca.equals(T_MAP_POINT)) {

                    UpdateLocation updateLocation = new UpdateLocation();
                    myInfo.setPoint(curLoca);

                    try {
                        String str = updateLocation.execute(myInfo.getID(), String.valueOf(lat), String.valueOf(lon)).get().toString();
                        Log.d("UpdateLocation", str);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    pref = getSharedPreferences("pref", 0);
                    prefEditor = pref.edit();
                    int order = pref.getInt("pointOrder", 0);
                    int code = pref.getInt("desCode", 0);

                    Log.d("pref", Boolean.toString(pref.getBoolean("destinationSet", false)) + ", " + Integer.toString(code) + ", " + Integer.toString(order));

                    if (pref.getBoolean("destinationSet", false)) {

                        Log.d("pref", Boolean.toString(pref.getBoolean("destinationSet", false)) + ", " + Integer.toString(code) + ", " + Integer.toString(order));

                        UpdateForDestination ufd = new UpdateForDestination();
                        try {
                            String str = ufd.execute(Integer.toString(code), Integer.toString(order),
                                    String.valueOf(lat), String.valueOf(lon)).get();

                            Log.d("updatePoint", str);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        prefEditor.putInt("pointOrder", order + 1);
                        prefEditor.commit();

                    }
                    T_MAP_POINT = curLoca;
                }
            }

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
