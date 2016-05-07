package com.example.sora.coins.Destination;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.sora.coins.R;
import com.example.sora.coins.etc.APIKey;
import com.example.sora.coins.etc.MyInfo;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by sora on 2016-04-26.
 */
public class DestinationListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, ListView.OnLongClickListener{

    TMapView mapView;
    SwipeRefreshLayout swipe;

    ArrayList<DestinationListInfo> destinationListInfos;

    ListView listView;
    DestinationListAdapter listAdapter;


    FloatingActionButton addFloating;
    String group_code;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_list_activity);

        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(APIKey.ApiKey);

        destinationListInfos = new ArrayList<DestinationListInfo>();

        group_code = MyInfo.getInstance().getGroupCode();

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipe.setOnRefreshListener(this);

        listAdapter = new DestinationListAdapter(this);
        listView = (ListView) findViewById(R.id.destinationList);
        listView.setAdapter(listAdapter);

        addFloating = (FloatingActionButton) findViewById(R.id.addFloating);
        addFloating.setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        DestinationListInfo info;
        TMapPoint startP = null, endP = null;

        try {
            LoadDestinationsList loadDestinationsList = new LoadDestinationsList();
            String str = loadDestinationsList.execute(group_code).get();

            JSONArray jsonArray = new JSONArray(str);


            for (int i=0; i<jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);

                String id = obj.getString("id");
                String type = obj.getString("type");
                int time = obj.getInt("time");
                int distance = obj.getInt("distance");
                String resist = obj.getString("resist");
                info = new DestinationListInfo(id, time, distance, type, resist);

                String lineStirng = obj.getString("points");
                info.setLineString(lineStirng);

                JSONArray pointArray = obj.getJSONArray("points");

                for(int j=0; j<pointArray.length(); j++){
                    JSONArray point = pointArray.getJSONArray(j);

                    TMapPoint p = new TMapPoint(point.getDouble(0), point.getDouble(1));
                    System.out.println(p.getLatitude() + ", " + p.getLongitude());
                    if(j == 0) {
                        startP = p;
                    }
                    else if(j == pointArray.length()-1) {
                        endP = p;
                    }

                    info.addLinePoint(p);
                }
                listAdapter.addItem(type, startP, endP, id);
                destinationListInfos.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), DestinationViewActivity.class);
                intent.putExtra("lineString", destinationListInfos.get(position).getLineString());
                startActivity(intent);

            }
        });
    }

    @Override
    public void onRefresh() {
        //list 재구성
        swipe.setRefreshing(false);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.addFloating :
                Intent intent = new Intent(getApplicationContext(), DestinationSelectActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

}
