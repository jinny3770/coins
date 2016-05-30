package coins.hansung.way.Main;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import coins.hansung.way.Chat.ChattingActivity;
import coins.hansung.way.Destination.DestinationListActivity;
import coins.hansung.way.Intro.IntroMain;
import coins.hansung.way.LockScreen.LockScreenService;
import coins.hansung.way.NFC.NdefMessageParsing;
import coins.hansung.way.NFC.ParsingRecord;
import coins.hansung.way.NFC.ReadNFC;
import coins.hansung.way.NFC.TextRecord;
import coins.hansung.way.R;
import coins.hansung.way.SideMenu.AppInfoActivity;
import coins.hansung.way.SideMenu.GroupManageActivity;
import coins.hansung.way.SideMenu.ProfileIntroActivity;
import coins.hansung.way.SideMenu.SettingsActivity;
import coins.hansung.way.etc.APIKey;
import coins.hansung.way.etc.Family;
import coins.hansung.way.etc.FamilyMarkerResource;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;
import coins.hansung.way.etc.PersonInfo;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TMapGpsManager.onLocationChangedCallback {
    // navi, actionbar, fab 선언부
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView, navigationFooterView;


    // 지도 layout 관련 선언부
    LinearLayout linearLayout;
    TMapView mapView;


    // preference 선언부 - 자동 로그인 관련
    SharedPreferences loginPref;
    SharedPreferences.Editor loginPrefEditor;


    View afterLoginView;


    // profile 이름, id textView 및 handler
    TextView profileName, profileID;
    ImageView profileImage;


    MyInfo myinfo;

    // family list
    Boolean familyCheck = false;
    ListView familyList;
    Family familyInstance;
    ArrayList<PersonInfo> family;


    FamilyMarkerResource familyMarkerResource;
    ArrayList<Integer> familyMarkerResourceList;
    ArrayList<Bitmap> familyMarkerBitmap;


    FamilyListViewAdapter familyAdapter;
    ArrayList<TMapMarkerItem> familyMarker;
    int familyIndex;
    String familyLocationString;

    // GPS & 지도 관련 변수
    private static final int defaultZoomLevel = 17;
    private static final int defaultMinDistance = 3;    // 단위 : m
    private static final int defaultMinTime = 2000;


    static TMapGpsManager tMapGpsManager;

    TMapPoint myLocation;
    ImageButton locaButton;

    // my marker
    Bitmap myMarkerBitmap;
    TMapMarkerItem myMarker;


    // 위치공유 버튼
    TextView locationShare;
    Intent service;
    SharedPreferences defaultPref;


    // NFC 관련 변수
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter intentFilters[];
    String techLists[][];
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;
    public static final String CHARS = "0123456789ABCDEF";

    // 종료 관련 변수
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, ReadNFC.class));

        // NFC
        adapter = NfcAdapter.getDefaultAdapter(this);
        Intent targetIntent = new Intent(this, ReadNFC.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pendingIntent = PendingIntent.getActivity(this, 0, this.getIntent().setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndefFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFilters = new IntentFilter[]{ndefFilter,};
        techLists = new String[][]{new String[]{NfcF.class.getName()}};
        Intent passedIntent = getIntent();

        if (passedIntent != null) {
            String actionString = passedIntent.getAction();

            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(actionString)) {
                processTag(passedIntent);
            }
        }

        defaultPref = PreferenceManager.getDefaultSharedPreferences(this);

        /* 기본 구성 선언 및 기능 정의 */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setCustomView(R.layout.custom_main_actionbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myinfo.getID() != null) {
                    loadFamilyList();
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_drawer);
        navigationView.setNavigationItemSelectedListener(this);


        navigationFooterView = (NavigationView) findViewById(R.id.nav_view_footer);
        navigationFooterView.setNavigationItemSelectedListener(this);

        /* 지도 관련 layout 정의 */
        linearLayout = (LinearLayout) findViewById(R.id.mapView);

        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(APIKey.ApiKey);
        mapView.setTrackingMode(true);
        mapView.setZoomLevel(defaultZoomLevel);
        mapView.setMapPosition(TMapView.POSITION_DEFAULT);
        mapView.setTrackingMode(true);


        tMapGpsManager = new TMapGpsManager(this);
        tMapGpsManager.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        tMapGpsManager.setMinTime(defaultMinTime);
        tMapGpsManager.setMinDistance(defaultMinDistance);
        tMapGpsManager.OpenGps();

        myLocation = tMapGpsManager.getLocation();

        Log.d("Location", "latitude : " + Double.toString(myLocation.getLatitude()) + ", longitude : " + Double.toString(myLocation.getLongitude()));


        linearLayout.addView(mapView);

        service = new Intent(this, LocationService.class);
        startService(service);


        familyMarkerResource = new FamilyMarkerResource();
        familyMarkerResourceList = familyMarkerResource.getFamilyMarkerBitmap();
        familyMarkerBitmap = new ArrayList<>();

        for (int i = 0; i < familyMarkerResourceList.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), familyMarkerResourceList.get(i));
            familyMarkerBitmap.add(bitmap);
        }


        myMarkerBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.black);
        setMyMarker(myLocation);

        /* preference 정의 */
        loginPref = getSharedPreferences("Login", 0);
        loginPrefEditor = loginPref.edit();

        LayoutInflater inflater = getLayoutInflater();



        myinfo = MyInfo.getInstance();
        myinfo.setID(loginPref.getString("ID", null));
        myinfo.setGroupCode(loginPref.getString("Code", "000000"));
        myinfo.setName(loginPref.getString("Name", null));

        afterLoginView = inflater.inflate(R.layout.nav_header_main, null);
        afterLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileIntroActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        });
        profileID = (TextView) afterLoginView.findViewById(R.id.mainProfileID);
        profileName = (TextView) afterLoginView.findViewById(R.id.mainProfileName);
        profileID.setText(myinfo.getID());
        profileName.setText(myinfo.getName());
        profileImage = (ImageView) afterLoginView.findViewById(R.id.mainProfileImage);


        // 현재 위치 추적 버튼
        locaButton = (ImageButton) findViewById(R.id.locaButton);
        locaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familyCheck = false;
                mapView.setTrackingMode(true);
                mapView.setLocationPoint(myMarker.longitude, myMarker.latitude);
            }
        });

        locationShare = (TextView) findViewById(R.id.locationShare);
        locationShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("위치 공유")
                        .setMessage("내 위치를 채팅방에 공유하시겠습니까?")
                        .setCancelable(false)
                        .setNegativeButton("공유합니다.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mapView.setLocationPoint(myLocation.getLongitude(), myLocation.getLatitude());
                                //myMarker.setTMapPoint(myLocation);

                                double lat = myLocation.getLatitude();
                                double lon = myLocation.getLongitude();
                                String address = null;
                                //String message = "택시택시";

                                try {
                                    LoadLocationString loadLocationString = new LoadLocationString();
                                    address = loadLocationString.execute(myLocation).get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show(); // 테스트
                                intent.putExtra("Latitude", lat);
                                intent.putExtra("Longitude", lon);
                                intent.putExtra("address", address);
                                intent.putExtra("Check", 1);
                                //intent.putExtra("message", message);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade, R.anim.hold);
                            }
                        })
                        .setPositiveButton("공유하지 않습니다.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent passedIntent) {
        super.onNewIntent(passedIntent);

        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            byte tagID[] = tag.getId();
            Log.e("태그 ID : ", toHexString(tagID) + "\n");
        }

        if (passedIntent != null) {
            processTag(passedIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // NFC adapter 체크
        if (adapter != null) {
            adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
        }

        navigationView.removeHeaderView(navigationView.getHeaderView(0));

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = new Intent(getApplicationContext(), LockScreenService.class);
        Log.e("Lock Boolean", String.valueOf(pref.getBoolean("lock", true)));

        if (pref.getBoolean("lock", true)) {
            Log.e("LockScreen", "Start");
            startService(intent);
        } else {
            Log.e("LockScreen", "Stop");
            stopService(intent);
        }

        /* 자동 로그인이 되어 있으면*/
        if (loginPref.getBoolean("AutoLogin", false)) {
            Log.d("Login", "pref : true, into first if");

            myinfo.setID(loginPref.getString("ID", null));
            myinfo.setGroupCode(loginPref.getString("Code", "000000"));
            myinfo.setName(loginPref.getString("Name", null));

            int batteryP = getBatteryPercentage(getApplicationContext());
            UploadBattery uploadBattery = new UploadBattery();
            uploadBattery.execute(batteryP);

            // handler 이용하여 profile view 띄우기
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    profileID.setText(myinfo.getID());
                    profileName.setText(myinfo.getName());

                    try {
                        LoadImage loadImage = new LoadImage();
                        Bitmap bitmap = loadImage.execute(myinfo.getID()).get();

                        myinfo.setProfileImage(bitmap);
                        if (bitmap != null)
                            profileImage.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            navigationView.addHeaderView(afterLoginView);

            //initMyLocation(tMapGpsManager.getLocation());

            if (defaultPref.getBoolean("gps", true)) {
                loadFamilyList();
                //setMyMarker(myinfo.getPoint());

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        TextView gpsText = (TextView) findViewById(R.id.gpsText);
                        gpsText.setVisibility(View.INVISIBLE);
                    }
                });

            } else {
                if (family != null)
                    family.clear();
                familyList = null;
                mapView.removeAllMarkerItem();

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        TextView gpsText = (TextView) findViewById(R.id.gpsText);
                        gpsText.setVisibility(View.VISIBLE);
                    }
                });

                if(service != null)
                    stopService(service);
            }

        }
        /* 자동 로그인이 되어 있지 않으면*/
        else {
            Intent intro = new Intent(getApplicationContext(), IntroMain.class);
            startActivityForResult(intro, 10);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (intervalTime >= 0 && FINSH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
            Log.d("press back twice time.", "exit the process");
            finish();

        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(),"'뒤로'버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        /* 로그인 되어있을때만 동작 */
        if (!loginPref.getBoolean("autoLoginSet", false)) {

            if (id == R.id.nav_arrive) {
                if(!myinfo.getGroupCode().equals("000000")) {
                    intent = new Intent(getApplicationContext(), DestinationListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
                else Toast.makeText(getApplicationContext(), "그룹에 가입 후 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_chat) {

                if(!myinfo.getGroupCode().equals("000000")) {
                    intent = new Intent(getApplicationContext(), ChattingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                }
                else Toast.makeText(getApplicationContext(), "그룹에 가입 후 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_group) {
                intent = new Intent(getApplicationContext(), GroupManageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

            } else if (id == R.id.nav_setting) {
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

            } else if (id == R.id.nav_appInfo) {
                intent = new Intent(getApplicationContext(), AppInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

            } else if (id == R.id.nav_logout) {
                loginPrefEditor.clear();
                loginPrefEditor.commit();

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        navigationView.removeHeaderView(navigationView.getHeaderView(0));

                        family.clear();
                        familyMarker.clear();
                        mapView.removeAllMarkerItem();
                    }
                });

                startActivity(new Intent(this, IntroMain.class));
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void processTag(Intent passedIntent)
    {
        Parcelable rawMessage[] = passedIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMessage == null)
        {
            return;
        }

        Toast.makeText(getApplicationContext(), "스캔 성공", Toast.LENGTH_SHORT).show();
        Log.i("info", "rawMessage.length : " + rawMessage.length);

        NdefMessage message[];

        if (rawMessage != null)
        {
            message = new NdefMessage[rawMessage.length];

            for (int i = 0; i < rawMessage.length; i++)
            {
                message[i] = (NdefMessage) rawMessage[i];
                showTag(message[i]);
            }
        }
    }


    public static String toHexString(byte data[])
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < data.length; ++i)
        {
            stringBuilder.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(CHARS.charAt(data[i] & 0x0F));
        }

        return stringBuilder.toString();
    }


    private int showTag(NdefMessage message)
    {
        List<ParsingRecord> records = NdefMessageParsing.parse(message);
        final int size = records.size();

        for (int i = 0; i < size; i++)
        {
            ParsingRecord record = records.get(i);

            int recordType = record.getType();
            String recordString = "";

            if (recordType == ParsingRecord.TYPE_TEXT)
            {
                recordString = ((TextRecord) record).getText();
            }

            Log.e(recordString, "\n");

            Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
            intent.putExtra("message", recordString);
            intent.putExtra("Check", 2);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }

        return size;
    }


    void setMyMarker(TMapPoint mapPoint) {
        myMarker = new TMapMarkerItem();
        myMarker.setVisible(myMarker.VISIBLE);

        myMarker.setIcon(myMarkerBitmap);
        myMarker.setPosition((float) 0.5, (float) 1.0);
        myMarker.setTMapPoint(mapPoint);
        mapView.addMarkerItem("myMarker", myMarker);
        mapView.bringMarkerToFront(myMarker);
    }

    @Override
    public void onLocationChange(Location location) {
        TMapPoint point = new TMapPoint(location.getLatitude(), location.getLongitude());
        myLocation = point;

        if (!familyCheck)
            mapView.setLocationPoint(point.getLongitude(), point.getLatitude());

        mapView.bringMarkerToFront(myMarker);
        setMyMarker(point);
    }

    void loadFamilyList() {
        // 가족 List

        familyList = (ListView) findViewById(R.id.familyList);
        familyAdapter = new FamilyListViewAdapter(this);
        familyMarker = new ArrayList<TMapMarkerItem>();
        familyInstance = Family.getInstance();
        family = familyInstance.getFamilyArray();

        // family List Load
        familyList.setAdapter(familyAdapter);
        familyList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mapView.setCenterPoint(family.get(position).getPoint().getLongitude(), family.get(position).getPoint().getLatitude());
                mapView.bringMarkerToFront(familyMarker.get(position));
                mapView.setTrackingMode(false);

                familyCheck = true;
            }
        });

        try {
            String returnString = new LoadFamilyList().execute(myinfo.getGroupCode()).get();
            Log.i("FamilyLoading", returnString);

            JSONArray jsonArray = new JSONArray(returnString);
            Log.i("FamilyLoading", Integer.toString(jsonArray.length()));

            // ArrayList에 저장하고 Listview에 추가
            for (int i = 0, j = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PersonInfo pInfo = new PersonInfo();

                // 본인 제외한 리스트 pInfo에 적용
                if (!jsonObject.getString("id").equals(myinfo.getID())) {
                    Log.i("FamilyLoading", Integer.toString(i) + " : " + jsonObject.getString("id"));

                    familyIndex = j;
                    pInfo.setID(jsonObject.getString("id"));
                    pInfo.setGroupCode(myinfo.getGroupCode());
                    pInfo.setName(jsonObject.getString("name"));
                    pInfo.setPhoneNumber(jsonObject.getString("phoneNumber"));
                    pInfo.setBattery(jsonObject.getInt("battery"));

                    String str = jsonObject.getString("gps");
                    if (str.equals("1")) pInfo.setGpsSig(true);
                    else pInfo.setGpsSig(false);

                    Log.d("familyInfo", pInfo.getID() + ", " + pInfo.getName() + ", " + pInfo.getPhoneNumber());
                    Log.d("GPSSignal", pInfo.getGpsSig().toString());

                    LoadImage loadImage = new LoadImage();
                    Bitmap bitmap = loadImage.execute(pInfo.getID()).get();

                    if (bitmap == null)
                        bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.person);


                    // 가족 위치 설정
                    TMapPoint pPoint = new TMapPoint(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                    pInfo.setPoint(pPoint);

                    TMapMarkerItem marker = initFamilyMarker(pPoint);
                    marker.setIcon(familyMarkerBitmap.get(j));
                    familyMarker.add(marker);
                    mapView.addMarkerItem("family" + j, marker);


                    // list에 추가
                    family.add(pInfo);


                    LoadLocationString loadLocationString = new LoadLocationString();
                    familyLocationString = loadLocationString.execute(family.get(familyIndex).getPoint()).get();
                    familyAdapter.addItem(getResources().getDrawable(familyMarkerResourceList.get(j)),
                    family.get(familyIndex).getName(), familyLocationString, family.get(familyIndex).getGpsSig(), family.get(familyIndex).getBattery(), bitmap);

                    j++;
                } else {
                    myinfo.setPoint(new TMapPoint(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")));
                    mapView.setLocationPoint(myinfo.getPoint().getLongitude(), myinfo.getPoint().getLatitude());

                }
            }

            familyInstance.setFamilyArray(family);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    TMapMarkerItem initFamilyMarker(TMapPoint point) {
        TMapMarkerItem marker = new TMapMarkerItem();
        marker.setVisible(marker.VISIBLE);
        marker.setTMapPoint(point);
        marker.setPosition((float) 0.5, (float) 1.0);
        marker.setIcon(myMarkerBitmap);
        return marker;
    }

    protected void initMyLocation(TMapPoint mapPoint) {
        myMarker = new TMapMarkerItem();
        myMarker.setVisible(myMarker.VISIBLE);

        myMarker.setIcon(myMarkerBitmap);
        myMarker.setPosition((float) 0.5, (float) 1.0);
        myMarker.setTMapPoint(mapPoint);
        mapView.addMarkerItem("myLocation", myMarker);
    }

    public static int getBatteryPercentage(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    class LoadLocationString extends AsyncTask<TMapPoint, Void, String> {
        @Override
        protected String doInBackground(TMapPoint... params) {

            TMapPoint point = params[0];
            TMapData data = new TMapData();

            TMapAddressInfo address = null;
            try {
                address = data.reverseGeocoding(point.getLatitude(), point.getLongitude(), "A00");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String str = address.strCity_do + " " + address.strGu_gun + " " + address.strLegalDong;

            if (address.strBuildingName != null) {
                str += (" " + address.strBuildingName);
            }

            return str;
        }
    }

    class UploadBattery extends AsyncTask<Integer, Void, String> {

        URL url;
        String data;
        int battery;


        @Override
        protected String doInBackground(Integer... params) {

            try {

                battery = params[0];

                url = new URL(Links.uploadBatteryURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(myinfo.getID(), "UTF-8") + "&" +
                        URLEncoder.encode("Battery", "UTF-8") + "=" + URLEncoder.encode(Float.toString(battery), "UTF-8");


                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str = reader.readLine();

                return str;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("UploadBattery", e.getMessage());
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("fail")) {
                Toast.makeText(getApplicationContext(), "배터리 잔량 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class LoadImage extends AsyncTask<String, Void, Bitmap> {

        Bitmap bm;

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(Links.serverURL + "/profileImage/" + params[0] + ".jpg");
                Log.d("LoadImage", url.toString());

                URLConnection conn = url.openConnection();
                conn.connect();

                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                bm = BitmapFactory.decodeStream(bis);
                bis.close();

                return bm;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("LoadImage", e.getMessage());
                return null;
            }

        }
    }
}