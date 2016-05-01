package com.example.sora.coins.Main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sora.coins.etc.APIKey;
import com.example.sora.coins.Destination.DestinationList;
import com.example.sora.coins.LockScreen.LockScreenService;
import com.example.sora.coins.etc.MyInfo;
import com.example.sora.coins.R;
import com.example.sora.coins.etc.PersonInfo;
import com.example.sora.coins.etc.Settings;
import com.example.sora.coins.Sidebar.LoginActivity;
import com.example.sora.coins.Sidebar.ProfileActivity;
import com.example.sora.coins.Sidebar.SideGroupActivity;
import com.example.sora.coins.Sidebar.SideSettingActivity;
import com.example.sora.coins.Sidebar.SideWarningActivity;
import com.example.sora.coins.Chat.ChatActivity;
import com.google.android.gcm.GCMRegistrar;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private static final int defaultZoomLevel = 15;
    private static final int defaultMinDistance = 3;    // 단위 : m
    private static final int defaultMinTime = 1000;     // 단위 : 1/1000s

    private static String updateLocationURL = "http://52.79.124.54/updateLocation.php";
    private static String loadFamilyURL = "http://52.79.124.54/loadFamilyList.php";


    // 상단 액션바 관련 변수
    ActionBar actionBar;
    ImageButton chatButton, locaButton, shareButton, desButton;
    CustomOnclickListener clickListener;

    // GPS 지도 관련 변수
    TMapView mapView;
    TMapPoint curLoca;
    TMapMarkerItem myLoca;
    TMapGpsManager tMapGpsManager;
    LinearLayout mapLayout;
    Bitmap bitmap;


    // 드로어 & 사이드바 관련 변수
    DrawerLayout sideLayout;
    ActionBarDrawerToggle sideToggle;
    NavigationView navigationView;
    /*boolean check;
    View v = getLayoutInflater().inflate(R.layout.side_header, null, false);*/

    // 내 정보
    MyInfo myInfo;
    Settings settings;

    // 하단 가족 리스트뷰
    ListView familyList;
    ListViewAdapter familyAdapter;

    ArrayList<PersonInfo> family;


    @Override
    protected void onResume() {
        super.onResume();

        // SideSetting의 값 받아서 조건처리
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Intent intent = new Intent(getApplicationContext(), LockScreenService.class);

        if (pref.getBoolean("swc", true))
        {
            startService(intent);
        }

        else
        {
            stopService(intent);
        }

        // 현재 위치 설정
        curLoca = tMapGpsManager.getLocation();
        showMyLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // android.os.NetworkOnMainThreadException 에러 방지용 코드
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // GCM
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regID = GCMRegistrar.getRegistrationId(this);

        if ("".equals(regID))
        {
            GCMRegistrar.register(this, "386569608668");
        }

        else
        {
            Log.e("id", regID);
        }


        // MyInfo instance 불러온다.
        myInfo = MyInfo.getInstance();
        settings = new Settings();

        // 상단 액션바
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF5e6472));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater actionBarInflater = LayoutInflater.from(this);
        View customView = actionBarInflater.inflate(R.layout.actionbar_layout, null);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);


        // 상단 액션바 리스너
        clickListener = new CustomOnclickListener();

        chatButton = (ImageButton) findViewById(R.id.chatButton);
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        locaButton = (ImageButton) findViewById(R.id.locaButton);
        desButton = (ImageButton) findViewById(R.id.destination);

        chatButton.setOnClickListener(clickListener);
        shareButton.setOnClickListener(clickListener);
        locaButton.setOnClickListener(clickListener);
        desButton.setOnClickListener(clickListener);


        // GPS 지도 setting 및 관련 리스너
        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        tMapGpsManager.setMinTime(defaultMinTime);
        tMapGpsManager.setMinDistance(defaultMinDistance);
        tMapGpsManager.OpenGps();

        curLoca = tMapGpsManager.getLocation();

        mapView = new TMapView(this);
        mapView.setTrackingMode(true);
        mapView.setSKPMapApiKey(APIKey.ApiKey);
        mapView.setZoomLevel(defaultZoomLevel);
        mapView.setMapPosition(TMapView.POSITION_DEFAULT);

        mapLayout = (LinearLayout) findViewById(R.id.mapView);
        mapLayout.addView(mapView);

        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_place);


        // 가족 리스트
        familyList = (ListView) findViewById(R.id.familyList);
        familyAdapter = new ListViewAdapter(this);

        family = new ArrayList<PersonInfo>();

        // 드로어 & 사이드바
        //check = false;

        sideLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        sideLayout.setDrawerListener(sideToggle);

        sideToggle = new ActionBarDrawerToggle(this, sideLayout, R.string.app_name, R.string.app_name)
        {
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
            }
        };

        sideToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;

                switch (menuItem.getItemId()) {
                    case R.id.side_login: // 로그인
                        if (settings.getLogin()) {
                            intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }

                        break;

                    case R.id.side_group: // 그룹 설정
                        intent = new Intent(getApplicationContext(), SideGroupActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.side_alert: // 비상연락망 설정
                        intent = new Intent(getApplicationContext(), SideWarningActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.side_setting: // 환경 설정
                        intent = new Intent(getApplicationContext(), SideSettingActivity.class);
                        startActivity(intent);
                        break;
                }

                sideLayout.closeDrawers();
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        sideToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        sideToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (sideToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChange(Location location) {
        curLoca = tMapGpsManager.getLocation();
        mapView.setLocationPoint(curLoca.getLongitude(), curLoca.getLatitude());
        myLoca.setTMapPoint(curLoca);
        myInfo.setPoint(curLoca);

        /*
        if(Settings.Login) {
            // update하는 코드 넣자
            UpdateLcation update = new UpdateLcation();
            update.execute(myInfo.getID(), Double.toString(myInfo.getPoint().getLatitude()), Double.toString(myInfo.getPoint().getLongitude()));
        }
        */
    }

    @Override
    protected void onDestroy() {
        tMapGpsManager.CloseGps();
        super.onDestroy();
    }


    // 현재 위치 marker 설정
    protected void showMyLocation() {

        myLoca = new TMapMarkerItem();
        myLoca.setVisible(myLoca.VISIBLE);

        myLoca.setTMapPoint(curLoca);
        myLoca.setIcon(bitmap);
        myLoca.setPosition((float) 0.5, (float) 1.0);
        mapView.addMarkerItem("myLocation", myLoca);
    }

    // 가족 리스트 불러오기
    public void loadFamilyList()
    {
        if (settings.getLogin())
        {
            /*if (!check)
            {
                navigationView.addHeaderView(v);
                check = true;
            }*/

            familyList.setAdapter(familyAdapter);
            familyAdapter.addItem(getResources().getDrawable(R.drawable.person), "권순일1", "성북구 삼선동");

            familyList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    //loca = new TMapPoint(127.00948476791382, 37.581937771712205);
                    //ListData listData = (ListData)familyAdapter.getItem(position);
                    mapView.setCenterPoint(127.00948476791382, 37.581937771712205);
                    mapView.setTrackingMode(false);
                    //myLoca.setTMapPoint(curLoca);
                    //mapView.setTrackingMode(true);

                    /*
                    locaMarker = new TMapMarkerItem();
                    locaMarker.setVisible(locaMarker.VISIBLE);

                    locaMarker.setTMapPoint(loca);
                    locaMarker.setIcon(bitmap);
                    locaMarker.setPosition((float) 0.5, (float) 1.0);
                    mapView.addMarkerItem("famLocation", locaMarker);
                    */
                }
            });
        }
/*
        else
        {
            navigationView.removeHeaderView(v);
        }*/
    }

    public String pointToString(TMapPoint point) throws IOException, ParserConfigurationException, SAXException {
        TMapData data = new TMapData();
        return data.convertGpsToAddress(point.getLatitude(), point.getLongitude());
    }

    class UpdateLocation extends AsyncTask<String, Void, Void> {
        String data;

        @Override
        protected Void doInBackground(String... params)
        {
            String ID = params[0];
            String lat = params[1];
            String lon = params[2];
            URL url = null;

            try {
                url = new URL(updateLocationURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                        + "&" + URLEncoder.encode("lati", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8")
                        + "&" + URLEncoder.encode("long", "UTF-8") + "=" + URLEncoder.encode(lon, "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class LoadFamilyList extends AsyncTask <String, Void, String> {

        URL url = null;
        BufferedReader reader = null;
        String code;

        @Override
        protected String doInBackground(String... params) {

            code = params[0];
            String data = null;

            try {
                URL url = new URL(loadFamilyURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("CODE", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {

            JSONArray jsonArray;
            JSONObject obj;
            PersonInfo pInfo;

            if(!s.equals("fail")) {

                try {
                    jsonArray = new JSONArray(s);
                    pInfo = new PersonInfo();

                    for(int i=0; i<jsonArray.length(); i++) {
                        obj = jsonArray.getJSONObject(i);

                        pInfo.setID(obj.getString("id"));
                        pInfo.setGroupCode(code);
                        pInfo.setName(obj.getString("name"));
                        pInfo.setPoint(new TMapPoint(obj.getDouble("latitude"), obj.getDouble("longitude")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CustomOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Intent intent;

            switch (v.getId()) {

                case R.id.chatButton:
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    break;

                case R.id.shareButton:
                    intent = new Intent(getApplicationContext(), ChatActivity.class);

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("WAY")
                            .setMessage("내 위치를 채팅방에 공유?")
                            .setCancelable(false)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    double lat, lon;
                                    String address = "";
                                    TMapData tmapData = new TMapData();

                                    curLoca = tMapGpsManager.getLocation();
                                    mapView.setLocationPoint(curLoca.getLongitude(), curLoca.getLatitude());
                                    myLoca.setTMapPoint(curLoca);

                                    lat = myLoca.getTMapPoint().getLatitude();
                                    lon = myLoca.getTMapPoint().getLongitude();

                                    try
                                    {
                                        address = tmapData.convertGpsToAddress(lat, lon);
                                    }

                                    catch (MalformedURLException me)
                                    {
                                        me.printStackTrace();
                                    }

                                    catch (IOException ioe)
                                    {
                                        ioe.printStackTrace();
                                    }

                                    catch (ParserConfigurationException pce)
                                    {
                                        pce.printStackTrace();
                                    }

                                    catch (SAXException saxe)
                                    {
                                        saxe.printStackTrace();
                                    }

                                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show(); // 테스트
                                    intent.putExtra("Latitude", lat);
                                    intent.putExtra("Longitude", lon);
                                    intent.putExtra("address", address);
                                    intent.putExtra("Check", 1);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade, R.anim.hold);
                                }
                            })
                            .setNegativeButton("놉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                    break;

                case R.id.destination:
                    intent = new Intent(getApplicationContext(), DestinationList.class);
                    startActivity(intent);
                    break;

                case R.id.locaButton:
                    mapView.setTrackingMode(true);
                    break;
            }
        }
    }
}