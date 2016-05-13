package com.example.sora.coins.Destination;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sora on 2016-04-29.
 */
public class DestinationListInfo extends DestinationInfo {

    String ID;
    int code;

    String lineString;
    String myLineString;
    TMapPolyLine myLine;
    ArrayList<TMapPoint> myLinePoint;

    SimpleDateFormat dataFormat;
    Date resistDate;

    public DestinationListInfo(String ID, int time, int distance, String type, String timeStamp) throws ParseException {
        super(time, distance, type);

        dataFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        resistDate = dataFormat.parse(timeStamp);

        this.ID = ID;

    }

    public void setLineString(String lineString) {
        this.lineString = lineString;
    }

    public void setMyLineString(String myLineString) {
        this.myLineString = myLineString;
    }

    public void addLinePoint(TMapPoint point) {
        line.addLinePoint(point);
        linePoint.add(point);
    }

    public void addMyLinePoint(TMapPoint point) {
        myLine.addLinePoint(point);
        myLinePoint.add(point);
    }

    public ArrayList<TMapPoint> getMyLinePoint() {
        return myLinePoint;
    }

    public TMapPolyLine getMyLine() {
        return myLine;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getArriveTime() {

        long departureTime = resistDate.getTime() + time;

        Date depDate = new Date(departureTime);

        return dataFormat.format(depDate);
    }

    public String getDepartureTime() {

        return dataFormat.format(resistDate).toString();
    }

    public int getCode() {
        return code;
    }


    public String getLineString() {
        return lineString;
    }
}
