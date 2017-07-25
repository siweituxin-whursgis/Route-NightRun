package com.example.huyigong.route_nightrun;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Console;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningSafeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningSafeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningSafeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RunningSafeFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunningSafeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningSafeFragment newInstance(String param1, String param2) {
        RunningSafeFragment fragment = new RunningSafeFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safe, container, false);
        Button callPolice = (Button) view.findViewById(R.id.CallPolice);
        Button schoolPolice = (Button) view.findViewById(R.id.schoolPolice);
        Button setbackTime = (Button)view.findViewById(R.id.backTime);

        setbackTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);
//                new TimePickerDialog (RunningSafeFragment.this,new TimePickerDialog.OnTimeSetListener(){
//                    public void onTimeSet(TimePicker view,int hourOfDay,int miniute){
//                        Calendar calendar=Calendar.getInstance();
//                        calendar.setTimeInMillis(System.currentTimeMillis());
//                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
//                        calendar.set(Calendar.MINUTE,miniute);
//                        calendar.set(Calendar.SECOND,0);
//                        calendar.set(Calendar.MILLISECOND,0);
//
//                        Intent intent = new Intent();
//
//                    }
//                });

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new SetClockTimeListener(), 0, 0, true);

                timePickerDialog.setTitle("选择回寝时间");
                timePickerDialog.show();
            }
        });
        schoolPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent schoolPolice = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "027-87308337"));//直接拨打电话
//                  startActivity(dialIntent);
                    getActivity().startActivity(schoolPolice);
                } catch (SecurityException e) {
                    Toast.makeText(getContext(), "拨号权限被禁用" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        if (callPolice == null) {
            System.out.println("callPolice is null");
        }
        else {
            callPolice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "110"));//直接拨打电话
//                    startActivity(dialIntent);
                        getActivity().startActivity(dialIntent);
                    } catch (SecurityException e) {
                        Toast.makeText(getContext(), "拨号权限被禁用" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }); callPolice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "110"));//直接拨打电话
//                    startActivity(dialIntent);
                        getActivity().startActivity(dialIntent);
                    } catch (SecurityException e) {
                        Toast.makeText(getContext(), "拨号权限被禁用" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return view;
    }

    /**
     * 时间选择控件设置时间的监听器
     */
    class SetClockTimeListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(calendar.MINUTE, minute);
            calendar.set(calendar.SECOND, 0);
            calendar.set(calendar.MILLISECOND, 0);
            // 提示
//            Toast.makeText(getActivity().getApplicationContext(), "已设定" + calendar.getTime().toString() + "闹钟", Toast.LENGTH_LONG);
            // 设定计时器
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

//    class AlarmAlert extends android.app.Activity {
//        public void onCreate(Bundle SavedInstanceState) {
//            super.onCreate(SavedInstanceState);
//            new AlertDialog.Builder(AlarmAlert.this).setIcon(R.drawable.clock)
//                    .setTitle("闹钟响了。。。").setMessage("快起床！！！")
//                    .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            AlarmAlert.this.finish();
//                        }
//                    }).show();
//        }
//    }
}


