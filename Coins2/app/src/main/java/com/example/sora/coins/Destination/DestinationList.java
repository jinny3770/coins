package com.example.sora.coins.Destination;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.sora.coins.Main.ListViewAdapter;
import com.example.sora.coins.R;

/**
 * Created by sora on 2016-04-26.
 */
public class DestinationList extends AppCompatActivity implements View.OnClickListener{

    SwipeRefreshLayout swipe;

    ListView listView;
    ListViewAdapter listAdapter;

    FloatingActionButton addFloating;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_list_activity);

        /*
        listView = (ListView) findViewById(R.id.destinationList);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        */
        addFloating = (FloatingActionButton) findViewById(R.id.addFloating);
        addFloating.setOnClickListener(this);
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
