package coins.hansung.way.Main;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import coins.hansung.way.Destination.DeleteDestinations;
import coins.hansung.way.Destination.DestinationSelectActivity;
import coins.hansung.way.etc.APIKey;
import coins.hansung.way.etc.MyInfo;
import coins.hansung.way.etc.RegID;

public class LocationService extends Service implements Runnable {

    TMapView mapView;
    TMapPoint curLoca;
    TMapGpsManager tMapGpsManager;

    MyInfo myInfo;


    public static String whowhowho;
    private int mStartId;
    private Handler mHandler;
    private boolean mIsRunning = false;
    private static final int TIMER_PERIOD = 20 * 1000;
    private static final int COUNT = 1;

    int check=1;

    private static TMapPoint T_MAP_POINT = null;
    private int mCounter;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;


    RegID regID;
    private Result result;
    private Message message;
    private final Sender sender = new Sender("AIzaSyB2XTCAUUYfCMNCSyd4Xwvr9gqYr65e1no");

    boolean destinationCheck;

    public void onCreate() {

        super.onCreate();
        mHandler = new Handler();
        mIsRunning = false;


    }

    public int onStartCommand(Intent service, int flags, int startId) {

        mStartId = startId;
        mCounter = COUNT;
        if (!mIsRunning) {
            mHandler.postDelayed(this, TIMER_PERIOD);
            mIsRunning = true;
        }

        destinationCheck = service.getBooleanExtra("destinationCheck", false);

        myInfo = MyInfo.getInstance();

        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        mIsRunning = false;
        super.onDestroy();
    }

    @Nullable

    @Override
    public void run() {
        if (!mIsRunning) {
            return;
        } else

        {
            tMapGpsManager = new TMapGpsManager(this);
            tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
            tMapGpsManager.setMinTime(1000);
            tMapGpsManager.setMinDistance(3);
            tMapGpsManager.OpenGps();
            double lon = 0, lat = 0;

            try
            {
                curLoca = MainActivity.tMapGpsManager.getLocation();
                lon = curLoca.getLongitude();
                lat = curLoca.getLatitude();
            }

            catch (NullPointerException npe)
            {
                npe.printStackTrace();
            }

            mapView = new TMapView(this);
            mapView.setSKPMapApiKey(APIKey.ApiKey);
            mapView.setZoomLevel(15);
            mapView.setMapPosition(TMapView.POSITION_DEFAULT);
            mapView.setTrackingMode(true);

            double getLat = 0, getLong = 0;
            try{
                getLat = curLoca.getLatitude();
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
            try{
                getLong = curLoca.getLongitude();
            }catch (NullPointerException e) {
                e.printStackTrace();
            }


            if (getLat != 0 && getLong != 0) {
                Log.d("service", "service enter");

                if (myInfo.getID() != null && !curLoca.equals(T_MAP_POINT)) {

                    Log.d("service", "service Execute");
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

                        if(distanceCalc(latitude, longitude, getLat, getLong)) {

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


            if(destinationCheck) {

                long time = System.currentTimeMillis();
                DateFormat df = new SimpleDateFormat("HHmmss", Locale.KOREA); // HH=24h, hh=12h
                String str = df.format(time);
                Integer Now, Duration;
                Now = Integer.parseInt(str);
                Duration = Now;
                Log.e("Current Time : ", str);
                try {
                    Duration = DestinationSelectActivity.Dochack - Now;
                    Log.d("Duration ", Duration.toString());
                } catch (NullPointerException e) {
                }

                if (Duration < -20 && check == 1) {
                    check=0;
                    Log.e("Send", sender.toString());

                    regID = RegID.getInstance();
                    myInfo = MyInfo.getInstance();
                    try {
                        String returnString = new LoadFamilyList().execute(myInfo.getGroupCode()).get();
                        JSONArray jsonArray = new JSONArray(returnString);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            message = new Message.Builder().addData("message", myInfo.getName()).build();
                            String code = jsonObject.getString("GCMCode");
                            Log.e("-------------","---------------");
                            Log.e("groupcode", myInfo.getGroupCode());
                            Log.e("jsonobject", jsonObject.getString("GCMCode"));
                            Log.e("who?", jsonObject.getString("id"));
                            Log.e("returnstring", returnString);
                            Log.e("gcmcode", code);
                            Log.e("message", message.toString());
                            Log.e("regID", GCMRegistrar.getRegistrationId(this));
                            Log.e("myInfo", myInfo.toString());
                            Log.e("sender", sender.send(message, code, 5).toString());


                            if (code != null) {
                                result = sender.send(message, code, 5);

                            }
                        }
                    } catch (Exception e) {
                        result = null;
                        e.printStackTrace();
                    }

                    if (result.getMessageId() != null) {
                        try {
                            System.out.println("푸쉬 테스트");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String error = result.getErrorCodeName();
                        System.out.println(error);

                        if (Constants.ERROR_INTERNAL_SERVER_ERROR.equals(error)) {
                            System.out.println("구글 서버 에러");
                        }
                    }
                }

            }

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
