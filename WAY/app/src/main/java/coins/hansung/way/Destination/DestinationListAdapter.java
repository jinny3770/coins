package coins.hansung.way.Destination;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skp.Tmap.TMapAddressInfo;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import coins.hansung.way.R;

/**
 * Created by sora on 2016-04-29.
 */
public class DestinationListAdapter extends BaseAdapter {


    Context myContext = null;
    ArrayList<DestinationData> myListData = new ArrayList<DestinationData>();

    DestinationListAdapter(Context context) {
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
        holder.arrive.setText(myData.arrive);

        return convertView;
    }

    public void addItem(String type, TMapPoint startP, TMapPoint endP, String id) throws IOException, ParserConfigurationException, SAXException, ExecutionException, InterruptedException {

        TMapData tMapData = new TMapData();
        DestinationData data = new DestinationData();

        data.type = type;

        // id -> 이름 변환 어떻게 할 것인가? 생각해 볼 것.
        data.name = id;

        LoadLocationString loadLocationString = new LoadLocationString();
        data.departure = loadLocationString.execute(startP).get();

        LoadLocationString loadLocationString2 = new LoadLocationString();
        data.arrive = loadLocationString2.execute(endP).get();

        switch (type) {
            case "walk" :
                data.icon = this.myContext.getResources().getDrawable(R.drawable.ic_directions_walk_white_24dp);
                break;

            case "bicycle" :
                data.icon = this.myContext.getResources().getDrawable(R.drawable.ic_directions_bike_white_24dp);
                break;

            case "car" :
                data.icon = this.myContext.getResources().getDrawable(R.drawable.ic_local_taxi_white_24dp);
                break;
        }

        myListData.add(data);
    }

    public void remove(int position) {
        myListData.remove(position);
        dataChange();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void dataChange() {
        this.notifyDataSetChanged();
    }

    class LoadLocationString extends AsyncTask<TMapPoint, Void, String> {

        @Override
        protected String doInBackground(TMapPoint... params) {
            TMapPoint point = params[0];
            TMapData data = new TMapData();

            TMapAddressInfo addressInfo = null;
            try {
                addressInfo = data.reverseGeocoding(point.getLatitude(), point.getLongitude(), "A00");
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }

            String str = addressInfo.strCity_do + " " + addressInfo.strGu_gun + " "
                    + addressInfo.strLegalDong + " ";

            if (addressInfo.strBuildingName != null)
                str += addressInfo.strBuildingName;

            else str += addressInfo.strBunji;

            return str;
        }
    }
}
