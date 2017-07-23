package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class weight_management extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_management);


        Button esitimateBMI = (Button)findViewById(R.id.bmi_button);

      esitimateBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText height =   (EditText)findViewById(R.id.height);
               String height_string = height.getText().toString();
                double height_int = Integer.parseInt(height_string);
                EditText weight = (EditText)findViewById(R.id.weight);
                String weight_string = weight.getText().toString();
                double weight_int = Integer.parseInt(weight_string);
                double bmi = weight_int / (height_int/100)/(height_int/100);
                TextView BMI = (TextView)findViewById(R.id.bmitext);
                BMI.append(String.valueOf(bmi));
            }
       });




    }
}
