package com.example.huyigong.route_nightrun.substances;

/**
 * Created by HuYG0 on 2017/8/1.
 */
public class GymsInfo {
    Gym[] gyms;

    public Gym[] getGyms() {
        return gyms;
    }

    public void setGyms(Gym[] gyms) {
        this.gyms = (gyms == null) ? new Gym[0] : gyms.clone();
    }

    public GymsInfo() {
        super();
    }
}
