package com.example.sora.coins.Destination;

import android.graphics.Color;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

/**
 * Created by sora on 2016-04-25.
 */
public class DestinationInfo {


    TMapPoint start, end;
    TMapPolyLine line;
    ArrayList<TMapPoint> linePoint;
    int time, distance;

    String type;

    DestinationInfo(TMapPoint start, TMapPoint end, int time, int distance) {

        this.start = start;
        this.end = end;
        this.time = time;
        this.distance = distance;
        initLinePoint();
    }

    DestinationInfo(String time, String distance) {
        this.time = Integer.parseInt(time);
        this.distance = Integer.parseInt(distance);
    }

    public String getStringTime() {
        return Double.toString(time);
    }

    public String getStringDistance() {
        return Double.toString(distance);
    }

    public int getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }

    public String getType() {
        return type;
    }

    public TMapPolyLine getLine() {
        return line;
    }

    public ArrayList<TMapPoint> getLinePoint() {
        return linePoint;
    }

    public String getStringLine() {
        String str = "[";
        TMapPoint tmp;


        for (int i = 0; i < linePoint.size(); i++) {
            tmp = linePoint.get(i);

            str += "[" + Double.toString(tmp.getLatitude()) + "," + Double.toString(tmp.getLongitude()) + "]";

            if (i != linePoint.size() - 1) {
                str += ",";
            }
        }

        str += "]";
        return str;
    }

    public void setTime(String time) {
        this.time = Integer.parseInt(time);
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = Integer.parseInt(distance);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setType(String type) {
        this.type = type;

    }

    public void addLinePoint(TMapPoint point) {
        line.addLinePoint(point);
        linePoint.add(point);
    }

    public void initLinePoint() {
        line = new TMapPolyLine();
        linePoint = new ArrayList<TMapPoint>();
        line.setLineWidth(5);
        line.setLineColor(Color.RED);
    }
}
