package com.example.huyigong.route_nightrun;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huyigong.route_nightrun.helpers.WeatherApi;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WeatherActivity extends AppCompatActivity {
    Button run_outside;
    Handler mWeatherHandler;

    static String[] WEEK_NAME_STRING = {"日", "一", "二", "三", "四", "五", "六"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        final TextView city = (TextView) findViewById(R.id.city);
        final TextView temp = (TextView) findViewById(R.id.temp);
        final TextView wet = (TextView)findViewById(R.id.wet);

        mWeatherHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                if (bundle == null) {
                    Log.i("获取天气失败", "数据为空");
                    return;
                }
                try {
                    city.append(bundle.getString("city"));
                    temp.setText("30"+"°C");
                    wet.append(bundle.getString("SD"));
                    int ifrain = bundle.getInt("rain");
                    TextView accom = (TextView)findViewById(R.id.accom);
                    TextView wea = (TextView)findViewById(R.id.weather);
                    if(ifrain == 0){
                        accom.append("室外夜跑");
                        wea.setText("多云");
                    }
                    else{
                        accom.append("室内健身");
                        wea.setText("有雨");
                    }
                } catch (Exception e) {
                    System.out.println("获取天气失败：" + e.getMessage());
                }
            }
        };

        WeatherApi weatherApi = new WeatherApi(mWeatherHandler);
        weatherApi.run();

        // 添加其他元素
        run_outside = (Button)findViewById(R.id.Button_night_run);
        run_outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent night_running = new Intent(WeatherActivity.this, NightRunActivity.class);
                startActivity(night_running);
            }
        });

        Button indoor_run = (Button)findViewById(R.id.indoor_run);
        indoor_run.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent gotoIndoorRunning  = new Intent(WeatherActivity.this,IndoorRunActivity.class );
                startActivity(gotoIndoorRunning);
            }
        });

        // 修改日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        ((TextView) findViewById(R.id.date)).setText(String.valueOf(month) + "月" + String.valueOf(day) + "日" + " " + "星期" + WEEK_NAME_STRING[week]);
    }
}
