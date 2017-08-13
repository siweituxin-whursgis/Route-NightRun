package com.example.huyigong.route_nightrun;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningEstimateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningEstimateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningEstimateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RunningEstimateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunningEstimateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningEstimateFragment newInstance(String param1, String param2) {
        RunningEstimateFragment fragment = new RunningEstimateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // TODO: 添加监听器，此处暂时隐藏
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public int userId = 39;
    public int userWeight = 60;//kg
    Handler mRunningInfoHander;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_running_esimate, container, false);
        Button btn_run = (Button)view.findViewById(R.id.button_runcom);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context temp = getActivity();
                Intent intent_run = new Intent(temp, run_competition.class);
                temp.startActivity(intent_run);    //这里用getActivity().startActivity(intent);
            }
        });
        Button btn_estimate = (Button)view.findViewById(R.id.button_esitimate);
        btn_estimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context temp = getActivity();
                Intent intent_run = new Intent(temp, Calculate_KM.class);
                temp.startActivity(intent_run);    //这里用getActivity().startActivity(intent);
            }
        });
        Button btn_weight = (Button)view.findViewById(R.id.button_weight);
        btn_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context temp = getActivity();
                Intent intent_weight= new Intent(temp, weight_management.class);
                temp.startActivity(intent_weight);    //这里用getActivity().startActivity(intent);
            }
        });
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
                   // ((Button) view.findViewById(R.id.button_allmeter)).setText("累计路程：" + 2.1 + "km");
                   // ((Button) view.findViewById(R.id.button_cal)).setText("消耗卡路里：" + 230 + "cal");
                    ((Button) view.findViewById(R.id.button_allmeter)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final EditText inputServer = new EditText(getContext());
                          //  inputServer.setHint("周目标");
                            inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
                            Context temp = getActivity();
                            new AlertDialog.Builder(temp)
                                    .setTitle("总公里数")
                                    .setMessage("2.1KM")
                                    .setPositiveButton("确定", null)
                                    .setNegativeButton("取消", null)
                                    //  .setView(new EditText(getApplicationContext()))
                                   // .setView(inputServer)
                                    .show();
                        }
                    });
                    ((Button) view.findViewById(R.id.button_cal)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final EditText inputServer = new EditText(getContext());
                            //  inputServer.setHint("周目标");
                            inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
                            Context temp = getActivity();
                            new AlertDialog.Builder(temp)
                                    .setTitle("消耗卡路里")
                                    .setMessage("230大卡")
                                    .setPositiveButton("确定", null)
                                    .setNegativeButton("取消", null)
                                    //  .setView(new EditText(getApplicationContext()))
                                    // .setView(inputServer)
                                    .show();
                        }
                    });
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

        return view;

    }

    private void ShowToast(final String message, final int duration)
    {

    }
}
