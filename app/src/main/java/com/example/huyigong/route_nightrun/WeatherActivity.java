package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WeatherActivity extends AppCompatActivity {

    Button run_outside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 添加其他元素
        run_outside = (Button)findViewById(R.id.Button_night_run);
        run_outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent night_running = new Intent(WeatherActivity.this, NightRunActivity.class);
                startActivity(night_running);
            }
        });
    }
}
