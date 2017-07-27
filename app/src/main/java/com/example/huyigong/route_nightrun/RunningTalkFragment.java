package com.example.huyigong.route_nightrun;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningTalkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningTalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningTalkFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RunningTalkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunningTalkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningTalkFragment newInstance(String param1, String param2) {
        RunningTalkFragment fragment = new RunningTalkFragment();
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

    MapView mMapView;   // 地图
    ArcGISMap mArcGISMap;
    Graphic mCurPointGraphic; // 当前点
    PictureMarkerSymbol mPinSymbol;
    GraphicsOverlay mGraphicsOverlay;
    LocationManager mLocationManager; // 定位服务
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Point pinPoint = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                mMapView.setViewpointCenterAsync(pinPoint);
                if (mCurPointGraphic != null) {
                    mCurPointGraphic.setGeometry(pinPoint);
                }
            } else {
                Toast.makeText(getContext(), "位置获取出错", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk, container, false);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        } catch (SecurityException se) {
            Toast.makeText(getContext(), "未开启定位权限", Toast.LENGTH_SHORT).show();
        }
        mMapView = (MapView) view.findViewById(R.id.talk_map_view);
        mArcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 30.541093, 114.360734, 16);
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        mMapView.setMap(mArcGISMap);
        // 添加当前点
        BitmapDrawable pinBitmap = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.boy);
        mPinSymbol = new PictureMarkerSymbol(pinBitmap);
        mPinSymbol.setHeight(40);
        mPinSymbol.setWidth(40);
        mPinSymbol.loadAsync();
        mPinSymbol.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                Location location = getLocation();
                if (location != null) {
                    Point pinPoint = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                    mMapView.setViewpointCenterAsync(pinPoint);
                    mCurPointGraphic = new Graphic(pinPoint, mPinSymbol);
                    mGraphicsOverlay.getGraphics().add(mCurPointGraphic);
                } else {
                    Toast.makeText(getContext(), "无法获取位置", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.resume();

    }

    Location getLocation() {
        Location location;
        try {
            if ((mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) && (location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) != null) {
                return location;
            } else if ((location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)) != null) {
                return  location;
            } else {
                return null;
            }
        } catch (SecurityException se) {
            Toast.makeText(getContext(), "未开启定位权限" + se.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        } catch (Exception e) {
            Toast.makeText(getContext(), "其他错误" + e.getMessage(), Toast.LENGTH_LONG).show();
//            System.out.println(e.getMessage());
            return null;
        }
    }
}
