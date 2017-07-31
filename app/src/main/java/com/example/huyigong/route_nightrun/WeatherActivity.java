package com.example.huyigong.route_nightrun;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonToken;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huyigong.route_nightrun.helpers.WeatherApi;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import org.json.JSONTokener;
import org.json.JSONArray;
import org.json.JSONStringer;
import org.json.*;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    Button run_outside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        JSONObject infojson = WeatherApi.getRainStateJson();

        TextView city = (TextView) findViewById(R.id.city);
        TextView temp = (TextView) findViewById(R.id.temp);
        TextView wet = (TextView)findViewById(R.id.wet);
        try {
            city.append(infojson.getString("city"));
            temp.setText(infojson.getInt("temp")+"°C");
            wet.append(infojson.getString("SD"));
            int ifrain = infojson.getInt("rain");
            TextView accom = (TextView)findViewById(R.id.accom);
            TextView wea = (TextView)findViewById(R.id.weather);
            if(ifrain==0){
                accom.append("室外夜跑");
                wea.setText("多云");
            }
            else{
                accom.append("室内健身");
                wea.setText("有雨");
            }
        } catch (Exception e) {
            System.out.println("获取天气失败");

        }
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
                Intent gotoIndoorRunning  = new Intent(WeatherActivity.this,Indoor_run.class );
                startActivity(gotoIndoorRunning);
            }
        });
    }
}
