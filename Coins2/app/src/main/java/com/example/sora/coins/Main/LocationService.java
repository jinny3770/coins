package com.example.sora.coins.Main;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.sora.coins.Destination.DeleteDestinations;
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
                Log.d("service", "service enter");

                if (myInfo.getID() != null && !curLoca.equals(T_MAP_POINT)) {

                    Log.d("service", "service Excute");
                    UpdateLocation updateLocation = new UpdateLocation();
                    myInfo.setPoint(curLoca);

                    try {
                        String str = updateLocation.execute(myInfo.getID(), String.valueOf(lat), String.valueOf(lon)).get().toString();
                        Log.d("service", "update Location");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    pref = getSharedPreferences("pref", 0);
                    prefEditor = pref.edit();

                    Log.d("service", "pref_destinationSet : " + Boolean.toString(pref.getBoolean("destinationSet", false)));

                    if (pref.getBoolean("destinationSet", false)) {

                        float latitude = pref.getFloat("endPointLatitude", 0);
                        float longitude = pref.getFloat("endPointLongitude", 0);
                        int order = pref.getInt("pointOrder", 0);
                        int code = pref.getInt("desCode", 0);

                        if(distanceCalc(latitude, longitude, curLoca.getLatitude(), curLoca.getLongitude())) {

                            Log.d("service", "delete destination");
                            DeleteDestinations deleteDestinations = new DeleteDestinations();
                            deleteDestinations.execute(myInfo.getID());

                            prefEditor.remove("destinationSet");
                            prefEditor.remove("pointOrder");
                            prefEditor.remove("desCode");
                            prefEditor.remove("endPointLatitude");
                            prefEditor.remove("endPointLongitude");
                            prefEditor.commit();
                        }


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

    public boolean distanceCalc(float endPointLatitude, float endPointLongitude, double latitude, double longitude) {

        double dis, ret;

        double Earth_r = 6371000.0;
        double Rad = Math.PI/180;

        double radEndLati = Rad * endPointLatitude;
        double radMyLati = Rad * latitude;
        double radDist = Rad * (longitude - endPointLongitude);

        dis = Math.sin(radEndLati) * Math.sin(radMyLati);
        dis = dis + Math.cos(radEndLati) * Math.cos(radMyLati) * Math.cos(radDist);

        ret = Earth_r * Math.acos(dis);

        double rslt = Math.round(Math.round(ret));
        Log.d("distanceCalc", Double.toString(rslt));

        if(rslt < 10) {

            return true;
        }
        else return false;
    }

}
