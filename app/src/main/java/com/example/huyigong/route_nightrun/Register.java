package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        final Button Regis_Stu;
        Regis_Stu = (Button)findViewById(R.id.Stu_Register);
        Regis_Stu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent register_Stu = new Intent(Register.this,Register_new.class);
                startActivity(register_Stu);
            }
        });
        final  Button OutRegisterBtn = (Button)findViewById(R.id.OutRegister);
        OutRegisterBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent registerOut = new Intent(Register.this,OutMenRegister.class);
                startActivity(registerOut);
            }
        });
       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //  public void onClick(View view) {
            //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
           //             .setAction("Action", null).show();
          //  }
       // });
    }

}
