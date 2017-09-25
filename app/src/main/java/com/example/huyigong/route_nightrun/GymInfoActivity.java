package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huyigong.route_nightrun.substances.Gym;
import com.example.huyigong.route_nightrun.substances.GymsInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GymInfoActivity extends AppCompatActivity {
    Handler mGymInfoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_info);
        // 创建Handler接收健身房数据
        mGymInfoHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Gym[] gyms = (Gym[]) msg.getData().getParcelableArray("gyms");
                GymAdapter adapter = new GymAdapter(gyms);
                ((ListView)findViewById(R.id.list_gym)).setAdapter(adapter);

//                SimpleAdapter gymItems = new SimpleAdapter(getApplicationContext(),gyms,R.layout.gym_item,new String[]{"gym_name","gym_loc"},new int[]{R.id.gymName,R.id.gym_location});
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.gym_item);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String GYM_INFO_API = getResources().getString(R.string.webapi_host) + getResources().getString(R.string.webapi_root) + getResources().getString(R.string.webapi_gym_info);
                try {
                    URL url = new URL(GYM_INFO_API);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    String jsonString = reader.readLine();
                    GymsInfo collection = gson.fromJson(jsonString, GymsInfo.class);
                    Gym[] gyms = collection.getGyms();
                    bundle.putParcelableArray("gyms", gyms);
                    Message message = new Message();
                    message.setData(bundle);
                    mGymInfoHandler.sendMessage(message);
                } catch (Exception e) {
                    Log.i("获取健身房数据失败", e.getMessage());
                }
            }
        }).start();
    }

    class GymAdapter extends BaseAdapter {
        Gym[] gyms;

        public Gym[] getGyms() {
            return gyms;
        }

        public void setGyms(Gym[] gyms) {
            this.gyms = gyms;
        }

        public GymAdapter(Gym[] gymData) {
            if (gymData != null) {
                gyms = gymData.clone();
            } else {
                gyms = new Gym[0];
            }
        }

        @Override
        public int getCount() {
            return gyms.length;
        }

        @Override
        public Object getItem(int position) {
            return (position < gyms.length) ? gyms[position] : null;
        }

        @Override
        public long getItemId(int position) {
            return (position > gyms.length) ? gyms[position].getGymID() : -1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.gym_item, null);
            }
            TextView gymName = (TextView) convertView.findViewById(R.id.gym_name);
            TextView gymLocation = (TextView) convertView.findViewById(R.id.gym_location);
            Button button = (Button) convertView.findViewById(R.id.gotothere);
            gymName.setText(gyms[position].getGymName());
            gymLocation.setText(gyms[position].getGymAddress());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 修改为特定的导航
                    Intent intent = new Intent(GymInfoActivity.this, IndoorRunActivity.class);
                    intent.putExtra("Gym", gyms[position]);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}

