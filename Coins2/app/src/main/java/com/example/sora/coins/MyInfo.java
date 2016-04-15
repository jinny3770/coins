package com.example.sora.coins;

/**
 * Created by sora on 2016-04-14.
 */
public class MyInfo extends PersonInfo {

    public static MyInfo info;

    private MyInfo() {
        super();
    }


    public static synchronized MyInfo getInstance() {

        if (info == null) {
            info = new MyInfo();
        }
        return info;
    }

    public void init() {
        info.setID(null);
        info.setName(null);
        info.setGroupCode(null);
        info.setPoint(null);
    }
}
