package coins.hansung.way.Destination;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import coins.hansung.way.R;


/**
 * Created by sora on 2016-05-31.
 */
public class SpinnerAdapter extends ArrayAdapter<Integer> {

    Context context;
    Integer[] images;

    public SpinnerAdapter(Context context, Integer[] images) {

        super(context, R.layout.custom_spinner, images);

        this.context = context;
        this.images = images;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_spinner, null);

        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.spinnerImage);

        imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), images[position], null));

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_spinner, null);

        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.spinnerImage);

        imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), images[position], null));

        return convertView;
    }
}
