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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sora.coins.Chat.ChatActivity;
import com.example.sora.coins.Destination.DestinationListActivity;
import com.example.sora.coins.LockScreen.LockScreenService;
import com.example.sora.coins.R;
import com.example.sora.coins.Sidebar.LoginActivity;
import com.example.sora.coins.Sidebar.ProfileActivity;
import com.example.sora.coins.Sidebar.SideGroupActivity;
import com.example.sora.coins.Sidebar.SideSettingActivity;
import com.example.sora.coins.Sidebar.SideWarningActivity;
import com.example.sora.coins.etc.APIKey;
import com.example.sora.coins.etc.MyInfo;
import com.example.sora.coins.etc.PersonInfo;
import com.example.sora.coins.etc.Settings;
import com.google.android.gcm.GCMRegistrar;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private static final int defaultZoomLevel = 15;
    private static final int defaultMinDistance = 3;    // 단위 : m
    private static final int defaultMinTime = 1000;     // 단위 : 1/1000s


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
    RelativeLayout profileLayout;


    // 내 정보
    MyInfo myInfo;
    Settings settings;


    // 하단 가족 리스트뷰
    Boolean flag = true;
    ListView familyListView;
    FamilyListViewAdapter familyAdapter;

    ArrayList<PersonInfo> family;


    @Override
    protected void onResume() {
        super.onResume();

        // 잠금화면 호출 여부 처리
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Intent intent = new Intent(getApplicationContext(), LockScreenService.class);

        if (pref.getBoolean("swc", true)) {
            startService(intent);
        } else {
            stopService(intent);
        }

        // 현재 위치 설정
        curLoca = tMapGpsManager.getLocation();
        showMyLocation();


        // 로그인 여부 확인 후 가족 리스트 불러오기
        if (settings.getLogin()) {
            if (flag) {
                flag = false;

                try {
                    String returnString = new LoadFamilyList().execute(myInfo.getGroupCode()).get();
                    JSONArray jsonArray = new JSONArray(returnString);

                    // ArrayList에 저장하고 Listview에 추가
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PersonInfo pInfo = new PersonInfo();

                        pInfo.setID(jsonObject.getString("id"));
                        pInfo.setGroupCode(myInfo.getGroupCode());
                        pInfo.setName(jsonObject.getString("name"));
                        pInfo.setPoint(new TMapPoint(37.58141461906891, 127.00945944471735));
                        family.add(pInfo);
                        familyAdapter.addItem(getResources().getDrawable(R.drawable.ic_profile), family.get(i).getName(), pointToString(family.get(i).getPoint()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            family.clear();
            familyAdapter.notifyDataSetInvalidated();
        }

        familyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mapView.setCenterPoint(family.get(position).getPoint().getLongitude(), family.get(position).getPoint().getLatitude());

                TMapMarkerItem familyLoca = new TMapMarkerItem();
                familyLoca.setVisible(familyLoca.VISIBLE);
                familyLoca.setTMapPoint(family.get(position).getPoint());
                familyLoca.setIcon(bitmap);
                familyLoca.setPosition((float) 0.5, (float) 1.0);
                mapView.addMarkerItem("familyLocation", familyLoca);
            }
        });
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

        if ("".equals(regID)) {
            GCMRegistrar.register(this, "386569608668");
        } else {
            Log.e("id", regID);
        }


        // MyInfo instance 불러온다.
        myInfo = MyInfo.getInstance();
        settings = Settings.getInstance();

        // 상단 액션바 & 리스너
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF5e6472));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater actionBarInflater = LayoutInflater.from(this);
        View customView = actionBarInflater.inflate(R.layout.actionbar_layout, null);
        actionBar.setCustomView(customView); // 액션바 레이아웃 지정
        actionBar.setDisplayShowCustomEnabled(true); // 사용여부

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
        family = new ArrayList<PersonInfo>();
        familyAdapter = new FamilyListViewAdapter(this);
        familyListView = (ListView) findViewById(R.id.familyList);
        familyListView.setAdapter(familyAdapter);


        // 드로어 & 사이드바
        profileLayout = (RelativeLayout) findViewById(R.id.profileLayout);
        sideLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        sideLayout.setDrawerListener(sideToggle);

        sideToggle = new ActionBarDrawerToggle(this, sideLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            public void onDrawerClosed(View view) {
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
        if (sideToggle.onOptionsItemSelected(item)) {
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
        showMyLocation();
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


    public String pointToString(TMapPoint point) throws IOException, ParserConfigurationException, SAXException {
        TMapData data = new TMapData();
        return data.convertGpsToAddress(point.getLatitude(), point.getLongitude());
    }


    class CustomOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Intent intent;

            switch (v.getId()) {

                case R.id.chatButton:
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
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
                                    curLoca = tMapGpsManager.getLocation();
                                    mapView.setLocationPoint(curLoca.getLongitude(), curLoca.getLatitude());
                                    myLoca.setTMapPoint(curLoca);

                                    double lat = myLoca.getTMapPoint().getLatitude();
                                    double lon = myLoca.getTMapPoint().getLongitude();
                                    String address = "";

                                    try {
                                        address = pointToString(myLoca.getTMapPoint());
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                    intent = new Intent(getApplicationContext(), DestinationListActivity.class);
                    startActivity(intent);
                    break;

                case R.id.locaButton:
                    mapView.setTrackingMode(true);
                    showMyLocation();
                    break;
            }
        }
    }
}