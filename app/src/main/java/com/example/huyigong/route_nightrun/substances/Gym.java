package com.example.huyigong.route_nightrun.substances;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HuYG0 on 2017/8/1.
 */
public class Gym implements Parcelable {
    int GymID;
    String GymName;
    String GymAddress;
    String GymCall;
    double GymLng;
    double GymLat;

    public int getGymID() {
        return GymID;
    }

    public void setGymID(int gymID) {
        GymID = gymID;
    }

    public String getGymName() {
        return GymName;
    }

    public void setGymName(String gymName) {
        GymName = gymName;
    }

    public String getGymAddress() {
        return GymAddress;
    }

    public void setGymAddress(String gymAddress) {
        GymAddress = gymAddress;
    }

    public String getGymCall() {
        return GymCall;
    }

    public void setGymCall(String gymCall) {
        GymCall = gymCall;
    }

    public double getGymLng() {
        return GymLng;
    }

    public void setGymLng(double gymLng) {
        GymLng = gymLng;
    }

    public double getGymLat() {
        return GymLat;
    }

    public void setGymLat(double gymLat) {
        GymLat = gymLat;
    }

    public Gym() {
        super();
    }

    public Gym(Parcel in) {
        GymID = in.readInt();
        GymName = in.readString();
        GymAddress = in.readString();
        GymCall = in.readString();
        GymLng = in.readDouble();
        GymLat = in.readDouble();
    }

    public static final Creator<Gym> CREATOR = new Creator<Gym>() {
        @Override
        public Gym createFromParcel(Parcel in) {
            return new Gym(in);
        }

        @Override
        public Gym[] newArray(int size) {
            return new Gym[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(GymID);
        dest.writeString(GymName);
        dest.writeString(GymAddress);
        dest.writeString(GymCall);
        dest.writeDouble(GymLng);
        dest.writeDouble(GymLat);
    }
}
