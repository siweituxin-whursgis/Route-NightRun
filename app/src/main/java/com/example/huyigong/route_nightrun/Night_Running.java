package com.example.huyigong.route_nightrun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Night_Running extends FragmentActivity implements OnClickListener {
    public running_esimate fg_running_esimate;
    public running fg_running;
    public talk fg_talk;
    public safe fg_safe;

    private FrameLayout flayout;
    private RelativeLayout course_layout;
    private RelativeLayout found_layout;
    private RelativeLayout settings_layout;
    private ImageView running_esimate_image;
    private ImageView  running_image;
    private ImageView talk_image;
    private ImageView safe_image;
    private RadioButton running_esimate_r;
    private RadioButton running;
    private RadioButton talk_RBtn;
    private RadioButton safe_RBtn;
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int blue =0xFF0AB2FB;
    FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night__running);
        fManager = getSupportFragmentManager();
        initViews();
    }
    public void initViews()
    {
        running_esimate_r = (RadioButton)findViewById(R.id.running_estimate);
        running = (RadioButton)findViewById(R.id.running);
        talk_RBtn = (RadioButton)findViewById(R.id.runningAppointment);
        safe_RBtn = (RadioButton)findViewById(R.id.running_safety);
        running_esimate_r.setOnClickListener(this);
        running.setOnClickListener(this);
        talk_RBtn.setOnClickListener(this);
        safe_RBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.running_estimate:
                setChioceItem(0);
                break;
            case R.id.running:
                setChioceItem(1);
                break;
            case R.id.runningAppointment:
                setChioceItem(2);
                break;
            case R.id.running_safety:
                setChioceItem(3);
                break;
            default:
                break;
        }
    }
    public void setChioceItem(int index)
    {
        FragmentTransaction transaction = fManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);
        switch (index){
            case 0 :
                running_esimate_r.setTextColor(blue);
               if(fg_running_esimate==null){
                   Fragment fragment = new running_esimate();
                transaction.add(R.id.content, fragment);}
                else{transaction.show(fg_running_esimate);}
                break;
            case 1 :
                running.setTextColor(blue);
                if(fg_running==null){ Fragment fragment = new running_esimate();
                    transaction.add(R.id.content, fragment);}
                else{transaction.show(fg_running);}
                break;
            case 2 :
                talk_RBtn.setTextColor(blue);
                if(fg_talk==null){ Fragment fragment = new running_esimate();
                    transaction.add(R.id.content, fragment);}
                else{transaction.show(fg_talk);}
                break;
            case 3 :
                safe_RBtn.setTextColor(blue);
                if(fg_safe==null){ Fragment fragment = new running_esimate();
                    transaction.add(R.id.content, fragment);}
                else{transaction.show(fg_safe);}
                break;
        }
        transaction.commit();
    }

    public void clearChioce(){

    }
    private void hideFragments(FragmentTransaction transaction) {
        if(fg_running_esimate!=null){
                transaction.hide(fg_running_esimate);
        }
        if(fg_running!=null){
            transaction.hide(fg_running);
        }
        if(fg_safe!=null){
            transaction.hide(fg_safe);
        }
        if(fg_talk!=null){
            transaction.hide(fg_talk);
        }

    }}
