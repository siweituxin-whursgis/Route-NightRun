package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Before_Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before__login);
       //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       // fab.setOnClickListener(new View.OnClickListener() {
         //   @Override
          //  public void onClick(View view) {
            //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
            //}
        //});
        Button gotoLogin = (Button)findViewById(R.id.GotoLogin);
        gotoLogin .setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent GTLogin = new Intent(Before_Login.this,LoginActivity.class);
                startActivity(GTLogin);
            }
        });
    }

}
