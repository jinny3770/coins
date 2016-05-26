package coins.hansung.way.Destination;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import coins.hansung.way.R;
import coins.hansung.way.etc.APIKey;
import coins.hansung.way.etc.MyInfo;

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

    MyInfo myInfo;

    FloatingActionButton addFloating;
    String group_code;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_list);

        myInfo = MyInfo.getInstance();

        mapView = new TMapView(this);
        mapView.setSKPMapApiKey(APIKey.ApiKey);

        destinationListInfos = new ArrayList<DestinationListInfo>();

        group_code = MyInfo.getInstance().getGroupCode();

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipe.setOnRefreshListener(this);

        listView = (ListView) findViewById(R.id.destinationList);
        listView.setAdapter(listAdapter);

        addFloating = (FloatingActionButton) findViewById(R.id.listfab);
        addFloating.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listAdapter = new DestinationListAdapter(this);
        listView.setAdapter(listAdapter);

        DestinationListInfo info;
        TMapPoint startP = null, endP = null;

        try {
            LoadDestinationsList loadDestinationsList = new LoadDestinationsList();
            String str = loadDestinationsList.execute(group_code).get();

            Log.d("ListLoad", str);

            JSONArray jsonArray = new JSONArray(str);


            for (int i=0; i<jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);

                String id = obj.getString("id");
                String type = obj.getString("type");
                int time = obj.getInt("time");
                int code = obj.getInt("code");
                int distance = obj.getInt("distance");
                String resist = obj.getString("resist");


                info = new DestinationListInfo(id, time, distance, type, resist);

                String lineString = obj.getString("points");
                info.setLineString(lineString);
                info.setCode(code);

                JSONArray pointArray = obj.getJSONArray("points");


                for(int j=0; j<pointArray.length(); j++){
                    JSONArray point = pointArray.getJSONArray(j);

                    TMapPoint p = new TMapPoint(point.getDouble(0), point.getDouble(1));
                    if(j == 0) {
                        startP = p;
                    }
                    if(j == pointArray.length()-1) {
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
                //intent.putExtra("realLineString", destinationListInfos.get(position).getMyLinePoint());

                System.out.println(destinationListInfos.get(position).getDepartureTime());

                intent.putExtra("code", destinationListInfos.get(position).getCode());
                intent.putExtra("departureTime", destinationListInfos.get(position).getDepartureTime());
                Log.d("listActivity", "departure : " + destinationListInfos.get(position).getDepartureTime());

                intent.putExtra("arriveTime", destinationListInfos.get(position).getArriveTime());
                Log.d("listActivity", "arrive : " + destinationListInfos.get(position).getArriveTime());

                intent.putExtra("time", destinationListInfos.get(position).getTime());
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                if (myInfo.getID().equals(destinationListInfos.get(position).getID())) {

                    new AlertDialog.Builder(DestinationListActivity.this)
                            .setTitle("삭제 확인")
                            .setMessage("목적지를 삭제하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeleteDestinations deleteDestinations = new DeleteDestinations();
                                    deleteDestinations.execute(myInfo.getID());
                                    listAdapter.remove(position);

                                    SharedPreferences pref = getSharedPreferences("pref", 0);
                                    SharedPreferences.Editor prefEditor = pref.edit();

                                    prefEditor.putBoolean("destinationSet", false);
                                    prefEditor.remove("desCode");
                                    prefEditor.remove("pointOrder");
                                    prefEditor.remove("endPointLatitude");
                                    prefEditor.remove("endPointLongitude");
                                    prefEditor.commit();
                                }
                            })
                            .setNegativeButton("No", null).create().show();

                    return true;
                }
                return false;
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
            case R.id.listfab :
                Intent intent = new Intent(getApplicationContext(), DestinationSelectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

}
