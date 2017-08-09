package com.example.huyigong.route_nightrun;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.example.huyigong.route_nightrun.Substances.DrinksInfo;
import com.example.huyigong.route_nightrun.helpers.CalculateGeometryApi;
import com.example.huyigong.route_nightrun.helpers.RouteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningRunFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningRunFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RunningRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunningRunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningRunFragment newInstance(String param1, String param2) {
        RunningRunFragment fragment = new RunningRunFragment();
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

    public int UserId = 39;

    enum fragmentStatus{
        ShowMap,
        RoutingByDistance_Unstart,
        RoutingByStops_Unstart,
        RoutingByDistance,
        RoutingByStops,
        Pause,
    };
    fragmentStatus mFragmentStatus = fragmentStatus.ShowMap;
    double planningDistance = -1;
    double userRuningDistance = 0;
    boolean haveGotRoute = false;
    boolean showLoctionAlways = false;
    ArrayList<Point> planningRoute = new ArrayList<Point>();
    Handler mShowNearDrinksHanler;
    Handler mGenerateStopPointsHandler;

    LinearLayout layoutOfRoutingButton = null;
    GridLayout layoutOfStartButton = null;
    GridLayout layoutOfContinueButton = null;
    GridLayout layoutOfClearPoints = null;
    GridLayout layoutOfReturnInit = null;
    Button btnRoutingByDistance = null;
    Button btnRoutingByStops= null;
    Button btnStart = null;
    Button btnContinue = null;
    Button btnClearPoints = null;
    Button btnReturnInit = null;
    MapView mMapView;   // 地图
    ArcGISMap mArcGISMap;
    Graphic mCurPointGraphic; // 当前点
    PictureMarkerSymbol mCurrentPointSymbol;
    GraphicsOverlay mPointsOverlay;
    GraphicsOverlay mPositionOverlay;
    GraphicsOverlay mLinesOverlay;
    GraphicsOverlay mPolygonsOverlay;
    GraphicsOverlay mDrinkOverlay;
    GraphicsOverlay mOthersOverlay;
    LocationManager mLocationManager; // 定位服务
    ArrayList<Point> allStopPointsList = new ArrayList<Point>();
    ArrayList<Point> stopPointsForShortestRoute = new ArrayList<Point>();
    ArrayList<DrinksInfo> drinkList = new ArrayList<DrinksInfo>();
    Point userPosition = null;

    long startTimeMiliseconds = 0;
    long endTimeMiliseconds = 0;

    final boolean DEBUG = false;

    //用于引用传递
    public class DoubleReferance{
        private double mValue;

        public  DoubleReferance(double initValue) { mValue = initValue; }

        public void setValue(double value) { mValue = value; }

        public double getValue() { return mValue; }
    }

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(DEBUG)
                return;
            if (location != null)
            {
//                userPosition = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
//                userPosition = (Point)GeometryEngine.project(mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY())), SpatialReference.create(4326));

                Point lastPoint;
                if(userPosition == null)
                {
                    userPosition = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                    lastPoint = userPosition;
                }
                else
                {
                    lastPoint = new Point(userPosition.getX(), userPosition.getY(), userPosition.getSpatialReference());
                    userPosition = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                }

                if((mFragmentStatus == fragmentStatus.RoutingByStops || mFragmentStatus == fragmentStatus.RoutingByDistance) && planningRoute != null && planningRoute.size() > 1)
                {
                    Point nearestPoint = CalculateGeometryApi.GetNearestPointInPolyLine(planningRoute, userPosition);
                    Point userPositionMkt = (Point)GeometryEngine.project(userPosition, mMapView.getSpatialReference());
                    double d = Math.sqrt((nearestPoint.getX() - userPositionMkt.getX())*(nearestPoint.getX() - userPositionMkt.getX()) + (nearestPoint.getY() - userPositionMkt.getY())*(nearestPoint.getY() - userPositionMkt.getY()));
                    if(nearestPoint != null && d < 50)
                        userPosition = new Point(nearestPoint.getX(), nearestPoint.getY(), nearestPoint.getSpatialReference());
                    else
                        ShowToast("温馨提醒：当前位置未在预定路线上", Toast.LENGTH_SHORT);
                }
                if(showLoctionAlways)
                {
                    mMapView.setViewpointCenterAsync(userPosition);
                    mCurPointGraphic.setGeometry(userPosition);
                    Point p1 = (Point)GeometryEngine.project(userPosition, SpatialReference.create(3857));
                    Point p2 = (Point)GeometryEngine.project(lastPoint, SpatialReference.create(3857));
                    double d = RouteApi.GetDistanceOfTwoPoint(p1, p2);
                    userRuningDistance += d;
                }
            }
            else
            {
//                ShowToast("位置获取出错", Toast.LENGTH_LONG);
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
//       super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_running, container, false);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        {
            boolean networkOn;
            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                networkOn = true;
            }
            catch (SecurityException se)
            {
                Toast.makeText(getContext(), "建议开启WiFi提高定位精度", Toast.LENGTH_SHORT).show();
                networkOn = false;
            }

            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            }
            catch (SecurityException se)
            {
                if(networkOn)
                    Toast.makeText(getContext(), "无GPS定位功能", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "无定位服务开启", Toast.LENGTH_SHORT).show();
            }
        }

        mMapView = (MapView) view.findViewById(R.id.mapView1);
        mArcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 30.541093, 114.360734, 16);
        mPointsOverlay = new GraphicsOverlay();
        mPositionOverlay = new GraphicsOverlay();
        {
            BitmapDrawable pinBitmap = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.boy);
            mCurrentPointSymbol = new PictureMarkerSymbol(pinBitmap);
            mCurrentPointSymbol.setHeight(40);
            mCurrentPointSymbol.setWidth(40);
            mCurrentPointSymbol.setOffsetX(0);
            mCurrentPointSymbol.setOffsetY(20);
            mCurrentPointSymbol.loadAsync();
            mCurrentPointSymbol.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    mCurPointGraphic = new Graphic(new Point(mMapView.getPivotX(), mMapView.getY(), SpatialReferences.getWgs84()), mCurrentPointSymbol);
                    mCurPointGraphic.setVisible(false);
                    mPositionOverlay.getGraphics().add(mCurPointGraphic);
                }
            });
        }

        try
        {
            Thread.sleep(1);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }

        mLinesOverlay = new GraphicsOverlay();
        mPolygonsOverlay = new GraphicsOverlay();
        mOthersOverlay = new GraphicsOverlay();
        mDrinkOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mPolygonsOverlay);
        mMapView.getGraphicsOverlays().add(mLinesOverlay);
        mMapView.getGraphicsOverlays().add(mPointsOverlay);
        mMapView.getGraphicsOverlays().add(mDrinkOverlay);
        mMapView.getGraphicsOverlays().add(mOthersOverlay);
        mMapView.getGraphicsOverlays().add(mPositionOverlay);
        mMapView.setMap(mArcGISMap);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mMapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event)
            {
                if(mFragmentStatus != fragmentStatus.RoutingByStops_Unstart)
                    return false;
                Point point = mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY()));
                stopPointsForShortestRoute.add(point);
                DrawPoint(point, R.drawable.flag, 30, 30, 15, 15);
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event)
            {
                if(!DEBUG)
                    return false;
                Point lastPoint;
                if(userPosition == null)
                {
                    userPosition = (Point)GeometryEngine.project(mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY())), SpatialReference.create(4326));
                    lastPoint = userPosition;
                }
                else
                {
                    lastPoint = new Point(userPosition.getX(), userPosition.getY(), userPosition.getSpatialReference());
                    userPosition = (Point)GeometryEngine.project(mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY())), SpatialReference.create(4326));
                }

                if((mFragmentStatus == fragmentStatus.RoutingByStops || mFragmentStatus == fragmentStatus.RoutingByDistance) && planningRoute != null && planningRoute.size() > 1)
                {
                    Point nearestPoint = CalculateGeometryApi.GetNearestPointInPolyLine(planningRoute, userPosition);
                    Point userPositionMkt = (Point)GeometryEngine.project(userPosition, mMapView.getSpatialReference());
                    double d = Math.sqrt((nearestPoint.getX() - userPositionMkt.getX())*(nearestPoint.getX() - userPositionMkt.getX()) + (nearestPoint.getY() - userPositionMkt.getY())*(nearestPoint.getY() - userPositionMkt.getY()));
                    if(nearestPoint != null && d < 50)
                        userPosition = new Point(nearestPoint.getX(), nearestPoint.getY(), nearestPoint.getSpatialReference());
                    else
                        ShowToast("温馨提醒：当前位置未在预定路线上", Toast.LENGTH_SHORT);
                }
                if(showLoctionAlways)
                {
                    mMapView.setViewpointCenterAsync(userPosition);
                    mCurPointGraphic.setGeometry(userPosition);
                    Point p1 = (Point)GeometryEngine.project(userPosition, SpatialReference.create(3857));
                    Point p2 = (Point)GeometryEngine.project(lastPoint, SpatialReference.create(3857));
                    double d = RouteApi.GetDistanceOfTwoPoint(p1, p2);
                    userRuningDistance += d;
                }
                return false;
            }
        });

