package com.example.huyigong.route_nightrun.helpers;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by HuYG0 on 2017/7/30.
 */

public class WeatherApi extends Thread {
    public final static String WEATHER_API_ADDRESS = "http://www.weather.com.cn/data/sk/101200101.html";

    Handler mHandler;

    public WeatherApi(Handler handler) {
        mHandler = handler;
    }

    /**
     * 执行天气获取任务
     */
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = getRainStateJson();
                Bundle data = createBundleFromJson(jsonObject);
                Message message = new Message();
                message.setData(data);
                mHandler.sendMessage(message);
            }
        }).start();
    }


    static private Bundle createBundleFromJson(JSONObject jsonObject) {
        Bundle bundle = new Bundle();
        try {
            bundle.putCharSequence("city", jsonObject.getString("city"));
            bundle.putCharSequence("cityid", jsonObject.getString("cityid"));
            bundle.putInt("temp", jsonObject.getInt("temp"));
            bundle.putCharSequence("WD", jsonObject.getString("WD"));
            bundle.putCharSequence("WS", jsonObject.getString("WS"));
            bundle.putCharSequence("SD", jsonObject.getString("SD"));
            bundle.putCharSequence("WSE", jsonObject.getString("WSE"));
            bundle.putCharSequence("time", jsonObject.getString("time"));
            bundle.putInt("isRadar", jsonObject.getInt("isRadar"));
            bundle.putCharSequence("Radar", jsonObject.getString("Radar"));
            bundle.putCharSequence("njd", jsonObject.getString("njd"));
            bundle.putCharSequence("qy", jsonObject.getString("qy"));
            bundle.putInt("qy", jsonObject.getInt("qy"));
            return bundle;
        } catch (Exception e) {
            return null;
        }
    }

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
            JSONObject root = ((JSONObject) jsonTokener.nextValue()).getJSONObject("weatherinfo");
            return root.getInt("rain");
        } catch (Exception e) {
            System.out.println("获取天气失败");
            return -1;
        }
    }

    /**
     * 获取下雨状况
     * @return
     */
    static public JSONObject getRainStateJson() {
        try {
            URL url = new URL(WEATHER_API_ADDRESS);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            JSONTokener jsonTokener = new JSONTokener(br.readLine());
            JSONObject root = (JSONObject) jsonTokener.nextValue();
            return root.getJSONObject("weatherinfo");
        } catch (Exception e) {
            System.out.println("获取天气失败");
            return null;
        }
    }

}
