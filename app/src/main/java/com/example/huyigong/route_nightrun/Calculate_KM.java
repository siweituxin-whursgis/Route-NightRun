package com.example.huyigong.route_nightrun;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;


public class Calculate_KM extends AppCompatActivity {

    public int userId = 39;

    public int userWeight = 60;//kg

    Handler mRunningInfoHander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate__km);
        ShowAllEvaluating();
    }

    private void ShowAllEvaluating()
    {
        mRunningInfoHander = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                try
                {
                    String response = ((Bundle)msg.getData()).getString("RunningInfo");
                    JSONTokener jsonParser = new JSONTokener(response);
                    JSONObject json = (JSONObject)jsonParser.nextValue();
                    JSONArray runningInfoArray = json.getJSONArray("RunningInfo");
                    JSONObject runningInfoObject = runningInfoArray.getJSONObject(0);
                    String routeInfoString = runningInfoObject.getString("RouteInfo");

//                    routeInfoString = routeInfoString.substring(1, routeInfoString.length() - 2);

                    JSONTokener jsonParser1 = new JSONTokener(routeInfoString);
                    JSONObject json1 = (JSONObject)jsonParser1.nextValue();
                    Timestamp dateAndTime =Timestamp.valueOf(json1.getString("DateTime"));
                    int secondDuring = json1.getInt("During");
                    double runningDistance = json1.getDouble("UserRunningDistance");

                    long days = (System.currentTimeMillis() - dateAndTime.getTime()) / 86400000;

                    if(days >= 1)
                        ShowToast("本数据为" + days + "天前的记录，要坚持锻炼哦！", Toast.LENGTH_LONG);


                    double speed = runningDistance / secondDuring;//(m/s)
                    double minuteDuring = secondDuring / 60;//(min)
                    double calorie = userWeight * runningDistance * 1036 / 1000000;//跑步热量（kcal）＝体重（kg）×距离（公里）×1.036

                    TextView speedView = (TextView)findViewById(R.id.textView13);
                    {
                        DecimalFormat df = new DecimalFormat("0.0");
                        speedView.setText("配速：" + df.format(speed) + "m/s");
                    }

                    TextView duringView = (TextView)findViewById(R.id.textView14);
                    {
                        DecimalFormat df = new DecimalFormat("0");
                        duringView.setText("时长：" + df.format(minuteDuring) + "min");
                    }

                    TextView calorieView = (TextView)findViewById(R.id.textView15);
                    {
                        DecimalFormat df = new DecimalFormat("0.0");
                        calorieView.setText("卡路里：" + df.format(calorie) + "kcal");
                    }


                    TextView averageElevationView = (TextView)findViewById(R.id.textView20);
                    {
                        averageElevationView.setText("平均海拔：23.3m");
                    }


                    TextView averageDuringTimeView = (TextView)findViewById(R.id.textView21);
                    {
                        DecimalFormat df = new DecimalFormat("0");
                        averageDuringTimeView.setText("平均时长：" + df.format(minuteDuring) + "min");
                    }
                }
                catch (Exception ex)
                {
                    ShowToast("获取用户记录失败", Toast.LENGTH_SHORT);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_checkrunninginfo) + "?UserId=" + userId);
//                    URL url = new URL("http://192.168.0.100:4521/" + getString(R.string.webapi_root) + getString(R.string.webapi_checkrunninginfo) + "?UserId=" + userId);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(4000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String content = reader.readLine();
                    Bundle bundle = new Bundle();
                    bundle.putString("RunningInfo", content);
                    Message message = new Message();
                    message.setData(bundle);
                    mRunningInfoHander.sendMessage(message);
                }
                catch (Exception e)
                {
                    ShowToast("获取用户记录失败", Toast.LENGTH_SHORT);
                }
            }
        }).start();

        ((Button) findViewById(R.id.set_week_target)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(getApplicationContext());
                inputServer.setHint("周目标");
                inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(Calculate_KM.this)
                        .setTitle("设置周目标")
                        .setMessage("输入目标公里数")
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                      //  .setView(new EditText(getApplicationContext()))
                        .setView(inputServer)
                        .show();
            }
        });
    }

    private void ShowToast(final String message, final int duration)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, duration).show();
            }
        });
    }
}
