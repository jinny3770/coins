package coins.hansung.way.Destination;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


/**
 * Created by sora on 2016-05-31.
 */
public class SpinnerAdapter extends BaseAdapter {

    Context myContext = null;
    ArrayList<Integer> imageIDs = new ArrayList<>();

    public SpinnerAdapter(Context context) {
        myContext = context;
    }

    @Override
    public int getCount() {
        return imageIDs.size();
    }

    @Override
    public Object getItem(int position) {
        return imageIDs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
