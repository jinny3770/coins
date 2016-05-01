package com.example.sora.coins.Main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sora.coins.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sora on 2016-04-14.
 */
public class ListViewAdapter extends BaseAdapter
{
    private Context myContext = null;
    private ArrayList<ListData> myListData = new ArrayList<ListData>();

    public ListViewAdapter(Context mContext) {
        super();
        this.myContext = mContext;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.familylist, null);

            holder.icon = (ImageView) convertView.findViewById(R.id.familyPicture);
            holder.name = (TextView) convertView.findViewById(R.id.familyName);
            holder.loca = (TextView) convertView.findViewById(R.id.familyLoca);

            convertView.setTag(holder);
        }

        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ListData myData = myListData.get(position);

        if (myData.icon != null)
        {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageDrawable(myData.icon);
        }

        else
        {
            holder.icon.setVisibility(View.GONE);
        }

        holder.name.setText(myData.name);
        holder.loca.setText(myData.loca);

        return convertView;
    }

    public void addItem(Drawable icon, String name, String loca)
    {
        ListData addInfo = null;
        addInfo = new ListData();
        addInfo.icon = icon;
        addInfo.name = name;
        addInfo.loca = loca;

        myListData.add(addInfo);
    }

    public void remove(int position)
    {
        myListData.remove(position);
        dataChange();
    }

    public void sort()
    {
        Collections.sort(myListData, ListData.comparator);
        dataChange();
    }

    public void dataChange()
    {
        this.notifyDataSetChanged();
    }
}