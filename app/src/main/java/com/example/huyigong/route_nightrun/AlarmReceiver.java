package com.example.huyigong.route_nightrun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by HuYG0 on 2017/7/24.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO 需要实现闹钟界面，并在此处进行调用
        System.out.println("闹钟");
        Intent alertIntent = new Intent(context, AlertActivity.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alertIntent);
    }
}
