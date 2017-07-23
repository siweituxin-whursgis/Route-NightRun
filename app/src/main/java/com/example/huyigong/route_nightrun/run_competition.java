package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class run_competition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_competition);
        Button enrollBtn = (Button)findViewById(R.id.comp1);
        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent night_running = new Intent(run_competition.this, enroll_marathon.class);
                startActivity(night_running);
            }
        });
        Button enrollBtn2 = (Button)findViewById(R.id.comp2);
        enrollBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent night_running = new Intent(run_competition.this, enroll_marathon.class);
                startActivity(night_running);
            }
        });
        Button enrollBtn3 = (Button)findViewById(R.id.comp3);
        enrollBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent night_running = new Intent(run_competition.this, enroll_marathon.class);
                startActivity(night_running);
            }
        });
    }
}
