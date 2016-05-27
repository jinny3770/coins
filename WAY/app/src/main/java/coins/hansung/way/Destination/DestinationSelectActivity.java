package coins.hansung.way.Destination;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.StaticLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.ItemList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import coins.hansung.way.Main.LocationService;
import coins.hansung.way.R;
import coins.hansung.way.etc.APIKey;
import coins.hansung.way.etc.Links;
import coins.hansung.way.etc.MyInfo;

import android.os.Handler;

/**
 * Created by sora on 2016-04-21.
 */
public class DestinationSelectActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mapLayout;
    TMapView mapView;

    MyInfo myInfo;
    TMapPoint endPoint;

    EditText searchText;
    TextView searchButton;
    Spinner spinner;

    Integer[] spinnerImages = {R.drawable.ic_directions_walk_white_24dp, R.drawable.ic_directions_bike_white_24dp,
            R.drawable.ic_local_taxi_white_24dp};


    DestinationInfo destinationInfo;
    Date currentTime;

    TMapData tMapData;


    TextView titleView;
    ArrayList<MarkerOverlay> markerList;
    Bitmap marker;

    TextView resistAlarm;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    public static Integer Dochack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_select);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("도착알림");
        getSupportActionBar().setCustomView(R.layout.custom_destination_actionbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        myInfo = MyInfo.getInstance();

        pref = getSharedPreferences("pref", 0);
        prefEditor = pref.edit();

        currentTime = new Date();
        tMapData = new TMapData();

        mapLayout = (LinearLayout) findViewById(R.id.destinationView);

        mapView = new TMapView(this);
        mapView.setZoomLevel(14);

        mapView.setCenterPoint(myInfo.getPoint().getLongitude(), myInfo.getPoint().getLatitude());
        mapLayout.addView(mapView);

        Bitmap departure = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_departure);
        Bitmap arrival = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_arrival);
        marker = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_place);

        mapView.setTMapPathIcon(departure, arrival);

        spinner = (Spinner) findViewById(R.id.selectSpinner);

        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (TextView) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        markerList = new ArrayList<MarkerOverlay>();

        // action bar
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_destination_actionbar);

        resistAlarm = (TextView) findViewById(R.id.resistAlarm);
        resistAlarm.setOnClickListener(this);


        titleView = (TextView) findViewById(R.id.bubble_title);
    }


    @Override
    public void onClick(View v) {

        TMapMarkerItem item;
        AppCompatDialog dialog;

        switch (v.getId()) {

            case R.id.resistAlarm:
                dialog = sendDialogMaker();
                dialog.show();
                break;


            case R.id.searchButton:
                if (searchText.getText().toString().length() > 0) {
                    TMapData tmapData = new TMapData();


                    for (int i = 0; i < markerList.size(); i++) {
                        mapView.removeMarkerItem2(markerList.get(i).getID());
                    }

                    tmapData.findAllPOI(searchText.getText().toString(), new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {

                            mapView.removeAllTMapPOIItem();
                            mapView.removeAllMarkerItem();
                            mapView.removeTMapPath();

                            Log.d("arrayList Size", Integer.toString(arrayList.size()));

                            if (arrayList.size() > 0) {
                                for (int i = 0; i < arrayList.size(); i++) {

                                    TMapPOIItem tmp = arrayList.get(i);
                                    Log.d("arrayList", "id : " + tmp.getPOIID());
                                    Log.d("arrayList", "name : " + tmp.getPOIName());
                                    Log.d("arrayList", "lon : " + tmp.getPOIPoint().getLongitude() + ", lat : " + tmp.getPOIPoint().getLatitude());

                                    MarkerOverlay overlay = new MarkerOverlay(getApplicationContext(), mapView, tmp.getPOIName());

                                    overlay.setID(tmp.getPOIID());
                                    overlay.setName(tmp.getPOIName());
                                    overlay.setTMapPoint(tmp.getPOIPoint());
                                    overlay.setIcon(marker);
                                    overlay.setPosition((float) 0.5, (float) 1.0);

                                    markerList.add(overlay);
                                    mapView.addMarkerItem2(tmp.getPOIID(), overlay);

                                }

                                mapView.setCenterPoint(arrayList.get(0).getPOIPoint().getLongitude(), arrayList.get(0).getPOIPoint().getLatitude());
                                mapView.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
                                    @Override
                                    public void onCalloutMarker2ClickEvent(String s, TMapMarkerItem2 tMapMarkerItem2) {

                                        for (int i = 0; i < markerList.size(); i++) {
                                            mapView.removeMarkerItem2(markerList.get(i).getID());
                                        }

                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                resistAlarm.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        endPoint = tMapMarkerItem2.getTMapPoint();

                                        String url = null;
                                        String sLati = Double.toString(myInfo.getPoint().getLatitude());
                                        String sLong = Double.toString(myInfo.getPoint().getLongitude());
                                        String eLati = Double.toString(tMapMarkerItem2.getTMapPoint().getLatitude());
                                        String eLong = Double.toString(tMapMarkerItem2.getTMapPoint().getLongitude());
                                        String type = null;


                                        switch (spinner.getSelectedItemPosition()) {

                                            case 0:
                                                url = Links.walkURL;
                                                type = "walk";
                                                break;

                                            case 1:
                                                url = Links.bicycleURL;
                                                type = "bicycle";
                                                break;

                                            case 2:
                                                url = Links.carURL;
                                                type = "car";
                                                break;
                                        }

                                        RouteTask routeTask = new RouteTask();
                                        try {
                                            beforeRoute(routeTask.execute(url, sLati, sLong, eLati, eLong, type).get().toString(), type);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                            }

                            else{
                                ToastTask toastTask = new ToastTask();
                                toastTask.execute();

                            }
                        }
                    });
                }

                resistAlarm.setVisibility(View.INVISIBLE);
                break;


        }
    }

    public void beforeRoute(String str, String tp) {
        try {
            JSONObject topObj = new JSONObject(str);

            // features의 배열 받아옴.
            JSONArray featureArray = topObj.getJSONArray("features");

            //feature obj 중 properties obj만 가져옴.
            JSONObject featObj = featureArray.getJSONObject(0).getJSONObject("properties");

            int time = featObj.getInt("totalTime");
            int dis = featObj.getInt("totalDistance");

            destinationInfo = new DestinationInfo(time, dis, tp);
            destinationInfo.initLinePoint();


            //feature obj 중 geometry obj만 가져옴 -> 경로 표시를 위해서
            for (int k = 0; k < featureArray.length(); k++) {
                JSONObject geoObj = featureArray.getJSONObject(k).getJSONObject("geometry");
                JSONArray cooArray = geoObj.getJSONArray("coordinates");
                String type = geoObj.getString("type");
                TMapPoint point;

                if (type.equals("Point")) {

                    point = new TMapPoint(cooArray.getDouble(1), cooArray.getDouble(0));

                    if (!destinationInfo.compareLastPoint(point)) {
                        destinationInfo.addLinePoint(point);
                    }

                } else if (type.equals("LineString")) {

                    for (int i = 0; i < cooArray.length(); i++) {

                        JSONArray obj = cooArray.getJSONArray(i);
                        point = new TMapPoint(obj.getDouble(1), obj.getDouble(0));

                        if (!destinationInfo.compareLastPoint(point)) {
                            destinationInfo.addLinePoint(point);
                        }
                    }
                }


            }
            mapView.addTMapPath(destinationInfo.getLine());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private AppCompatDialog sendDialogMaker() {

        TextView time, distance;

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.destination_alert, null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DestinationSelectActivity.this);

        builder.setTitle("도착 알림");
        builder.setView(dialogView);

        try {
            time = (TextView) dialogView.findViewById(R.id.timeView);
            distance = (TextView) dialogView.findViewById(R.id.distanceView);

            time.setText("소요 시간은 " + transformTime(destinationInfo.getTime()) + " 이며,");
            distance.setText("예상 이동거리는 " + transformDistance(destinationInfo.getDistance()) + "km 입니다.");

            long timet = System.currentTimeMillis();
            DateFormat df = new SimpleDateFormat("HHmmss", Locale.KOREA); // HH=24h, hh=12h
            String str = df.format(timet);
            Integer Now;
            Now = Integer.parseInt(str);


            Dochack = destinationInfo.getTime() + Integer.parseInt(str);
            Intent service = new Intent(this, LocationService.class);           //추가!!
            startService(service);

            builder.setPositiveButton("등록합니다.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (!pref.getBoolean("destinationSet", false)) {

                        Log.d("destinationSet", Boolean.toString(pref.getBoolean("destinationSet", false)));

                        ResistDestination resistDestination = new ResistDestination();
                        resistDestination.execute(destinationInfo);

                    } else {
                        Log.d("destinationSet", Boolean.toString(pref.getBoolean("destinationSet", false)));
                        Toast.makeText(getApplicationContext(), "이미 등록 된 목적지가 있습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });

            builder.setNegativeButton("등록하지 않습니다.", null);

            AppCompatDialog dialog = builder.create();
            return dialog;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }


    class ResistDestination extends AsyncTask<DestinationInfo, Void, String> {

        BufferedReader reader = null;


        @Override
        protected String doInBackground(DestinationInfo... params) {

            DestinationInfo info = params[0];

            try {

                URL url = new URL(Links.resistURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(makeData(info, myInfo));
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = reader.readLine();

                return line;

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("fail")) {
                Toast.makeText(getApplicationContext(), "등록에 실패했습니다.", Toast.LENGTH_LONG).show();

            } else {

                prefEditor.putBoolean("destinationSet", true);
                prefEditor.putInt("desCode", Integer.parseInt(s));
                prefEditor.putInt("pointOrder", 2);
                prefEditor.putFloat("endPointLatitude", (float) endPoint.getLatitude());
                prefEditor.putFloat("endPointLongitude", (float) endPoint.getLongitude());

                prefEditor.commit();

                Log.d("prefCheck", Boolean.toString(pref.getBoolean("destinationSet", false)) + ", "
                        + Integer.toString(pref.getInt("desCode", 0)));
                Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private String makeData(DestinationInfo info, MyInfo myInfo) throws JSONException, UnsupportedEncodingException {

        String data = null;

        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        ArrayList<TMapPoint> points = info.getLinePoint();

        obj.put("ID", myInfo.getID());
        obj.put("CODE", myInfo.getGroupCode());
        obj.put("TIME", info.getTime());
        obj.put("DISTANCE", info.getDistance());
        obj.put("TYPE", info.getType());

        for (int i = 0; i < points.size() - 1; i++) {

            TMapPoint p = points.get(i);
            JSONArray tmp = new JSONArray();

            tmp.put(p.getLatitude());
            tmp.put(p.getLongitude());

            arr.put(tmp);
        }

        obj.put("POINTS", arr);
        Log.i("dataLog", obj.toString());

        data = URLEncoder.encode("json", "UTF-8") + "=" + URLEncoder.encode(obj.toString(), "UTF-8");
        return data;
    }

    String transformDistance(int distance) {

        double kmDis = distance / 1000.0;
        String str = String.format("%.1f", kmDis);

        return str;
    }

    String transformTime(int time) {

        int hour = time / 3600;
        int minute = time / 60;

        String str = "";

        if (hour != 0)
            str += Integer.toString(hour) + "시간 ";

        str += Integer.toString(minute) + "분";

        return str;
    }

    class ToastTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
