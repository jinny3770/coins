package coins.hansung.way.SideMenu;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

import coins.hansung.way.R;

/**
 * Created by Administrator on 2016-05-27.
 */

public class GroupListAdapter extends BaseAdapter
{
    private ArrayList <GroupListInfo> infoList = new ArrayList <GroupListInfo>();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Context context = parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_group_list, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.groupName) ;
        TextView phoneView = (TextView) convertView.findViewById(R.id.groupPhone) ;

        GroupListInfo info = infoList.get(position);

        nameView.setText(info.name);
        phoneView.setText(info.number);

        return convertView;
    }

    @Override
    public int getCount()
    {
        return infoList.size();
    }

    @Override
    public GroupListInfo getItem(int position)
    {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void addItem(String name, String number)
    {
        GroupListInfo item = new GroupListInfo(name, number);
        infoList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position)
    {
        infoList.remove(position);
        notifyDataSetChanged();
    }

    public boolean checkDuplication(String number)
    {
        for (int i = 0; i < getCount(); i++)
        {
            if (infoList.get(i).number.equals(number))
            {
                return true;
            }
        }

        return false;
    }
}