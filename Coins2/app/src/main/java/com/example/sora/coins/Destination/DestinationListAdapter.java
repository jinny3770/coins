package com.example.sora.coins.Destination;

import android.content.Context;
import android.graphics.Point;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sora.coins.Main.FamilyListData;
import com.example.sora.coins.R;
import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by sora on 2016-04-29.
 */
public class DestinationListAdapter extends BaseAdapter {


    Context myContext = null;
    ArrayList<DestinationData> myListData = new ArrayList<DestinationData>();

    DestinationListAdapter (Context context) {
        this.myContext = context;
    }

    @Override
    public int getCount() {
        return myListData.size();
    }

    @Override
    public Object getItem(int position) {
        return myListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DestinationHolder holder;

        if(convertView == null) {
            holder = new DestinationHolder();

            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.destination_list_shape, null);

            holder.icon = (ImageView) convertView.findViewById(R.id.typeImage);
            holder.arrive = (TextView) convertView.findViewById(R.id.arriveText);
            holder.departure = (TextView) convertView.findViewById(R.id.departureText);
            holder.name = (TextView) convertView.findViewById(R.id.nameText);

            convertView.setTag(holder);
        }
        else {
            holder = (DestinationHolder) convertView.getTag();
        }

        DestinationData myData = myListData.get(position);

        if(myData.icon != null) {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageDrawable(myData.icon);
        }

        else {
            //holder.icon.setVisibility();
        }

        holder.name.setText(myData.name);
        holder.departure.setText(myData.departure);
        holder.arrive.setText(myData.departure);

        return convertView;
    }

    public void addItem(String type, TMapPoint startP, TMapPoint endP, String id) throws IOException, ParserConfigurationException, SAXException {

        TMapData tMapData = new TMapData();
        TMapAddressInfo addressInfo;
        DestinationData data = new DestinationData();

        data.type = type;

        // id -> 이름 변환 어떻게 할 것인가? 생각해 볼 것.
        data.name = id;

        addressInfo = tMapData.reverseGeocoding(startP.getLatitude(), startP.getLongitude(), "A00");
        data.departure = addressInfo.strCity_do + " " +  addressInfo.strGu_gun + " "
               + addressInfo.strLegalDong + " " + addressInfo.strBunji;

        addressInfo = tMapData.reverseGeocoding(endP.getLatitude(), endP.getLongitude(), "A00");
        data.arrive = addressInfo.strCity_do + " " +  addressInfo.strGu_gun + " "
                + addressInfo.strLegalDong + " " + addressInfo.strBunji;

        switch (type) {
            case "walk" :
                // data.icon =
                break;

            case "bicycle" :
                break;

            case "car" :
                break;
        }

        myListData.add(data);
    }

    public void remove(int position) {

    }

    public void dataChange() {

    }
}
