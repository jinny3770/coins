package com.example.sora.coins;

import com.skp.Tmap.TMapPoint;

import java.util.ArrayList;

/**
 * Created by sora on 2016-04-25.
 */
public class DestinationInfo {


    TMapPoint start, end;

    double time, distance;


    DestinationInfo(TMapPoint start, TMapPoint end, double time, double distance) {

        this.start = start;
        this.end = end;
        this.time =  time;
        this.distance = distance;
    }

    DestinationInfo(String time, String distance) {
        this.time =  Integer.parseInt(time);
        this.distance = Integer.parseInt(distance);
    }

    public String getStringTime() {
        return Double.toString(time);
    }

    public String getStringDistance() {
        return Double.toString(distance);
    }

    public double getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public void setTime(String time) {
        this.time = Double.parseDouble(time);
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = Double.parseDouble(distance);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
