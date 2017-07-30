package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Call_Taxi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call__taxi);
        Button calling_taxi = (Button)findViewById(R.id.taxi_coming);
        calling_taxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent taxi_com =new Intent (Call_Taxi.this,taxi_coming.class);
                startActivity(taxi_com);
            }
        });
    }
}