//        GenerateEndPoinrsFromFile();
        GenerateEndPoinrsFromMySql(new Point(114, 31, SpatialReferences.getWgs84()), new Point(115, 30, SpatialReferences.getWgs84()));

        layoutOfRoutingButton = (LinearLayout)view.findViewById(R.id.layout_routing);
        {
            layoutOfRoutingButton.setVisibility(View.VISIBLE);
        }

        layoutOfStartButton = (GridLayout) view.findViewById(R.id.layout_start);
        {
            layoutOfStartButton.setVisibility(View.GONE);
        }

        layoutOfContinueButton = (GridLayout) view.findViewById(R.id.layout_continue);
        {
            layoutOfStartButton.setVisibility(View.GONE);
        }

        layoutOfReturnInit = (GridLayout) view.findViewById(R.id.layout_return_Init);
        {
            layoutOfReturnInit.setVisibility(View.GONE);
        }

        layoutOfClearPoints = (GridLayout) view.findViewById(R.id.layout_clear_points);
        {
            layoutOfClearPoints.setVisibility(View.GONE);
        }

        btnRoutingByDistance = (Button)view.findViewById(R.id.btn_specify_route_length);
        {
            btnRoutingByDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userPosition == null)
                    {
                        ShowToast("获取用户位置失败，请检查定位服务是否开启", Toast.LENGTH_SHORT);
                        return;
                    }
                    final EditText inputServer = new EditText(getContext());
                    inputServer.setHint("往返总距离");
                    inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("请输入夜跑总距离").setIcon(android.R.drawable.ic_dialog_map).setView(inputServer).setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try
                            {
                                planningDistance = Double.parseDouble(inputServer.getText().toString());
                                if(planningDistance > 5000 || planningDistance < 500)
                                {
                                    planningDistance = 0;
                                    ShowToast("输入有效值为500~5000，请重新输入", Toast.LENGTH_SHORT);
                                    return;
                                }
                                else
                                {
                                    layoutOfRoutingButton.setVisibility(View.GONE);
                                    layoutOfStartButton.setVisibility(View.VISIBLE);
                                    mFragmentStatus = fragmentStatus.RoutingByDistance_Unstart;
                                    mMapView.setViewpointCenterAsync(userPosition);
                                    mCurPointGraphic.setGeometry(userPosition);
                                    mCurPointGraphic.setVisible(true);
                                    btnStart.setText("获取路径...");
                                    btnStart.setEnabled(false);
                                    ShowToast("本方法较为耗时，请耐心等待", Toast.LENGTH_LONG);
                                    Thread t = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayList<Point> points =  GenerateRouteByDistance(userPosition, planningDistance / 2);
                                            if(points == null || points.size() < 2)
                                            {
                                                ShowToast("路径规划失败", Toast.LENGTH_SHORT);
                                                InitFragment();
                                                return;
                                            }
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    btnStart.setText("开始夜跑");
                                                    btnStart.setEnabled(true);
                                                    ShowToast("点击开始夜跑启动实时定位", Toast.LENGTH_SHORT);
                                                }
                                            });
                                            planningRoute = points;
                                            DrawPoint(points.get(0), R.drawable.start, 22, 33, 0, (float)13.5);
                                            DrawPoint(points.get(points.size() - 1), R.drawable.terminal, 22, 33, 0, (float)13.5);
                                            DrawRoute(points, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 0, 16, 233), 3));
                                            double minLng = points.get(0).getX();
                                            double maxLng = points.get(0).getX();
                                            double minLat = points.get(0).getY();
                                            double maxLat = points.get(0).getY();
                                            for(int i=1;i<points.size();i++)
                                            {
                                                double templng = points.get(i).getX();
                                                double templat = points.get(i).getY();

                                                if(templng < minLng)
                                                    minLng = templng;
                                                else if(templng > maxLng)
                                                    maxLng = templng;

                                                if(templat < minLat)
                                                    minLat = templat;
                                                else if(templat > maxLat)
                                                    maxLat = templat;
                                            }
                                            ShowNearDrinks(new Point(minLng, maxLat, SpatialReferences.getWgs84()), new Point(maxLng, minLat, SpatialReferences.getWgs84()));
                                            mFragmentStatus = fragmentStatus.RoutingByDistance_Unstart;
                                        }
                                    });
                                    t.start();
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                ShowToast("输入格式有误", Toast.LENGTH_SHORT);
                                InitFragment();
                                return;
                            }
                        }
                    });
                    builder.show();
                }
            });
        }

        btnRoutingByStops = (Button)view.findViewById(R.id.btn_specify_route_stops);
        {
            btnRoutingByStops.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userPosition == null)
                    {
                        ShowToast("获取用户位置失败，请检查定位服务是否开启", Toast.LENGTH_SHORT);
                        return;
                    }
                    ShowToast("双击确定经过站点，点击\"开始夜跑按钮\"产生夜跑路线", Toast.LENGTH_LONG);
                    layoutOfRoutingButton.setVisibility(View.GONE);
                    layoutOfStartButton.setVisibility(View.VISIBLE);
                    layoutOfClearPoints.setVisibility(View.VISIBLE);
                    layoutOfReturnInit.setVisibility(View.VISIBLE);
                    btnStart.setText("开始夜跑");
                    mFragmentStatus = fragmentStatus.RoutingByStops_Unstart;
                    stopPointsForShortestRoute.clear();
                    mMapView.setViewpointCenterAsync(userPosition);
                    mCurPointGraphic.setGeometry(userPosition);
                    mCurPointGraphic.setVisible(true);
                    btnStart.setEnabled(true);
                }
            });
        }

        btnStart = (Button)view.findViewById(R.id.btn_start_run);
        {
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mFragmentStatus == fragmentStatus.ShowMap)
                    {

                    }
                    else if(mFragmentStatus == fragmentStatus.RoutingByDistance_Unstart)
                    {
                        mMapView.setViewpointCenterAsync(userPosition);
                        mCurPointGraphic.setGeometry(userPosition);
                        mFragmentStatus = fragmentStatus.RoutingByDistance;
                        btnStart.setText("暂停");
                        startTimeMiliseconds = System.currentTimeMillis();
                        mCurPointGraphic.setVisible(true);
                        showLoctionAlways = true;
                    }
                    else if(mFragmentStatus == fragmentStatus.RoutingByStops_Unstart)
                    {
                        if(stopPointsForShortestRoute.size() < 1)
                        {
                            ShowToast("请选择站点", Toast.LENGTH_SHORT);
                            return;
                        }
                        mFragmentStatus = fragmentStatus.RoutingByStops;
                        layoutOfReturnInit.setVisibility(View.GONE);
                        btnStart.setText("获取路径...");
                        btnStart.setEnabled(false);
                        showLoctionAlways = false;
                        mCurPointGraphic.setVisible(true);
                        showLoctionAlways = true;
                        layoutOfClearPoints.setVisibility(View.GONE);
                        GetShortestRoute();
                    }
                    else if(mFragmentStatus == fragmentStatus.RoutingByDistance)
                    {
                        mFragmentStatus = fragmentStatus.Pause;
                        btnStart.setText("停止夜跑");
                        btnStart.setEnabled(true);
                        showLoctionAlways = false;
                        layoutOfContinueButton.setVisibility(View.VISIBLE);
                    }
                    else if(mFragmentStatus == fragmentStatus.RoutingByStops)
                    {
                        mFragmentStatus = fragmentStatus.Pause;
                        btnStart.setText("停止夜跑");
                        btnStart.setEnabled(true);
                        showLoctionAlways = false;
                        layoutOfContinueButton.setVisibility(View.VISIBLE);
                    }
                    else if(mFragmentStatus == fragmentStatus.Pause)
                    {
                        TextView textView = new TextView(getContext());
                        {
                            DecimalFormat df = new DecimalFormat("0.0");
                            textView.setText("您本次跑步的距离为：" + df.format(userRuningDistance) + "m");
                            textView.setTextSize(20);

                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("夜跑结束").setIcon(android.R.drawable.ic_dialog_map).setView(textView).setPositiveButton("确定", null);
                        builder.show();
                        endTimeMiliseconds = System.currentTimeMillis();
                        UploadRunningInfo(UserId, planningRoute, userRuningDistance, planningDistance, startTimeMiliseconds, endTimeMiliseconds);

                        InitFragment();
                    }
                }
            });
        }

        btnContinue = (Button)view.findViewById(R.id.btn_continue_run);
        {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMapView.setViewpointCenterAsync(userPosition);
                    mCurPointGraphic.setGeometry(userPosition);
                    mFragmentStatus = fragmentStatus.RoutingByDistance;
                    btnStart.setText("暂停");
                    layoutOfContinueButton.setVisibility(View.GONE);
                    mCurPointGraphic.setVisible(true);
                    showLoctionAlways = true;
                }
            });
        }

        btnReturnInit = (Button)view.findViewById(R.id.btn_return_Init);
        {
            btnReturnInit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InitFragment();
                }
            });
        }

        btnClearPoints = (Button)view.findViewById(R.id.btn_clear_points);
        {
            btnClearPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    planningRoute.clear();
                    mPointsOverlay.getGraphics().clear();
                }
            });
        }

        mShowNearDrinksHanler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                try
                {
                    String response = ((Bundle)msg.getData()).getString("NearDrinks");
                    JSONTokener jsonParser = new JSONTokener(response);
                    JSONObject json = (JSONObject)jsonParser.nextValue();
                    JSONArray neardrinksArray = json.getJSONArray("neardrinks");
                    for(int i=0;i<neardrinksArray.length();i++)
                    {
                        JSONObject tempDrink = (JSONObject) neardrinksArray.get(i);
                        int drinkId = tempDrink.getInt("DrinkID");
                        String drinkName = tempDrink.getString("DrinkName");
                        final double drinkLng = tempDrink.getDouble("DrinkLng");
                        final double drinkLat = tempDrink.getDouble("DrinkLat");
                        final DrinksInfo di = new DrinksInfo(drinkId, drinkName, drinkLng, drinkLat);

                        BitmapDrawable pinBitmap = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.drink);
                        final PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(pinBitmap);
                        pictureMarkerSymbol.setHeight(40);
                        pictureMarkerSymbol.setWidth(40);
                        pictureMarkerSymbol.setOffsetX(0);
                        pictureMarkerSymbol.setOffsetY(20);
                        pictureMarkerSymbol.loadAsync();
                        pictureMarkerSymbol.addDoneLoadingListener(new Runnable() {
                            @Override
                            public void run() {
                                drinkList.add(di);
                                Graphic g = new Graphic(new Point(drinkLng, drinkLat, SpatialReferences.getWgs84()), pictureMarkerSymbol);
                                mDrinkOverlay.getGraphics().add(g);
                            }
                        });
                    }
                }
                catch (Exception ex)
                {
                    ShowToast("获取饮品店位置失败", Toast.LENGTH_SHORT);
                }
            }
        };

        mGenerateStopPointsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                try
                {
                    String response = ((Bundle)msg.getData()).getString("PointsJsonString");
                    JSONTokener jsonParser = new JSONTokener(response);
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    JSONArray pointsArray = json.getJSONArray("RouteStops");
                    for(int i=0;i<pointsArray.length();i++)
                    {
                        JSONObject tempDrink = (JSONObject) pointsArray.get(i);
//                        int pointId = tempDrink.getInt("id");
                        final double pointLng = tempDrink.getDouble("lng");
                        final double pointLat = tempDrink.getDouble("lat");
                        Point tempPoint = new Point(pointLng, pointLat, SpatialReferences.getWgs84());
                        allStopPointsList.add(tempPoint);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        };

        return view;
    }

    private void ShowToast(final String message, final int duration)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, duration).show();
            }
        });
    }

    private ArrayList<Point> GenerateRouteByDistance(Point pointGps ,double lengthOfRouth)
    {
        try
        {
            if(allStopPointsList == null || allStopPointsList.size() == 0)
            {
                ShowToast("无适合您当前位置的路径规划", Toast.LENGTH_LONG);
                return null;
            }
            pointGps = (Point)GeometryEngine.project(pointGps, SpatialReferences.getWgs84());
            Point point = (Point)GeometryEngine.project(pointGps, mMapView.getSpatialReference());
            Point leftTop = new Point(point.getX() - lengthOfRouth, point.getY() - lengthOfRouth, point.getSpatialReference());
            Point rightButtom = new Point(point.getX() + lengthOfRouth, point.getY() + lengthOfRouth, point.getSpatialReference());
            Point leftTopGps = (Point)GeometryEngine.project(leftTop, SpatialReference.create(4326));
            Point rightButtomGps = (Point)GeometryEngine.project(rightButtom, SpatialReference.create(4326));

            ArrayList<Point> pointsInBigRect = new ArrayList<Point>();
            for (Point tempPoint : allStopPointsList)
            {
                if (tempPoint.getX() < rightButtomGps.getX() && tempPoint.getX() > leftTopGps.getX() && tempPoint.getY() < rightButtomGps.getY() && tempPoint.getY() > leftTopGps.getY())
                    pointsInBigRect.add(tempPoint);
            }

            leftTop = new Point(point.getX() - lengthOfRouth / 2, point.getY() - lengthOfRouth / 2, point.getSpatialReference());
            rightButtom = new Point(point.getX() + lengthOfRouth / 2, point.getY() + lengthOfRouth / 2, point.getSpatialReference());
            leftTopGps = (Point)GeometryEngine.project(leftTop, SpatialReference.create(4326));
            rightButtomGps = (Point)GeometryEngine.project(rightButtom, SpatialReference.create(4326));

            ArrayList<Point> pointForRoutePlanning = new ArrayList<Point>();
            for (Point tempPoint : pointsInBigRect)
            {
                if (!(tempPoint.getX() < rightButtomGps.getX() && tempPoint.getX() > leftTopGps.getX() && tempPoint.getY() < rightButtomGps.getY() && tempPoint.getY() > leftTopGps.getY()))
                    pointForRoutePlanning.add(tempPoint);
            }

            if(pointForRoutePlanning.size() == 0)
            {
                ShowToast("无适合您当前位置的路径规划", Toast.LENGTH_LONG);
                return null;
            }

            double[] distanceOfAllRoutes = new double[pointForRoutePlanning.size()];
            {
                int index = 0;
                for(Point tempPoint : pointForRoutePlanning)
                {
                    if((distanceOfAllRoutes[index++] = RouteApi.GetDistanceOfShortestRoute(point, tempPoint)) == -1)
                    {
                        ShowToast("网络错误，路径规划失败", Toast.LENGTH_LONG);
                        return null;
                    }
                }
            }

            int indexOfNeareatRoute = 0;
            double NearestDistence = distanceOfAllRoutes[0];
            {
                for (int i=1;i< distanceOfAllRoutes.length;i++)
                {
                    if (Math.abs(distanceOfAllRoutes[i] - lengthOfRouth) < Math.abs(NearestDistence - lengthOfRouth))
                    {
                        indexOfNeareatRoute = i;
                        NearestDistence = distanceOfAllRoutes[i];
                    }
                }
            }
            DoubleReferance df = new DoubleReferance(0);
            ArrayList<Point> pointsArrayList = RouteApi.GetShortestRoute(point, pointForRoutePlanning.get(indexOfNeareatRoute), df);
            planningDistance = df.getValue();
            ArrayList<Point> resultPoints = new ArrayList<Point>();
            resultPoints.add(pointGps);
            for(Point tempPoint : pointsArrayList)
            {
                resultPoints.add(tempPoint);
            }
            haveGotRoute = true;
            return resultPoints;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public void GenerateEndPoinrsFromMySql(Point leftTop, Point rightBottom)
    {
        final double minLng = leftTop.getX();
        final double maxLng = rightBottom.getX();
        final double minLat = rightBottom.getY();
        final double maxLat = leftTop.getY();
        new Thread(new Runnable() {
        @Override
        public void run() {
            try
            {
//                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_neardrinks) + "?minLng=-180&maxLng=180&minLat=-90&maxLat=90");
                URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_routestops) + "?minLng=" + minLng +  "&maxLng=" + maxLng + "&minLat=" + minLat + "&maxLat=" + maxLat);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(4000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = reader.readLine();
                Bundle bundle = new Bundle();
                bundle.putString("PointsJsonString", content);
                Message message = new Message();
                message.setData(bundle);
                mGenerateStopPointsHandler.sendMessage(message);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }).start();
    }

    private void GenerateEndPoinrsFromFile()
    {
        try
        {
            InputStream inputStream = getResources().openRawResource(R.raw.route_planning_data);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String mimeTypeLine = "";
            while((mimeTypeLine = reader.readLine()) != null)
            {
                String[] contents = mimeTypeLine.split(",");
                double lng = Double.parseDouble(contents[1]);
                double lat = Double.parseDouble(contents[2]);
                Point point = new Point(lng, lat, SpatialReferences.getWgs84());
                allStopPointsList.add(point);

//                Graphic graphic = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 8));
//                mPointsOverlay.getGraphics().add(graphic);
            }
        }
        catch(Exception e)
        {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
    }

    private void GetShortestRoute()
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DoubleReferance dr = new DoubleReferance(0);
                ArrayList<Point> stops = new ArrayList<Point>();
                stops.add((Point)GeometryEngine.project(userPosition, mMapView.getSpatialReference()));
                for(Point tempPoint : stopPointsForShortestRoute)
                {
                    stops.add(tempPoint);
                }
                stops.add((Point)GeometryEngine.project(userPosition, mMapView.getSpatialReference()));
                stopPointsForShortestRoute.clear();
                ArrayList<Point> points = RouteApi.GetShortestRoute(stops, dr);
                if(points == null)
                {
                    ShowToast("获取路径失败，请检查网络状态或定位服务", Toast.LENGTH_LONG);
                    InitFragment();
                    return;
                }

                planningDistance = dr.getValue();
                ArrayList<Point> resultPooints = new ArrayList<Point>();
                resultPooints.add((Point)GeometryEngine.project(userPosition, SpatialReferences.getWgs84()));
                for(Point tempPoint : points)
                {
                    resultPooints.add(tempPoint);
                }
                resultPooints.add((Point)GeometryEngine.project(userPosition, SpatialReferences.getWgs84()));
                planningRoute = resultPooints;

                DrawRoute(resultPooints, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 0, 16, 233), 3));
                double minLng = resultPooints.get(0).getX();
                double maxLng = resultPooints.get(0).getX();
                double minLat = resultPooints.get(0).getY();
                double maxLat = resultPooints.get(0).getY();
                for(int i=1;i<resultPooints.size();i++)
                {
                    double templng = resultPooints.get(i).getX();
                    double templat = resultPooints.get(i).getY();

                    if(templng < minLng)
                        minLng = templng;
                    else if(templng > maxLng)
                        maxLng = templng;

                    if(templat < minLat)
                        minLat = templat;
                    else if(templat > maxLat)
                        maxLat = templat;
                }
                ShowNearDrinks(new Point(minLng, maxLat, SpatialReferences.getWgs84()), new Point(maxLng, minLat, SpatialReferences.getWgs84()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMapView.setViewpointCenterAsync(userPosition);
                        mCurPointGraphic.setGeometry(userPosition);
                        mFragmentStatus = fragmentStatus.RoutingByStops;
                        btnStart.setText("暂停");
                        startTimeMiliseconds = System.currentTimeMillis();
                        mCurPointGraphic.setVisible(true);
                        showLoctionAlways = true;
                        btnStart.setEnabled(true);
                    }
                });
            }
        });
        t.start();
    }

    private void DrawRoute(ArrayList<Point> pointsList, Symbol symbol)
    {
        try
        {
            PointCollection pointCollection = new PointCollection(pointsList);
            Polyline path = new Polyline(pointCollection, SpatialReference.create(4326));
            Graphic graphic = new Graphic(path, symbol);
            mLinesOverlay.getGraphics().add(graphic);
        }
        catch (Exception ex)
        {
            ShowToast("绘制路径失败", Toast.LENGTH_LONG);
            InitFragment();
        }
    }

    private void DrawPoint(final Point point, int imageId, float width, float height, float offsetX, float offsetY )
    {
        BitmapDrawable pinBitmap = (BitmapDrawable) ContextCompat.getDrawable(getContext(), imageId);
        final PictureMarkerSymbol pictureMarkerSymbol  = new PictureMarkerSymbol(pinBitmap);
        pictureMarkerSymbol.setWidth(width);
        pictureMarkerSymbol.setHeight(height);
        pictureMarkerSymbol.setOffsetX(offsetX);
        pictureMarkerSymbol.setOffsetY(offsetY);
        pictureMarkerSymbol.loadAsync();
        pictureMarkerSymbol.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                Graphic graphic = new Graphic();
                graphic = new Graphic(point, pictureMarkerSymbol);
                mPointsOverlay.getGraphics().add(graphic);
            }
        });
    }

    private void InitFragment()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFragmentStatus = fragmentStatus.ShowMap;
                layoutOfRoutingButton.setVisibility(View.VISIBLE);
                layoutOfStartButton.setVisibility(View.GONE);
                layoutOfContinueButton.setVisibility(View.GONE);
                layoutOfClearPoints.setVisibility(View.GONE);
                layoutOfReturnInit.setVisibility(View.GONE);
                planningDistance = 0;
                userRuningDistance = 0;
                haveGotRoute = false;
                mCurPointGraphic.setVisible(false);
                btnStart.setText("开始夜跑");
                btnStart.setEnabled(false);
                showLoctionAlways = false;
                mPointsOverlay.getGraphics().clear();
                mLinesOverlay.getGraphics().clear();
                mDrinkOverlay.getGraphics().clear();
                drinkList.clear();
                planningRoute.clear();
                startTimeMiliseconds = 0;
                endTimeMiliseconds = 0;
            }
        });
    }

    private void ShowNearDrinks(Point leftTop, Point rightBottom)
    {
        Point leftTopMkt = (Point)GeometryEngine.project(leftTop, SpatialReferences.getWebMercator());
        Point rightBottomMkt = (Point)GeometryEngine.project(rightBottom, SpatialReferences.getWebMercator());
        final double minLngMkt = leftTopMkt.getX() - 200;
        final double maxLngMkt = rightBottomMkt.getX() + 200;
        final double minLatMkt = rightBottomMkt.getY() - 200;
        final double maxLatMkt = leftTopMkt.getY() + 200;

        Point leftTopOpt = (Point)GeometryEngine.project(new Point(minLngMkt, maxLatMkt, SpatialReferences.getWebMercator()), SpatialReferences.getWgs84());
        Point rightBottomMktOpt = (Point)GeometryEngine.project(new Point(maxLngMkt, minLatMkt, SpatialReferences.getWebMercator()), SpatialReferences.getWgs84());

        final double minLng = leftTopOpt.getX();
        final double maxLng = rightBottomMktOpt.getX();
        final double minLat = rightBottomMktOpt.getY();
        final double maxLat = leftTopOpt.getY();
        Thread showNearDrinkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
//                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_neardrinks) + "?minLng=-180&maxLng=180&minLat=-90&maxLat=90");
//                                                        http://localhost:4521/api/neardrinks?minLng=-180&maxLng=180&minLat=-90&maxLat=30.52888
                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_neardrinks) + "?minLng=" + minLng +  "&maxLng=" + maxLng + "&minLat=" + minLat + "&maxLat=" + maxLat);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(4000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String content = reader.readLine();
                    Bundle bundle = new Bundle();
                    bundle.putString("NearDrinks", content);
                    Message message = new Message();
                    message.setData(bundle);
                    mShowNearDrinksHanler.sendMessage(message);
                }
                catch (Exception e)
                {
                    ShowToast("获取饮品店位置失败", Toast.LENGTH_SHORT);
                }
            }
        });
        showNearDrinkThread.start();
    }

    private JSONObject GenerateRouteJson(int userId, ArrayList<Point> pointsList, double distanceOfRoute, double distanceOfUserRunning, long during)
    {
        try
        {
            JSONObject routeInfo = new JSONObject();
            JSONObject routeSubInfo = new JSONObject();
            JSONArray pathArray = new JSONArray();
            for(int i=0;i<pointsList.size();i++)
            {
                Point tempPoint = (Point)GeometryEngine.project(pointsList.get(i), SpatialReferences.getWgs84());
                JSONObject pointJson = new JSONObject();
                pointJson.put("Lng", tempPoint.getX());
                pointJson.put("Lat", tempPoint.getY());
                pathArray.put(i, pointJson);
            }
            routeSubInfo.put("UserId", userId);
            routeSubInfo.put("DateTime", new Timestamp(System.currentTimeMillis()));
            routeSubInfo.put("Distance", distanceOfRoute);
            routeSubInfo.put("UserRunningDistance", distanceOfUserRunning);
            routeSubInfo.put("During", during);
            routeSubInfo.put("Path", pathArray);
            routeInfo.put("RouteInfo", routeSubInfo);

            return routeInfo;
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean UploadRunningInfo(int id, ArrayList<Point> pointsList, double distanceOfRoute, double distanceOfUserRunning, long start, long end)
    {
        if(distanceOfRoute < 200)
        {
            ShowToast("本次跑步距离过短，将不上传数据库", Toast.LENGTH_SHORT);
            return false;
        }
        final int userId = id;
        final long during = (end - start) / 1000;
        final JSONObject routeInfo = GenerateRouteJson(userId, pointsList, distanceOfRoute, distanceOfUserRunning, during);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_add_running_info));
//                                                        http://localhost:4521/api/neardrinks?minLng=-180&maxLng=180&minLat=-90&maxLat=30.52888
//                    URL url = new URL("http://192.168.0.100:4521/api/addrunninginfo/");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    {
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(4000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");
                    }
                    OutputStream outputDataStream = connection.getOutputStream();
                    outputDataStream.write(routeInfo.toString().getBytes());
                    outputDataStream.flush();
                    outputDataStream.close();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = reader.readLine();
                    JSONTokener jsonParser = new JSONTokener(response);
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    String status = json.getString("status");
                    if(status == "true")
                        ShowToast("本次跑步记录上次成功", Toast.LENGTH_SHORT);
                    else
                        ShowToast("记录上传失败，请检查网络", Toast.LENGTH_SHORT);
                }
                catch (Exception e)
                {
                    ShowToast("数据上传失败", Toast.LENGTH_SHORT);
                }
            }
        }).start();
        return true;
    }
}
