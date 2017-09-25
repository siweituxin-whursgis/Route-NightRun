package com.example.huyigong.route_nightrun.substances;

/**
 * 夜跑用户查询结果
 * Created by HuYG0 on 2017/8/5.
 */

public class NightRunnersInfo {
    private NightRunner[] NearPeople;

    public NightRunner[] getNearPeople() {
        return NearPeople;
    }

    public void setNearPeople(NightRunner[] nearPeople) {
        NearPeople = nearPeople;
    }
}
