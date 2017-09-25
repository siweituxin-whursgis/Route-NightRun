package com.example.huyigong.route_nightrun.substances;

/**
 * Created by CS_Tao on 2017/8/7.
 */

public class DrinksInfo {
    private int mId;
    private String mName;
    private double mLng;
    private double mLat;

    public DrinksInfo(int id, String name, double lng, double lat)
    {
        mId = id;
        mName = name;
        mLng = lng;
        mLat = lat;
    }

    public void setId(int id)
    {
        mId = id;
    }

    public int getId()
    {
        return mId;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void setLng(double lng)
    {
        mLng = lng;
    }

    public double getLng()
    {
        return mLng;
    }

    public void setLat(double lat)
    {
        mLat = lat;
    }

    public double getLat()
    {
        return mLat;
    }
}
