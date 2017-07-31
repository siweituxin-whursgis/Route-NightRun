package com.example.huyigong.route_nightrun.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by HuYG0 on 2017/7/31.
 */

public class CalledListenerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("push_service", "push_service start");
        // 创建新线程
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
