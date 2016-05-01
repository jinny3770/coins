package com.example.sora.coins.Destination;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

/**
 * Created by sora on 2016-04-29.
 */
public class DestinationListInfo extends DestinationInfo {

    String ID;

    TMapPolyLine myLine;
    ArrayList<TMapPoint> myLinePoint;

    DestinationListInfo(TMapPoint start, TMapPoint end, int time, int distance) {
        super(start, end, time, distance);
    }

    DestinationListInfo(String time, String distance) {
        super(time, distance);
    }

    DestinationListInfo(String ID, TMapPoint start, TMapPoint end, int time, int distance, String type) {
        super(start, end, time, distance);

        this.ID = ID;
        this.type = type;

    }

    public void addLinePoint(TMapPoint point) {
        line.addLinePoint(point);
        linePoint.add(point);
    }

    public ArrayList<TMapPoint> getMyLinePoint(){
        return myLinePoint;
    }

    public TMapPolyLine getMyLine() {
        return myLine;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }
}
