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

    DestinationListInfo(String ID, int time, int distance, String type) {
        super(time, distance, type);

        this.ID = ID;

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
