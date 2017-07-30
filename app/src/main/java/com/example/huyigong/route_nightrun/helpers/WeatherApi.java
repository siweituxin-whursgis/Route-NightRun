package com.example.huyigong.route_nightrun.helpers;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HuYG0 on 2017/7/30.
 */

public class WeatherApi {

    public final static String WEATHER_API_ADDRESS = "http://www.weather.com.cn/data/sk/101200101.html";

    /**
     * 获取下雨状况
     * @return -1:异常;0:不下雨;1:下雨.
     */
    static public int getRainState() {
        try {
            URL url = new URL(WEATHER_API_ADDRESS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            JSONTokener jsonTokener = new JSONTokener(br.readLine());
            JSONObject root = (JSONObject) jsonTokener.nextValue();
            return root.getInt("rain");
        } catch (Exception e) {
            System.out.println("获取天气失败");
            return -1;
        }

    }

}
