package com.example.sora.coins.Destination;

import android.graphics.Color;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

/**
 * Created by sora on 2016-04-25.
 */
public class DestinationInfo {

    TMapPolyLine line;
    ArrayList<TMapPoint> linePoint;
    int time, distance;

    String type;

    DestinationInfo(int time, int distance, String type) {

        this.time = time;
        this.distance = distance;
        this.type = type;
        initLinePoint();
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

    public boolean compareLastPoint(TMapPoint point) {

        if (linePoint.size() == 0)
            return false;
        else if (linePoint.get(linePoint.size()-1).equals(point)) {
            return true;
        } else return false;
    }

    public void initLinePoint() {
        line = new TMapPolyLine();
        linePoint = new ArrayList<TMapPoint>();
        line.setLineWidth(5);
        line.setLineColor(Color.RED);
    }
}
