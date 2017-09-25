package com.example.huyigong.route_nightrun.substances;

/**
 * 夜跑用户
 * Created by HuYG0 on 2017/8/5.
 */

public class NightRunner {
    private int UserID;
    private String UserName;
    private String UserAddress;
    private int UserGender;
    private int UserAverageRunTime;
    private double PositionLat;
    private double PositionLng;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getUserGender() {
        return UserGender;
    }

    public void setUserGender(int userGender) {
        UserGender = userGender;
    }

    public int getUserAverageRunTime() {
        return UserAverageRunTime;
    }

    public void setUserAverageRunTime(int userAverageRunTime) {
        UserAverageRunTime = userAverageRunTime;
    }

    public double getPositionLat() {
        return PositionLat;
    }

    public void setPositionLat(double positionLat) {
        PositionLat = positionLat;
    }

    public double getPositionLng() {
        return PositionLng;
    }

    public void setPositionLng(double positionLng) {
        PositionLng = positionLng;
    }

    public String getUserAddress() {
        return UserAddress;
    }

    public void setUserAddress(String userAddress) {
        UserAddress = userAddress;
    }
}
