package com.example.sora.coins.etc;

import com.skp.Tmap.TMapPoint;

/**
 * Created by sora on 2016-03-23.
 */
public class PersonInfo {

    private String ID;
    private String name;
    private String groupCode;
    private TMapPoint point;


    public PersonInfo() {
        ID = new String();
        name = new String();
        groupCode = new String();
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoint(TMapPoint point) {
        this.point = point;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public TMapPoint getPoint() {
        return point;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
