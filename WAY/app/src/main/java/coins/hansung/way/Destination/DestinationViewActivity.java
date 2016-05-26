package coins.hansung.way.Destination;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import coins.hansung.way.R;
import coins.hansung.way.etc.APIKey;

/**
 * Created by sora on 2016-04-29.
 */

// listActivity에서 intent로 받아올 것
public class DestinationViewActivity extends AppCompatActivity {
    TMapView mapView;
    TMapPoint centerPoint;
    TMapPolyLine polyLine;
    TMapPolyLine myLine;

    String type;
    String arriveTimeString;
    String departureTimeString;
    int time;
    int code;


    ImageView typeView;
    TextView arriveTimeText;
    TextView departureTimeText;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_view);


        departureTimeText = (TextView) findViewById(R.id.departureTimeText);
        arriveTimeText = (TextView) findViewById(R.id.arriveTimeText);
        linearLayout = (LinearLayout) findViewById(R.id.desMapView);


        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(APIKey.ApiKey);

        Intent intent = getIntent();

        try {

            System.out.println(intent.getStringExtra("lineString"));
            polyLine = lineParsing(intent.getStringExtra("lineString"));
            type = intent.getStringExtra("type");
            departureTimeString = intent.getStringExtra("departureTime");
            arriveTimeString = intent.getStringExtra("arriveTime");
            time = intent.getIntExtra("time", 0);
            code = intent.getIntExtra("code", 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bitmap arrival = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_arrival);
        Bitmap departure = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_departure);


        mapView.setTMapPathIcon(departure, arrival);
        mapView.addTMapPath(polyLine);
        mapView.setZoomLevel(15);
        mapView.setCenterPoint(centerPoint.getLongitude(), centerPoint.getLatitude());


        typeView = (ImageView) findViewById(R.id.desImage);

        switch (type) {
            case "car" :
                typeView.setImageResource(R.drawable.ic_local_taxi_white_24dp);
                break;

            case "bicycle" :
                typeView.setImageResource(R.drawable.ic_directions_bike_white_24dp);
                break;

            case "walk" :
                typeView.setImageResource(R.drawable.ic_directions_walk_white_24dp);
                break;
        }
        departureTimeText.setText("출발 : " + departureTimeString);
        arriveTimeText.setText("도착 : " + arriveTimeString);

        linearLayout.addView(mapView);

    }

    @Override
    protected void onResume() {

        myLine = new TMapPolyLine();

        // 비교 line생성
        LoadMyPoint loadMyPoint = new LoadMyPoint();
        try {
            String str = loadMyPoint.execute(code).get();
            Log.d("loadMyPoint", str);

            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray =  jsonObject.getJSONArray("points");

            for(int i=0; i<jsonArray.length(); i++) {
                JSONArray arr = jsonArray.getJSONArray(i);

                Log.d("loadMyPoint", Integer.toString(i) + " : " + Double.toString(arr.getDouble(0)) + ", " + Double.toString(arr.getDouble(1)));
                TMapPoint point = new TMapPoint(arr.getDouble(0), arr.getDouble(1));
                myLine.addLinePoint(point);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        myLine.setLineWidth(10);
        myLine.setLineColor(Color.BLUE);
        mapView.addTMapPolyLine("myLine", myLine);

        super.onResume();
    }

    TMapPolyLine lineParsing(String s) throws JSONException {

        Log.i("lineParsing", s);
        TMapPolyLine line = new TMapPolyLine();

        JSONArray pointArray = new JSONArray(s);

        for (int i = 0; i < pointArray.length(); i++) {
            JSONArray point = pointArray.getJSONArray(i);

            TMapPoint p = new TMapPoint(point.getDouble(0), point.getDouble(1));
            if (i == 0) centerPoint = p;
            line.addLinePoint(p);
        }

        return line;

    }
}
