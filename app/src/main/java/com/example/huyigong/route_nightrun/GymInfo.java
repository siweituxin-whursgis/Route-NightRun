package com.example.huyigong.route_nightrun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class GymInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_info);
        ArrayList<HashMap<String,Object>>gyms = new ArrayList<HashMap<String, Object>>();


        for(int i = 0;i<10;i++){
            HashMap<String,Object> gymitem = new HashMap<String,Object>();
            gymitem.put("gym_name","康斐斯健身（银泰创意城店）");
            gymitem.put("gym_loc","武汉市洪山区珞瑜路33号珞珈创意体验城11F层");
            gyms.add(gymitem);
        }
        SimpleAdapter gymItems = new SimpleAdapter(this,gyms,R.layout.gym_item,new String[]{"gym_name","gym_loc"},new int[]{R.id.gymName,R.id.gym_location});
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.gym_item);
        ((ListView)findViewById(R.id.list_gym)).setAdapter(gymItems);
    }
}
