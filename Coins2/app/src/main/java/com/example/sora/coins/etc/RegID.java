package com.example.sora.coins.etc;

/**
 * Created by Administrator on 2016-05-09.
 */

public class RegID
{
    public static RegID regID;
    static String id;

    private RegID()
    {
        super();
    }

    public static synchronized RegID getInstance()
    {
        if (regID == null)
        {
            regID = new RegID();
        }

        return regID;
    }

    public String getRegID()
    {
        return id;
    }

    public void setRegID(String id)
    {
        RegID.id = id;
    }
}
