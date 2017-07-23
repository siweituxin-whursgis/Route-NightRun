package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class enroll_marathon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_marathon);
        Button backBtn = (Button)findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToCom = new Intent(enroll_marathon.this,run_competition.class);
                startActivity(backToCom);
            }
        });

        Button submitbtn = (Button)findViewById(R.id.submit_button);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent submit = new Intent(enroll_marathon.this,success_submit.class);
                startActivity(submit);
            }
        });

    }

}
