package com.example.huyigong.route_nightrun;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class Night_Running extends FragmentActivity implements OnClickListener {
    private RunningEstimateFragment fg_runningEstimateFragment;
    private RunningRunFragment fg_running;
    private RunningTalkFragment fg_talk;
    private RunningSafeFragment fg_runningSafeFragment;
    private FrameLayout flayout;
    private RelativeLayout course_layout;
    private RelativeLayout found_layout;
    private RelativeLayout settings_layout;
    private ImageView running_esimate_image;
    private ImageView  running_image;
    private ImageView talk_image;
    private ImageView safe_image;
    private RadioButton running_esimate;
    private RadioButton running;
    private RadioButton talk;
    private RadioButton safe;
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int blue =0xFF0AB2FB;
    FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }
}
