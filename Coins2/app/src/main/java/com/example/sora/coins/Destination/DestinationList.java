package com.example.sora.coins.Destination;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.sora.coins.Main.ListViewAdapter;
import com.example.sora.coins.R;
import com.example.sora.coins.etc.MyInfo;
import com.skp.Tmap.TMapPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by sora on 2016-04-26.
 */
public class DestinationList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    SwipeRefreshLayout swipe;

    ArrayList<DestinationListInfo> destinationListInfos;

    ListView listView;
    ListViewAdapter listAdapter;


    FloatingActionButton addFloating;
    String group_code;

    final static String loadURL = "http://52.79.124.54/loadDestinationsList.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_list_activity);

        destinationListInfos = new ArrayList<DestinationListInfo>();

        group_code = MyInfo.getInstance().getGroupCode();

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipe.setOnRefreshListener(this);

        listView = (ListView) findViewById(R.id.destinationList);

        addFloating = (FloatingActionButton) findViewById(R.id.addFloating);
        addFloating.setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        LoadDestinationsList loadDestinationsList = new LoadDestinationsList();
        loadDestinationsList.execute(group_code);

        // list adapter 넣기
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
    public void onRefresh() {
        //list 재구성
        
        swipe.setRefreshing(false);

    }

    class LoadDestinationsList extends AsyncTask<String, Void, String> {

        String gcode;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {
            gcode = params[0];

            try {
                URL url = new URL(loadURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(true);
                conn.setDoInput(true);

                String data = URLEncoder.encode("CODE", "utf8")  + "=" + URLEncoder.encode(gcode, "utf8");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder answer = new StringBuilder();
                String str;

                while ((str = reader.readLine()) != null) {
                    answer.append(str);
                }

                str = answer.toString();

                Log.i("dataLog", str);
                return str;

            } catch (Exception e) {
                return e.getMessage().toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {

            DestinationListInfo info;

            try {

                Log.i("dataLog", s);
                JSONArray jsonArray = new JSONArray(s);

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    String id = obj.getString("id");
                    String type = obj.getString("type");
                    int time = obj.getInt("time");
                    int distance = obj.getInt("distance");
                    info = new DestinationListInfo(id, time, distance, type);

                    JSONArray pointArray = obj.getJSONArray("points");

                    for(int j=0; j<pointArray.length(); j++){
                        JSONArray point = pointArray.getJSONArray(j);

                        TMapPoint p = new TMapPoint(point.getDouble(0), point.getDouble(1));
                        info.addLinePoint(p);
                    }

                    destinationListInfos.add(info);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
