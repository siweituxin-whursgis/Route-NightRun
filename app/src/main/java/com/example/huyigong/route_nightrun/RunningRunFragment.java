package com.example.huyigong.route_nightrun;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.example.huyigong.route_nightrun.substances.DrinksInfo;
import com.example.huyigong.route_nightrun.helpers.CalculateGeometryApi;
import com.example.huyigong.route_nightrun.helpers.RouteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xutils.ImageManager;
import org.xutils.image.ImageManagerImpl;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

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
        x.Ext.init((Application) getContext().getApplicationContext());
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

    //地图现处状态枚举
    enum fragmentStatus{
        //只显示地图
        ShowMap,
        //按路径长度规划，还未开始
        RoutingByDistance_Unstart,
        //按路径站点规划，还未开始
        RoutingByStops_Unstart,
        //按路径长度规划
        RoutingByDistance,
        //按路径站点规划
        RoutingByStops,
        //跑步暂停
        Pause,
    };

    //地图现处状态枚举
    fragmentStatus mFragmentStatus = fragmentStatus.ShowMap;

    //规划的路径
    ArrayList<Point> planningRoute = new ArrayList<Point>();

    //规划路径长度
    double planningDistance = -1;

    //用户夜跑长度，靠gps定位计算
    double userRuningDistance = 0;

    //是否获得路径
    boolean haveGotRoute = false;

    //是否一直显示用户所处位置
    boolean showLoctionAlways = false;

    //处理器
    Handler mShowNearDrinksHanler;
    Handler mGenerateStopPointsHandler;
    Handler mShowRoadLampHandler;
    Handler mShowInterestViewsHandler;
    Handler mSetInterestViewSourceHandler;

    //控件
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

    //地图
    MapView mMapView;
    ArcGISMap mArcGISMap;

    //当前点
    Graphic mCurPointGraphic;
    PictureMarkerSymbol mCurrentPointSymbol;

    //几何要素图层
    GraphicsOverlay mPointsOverlay;
    GraphicsOverlay mPositionOverlay;
    GraphicsOverlay mLinesOverlay;
    GraphicsOverlay mPolygonsOverlay;
    GraphicsOverlay mDrinkOverlay;
    GraphicsOverlay mOthersOverlay;

    // 定位服务
    LocationManager mLocationManager;

    //按长度规划路径的预定站点
    ArrayList<Point> allStopPointsList = new ArrayList<Point>();

    //按站点规划的站点
    ArrayList<Point> stopPointsForShortestRoute = new ArrayList<Point>();
    ArrayList<DrinksInfo> drinkList = new ArrayList<DrinksInfo>();

    //路灯要素集图层
    FeatureCollectionLayer mRoadlampsFeatureCollectionLayer = null;
    final int ROADLAMP_LAYER_INDEX = 0;
    final int ROADLAMP_BROKEN_LAYER_INDEX = 1;

    //风景点要素集图层
    FeatureCollectionLayer mInterestViewsFeatureCollectionLayer = null;
    final int INTEREST_VIEWS_LAYER_INDEX = 0;

    //用户位置
    Point userPosition = null;

    //定时更新路灯信息
    Timer search_show_roadlamp_timer = new Timer();
    final long SEARCH_SHOW_ROADLAMP_TIMER_PERIOD = 120000;
    final double SEARCH_ROADLAMP_RADIUS = 500;

    //跑步开始和结束时间
    long startTimeMiliseconds = 0;
    long endTimeMiliseconds = 0;

    //是否正在调试
    final boolean DEBUG = false;

    //用于引用传递（脑袋短路的才写这个，但我不想改了 !@_@! ）
    public class DoubleReferance{
        private double mValue;

        public  DoubleReferance(double initValue) { mValue = initValue; }

        public void setValue(double value) { mValue = value; }

        public double getValue() { return mValue; }
    }

    //定位监听
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
                    //第一次获得用户位置
                    userPosition = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                    lastPoint = userPosition;
                    ShowNearRoadLamps(userPosition, userPosition, SEARCH_ROADLAMP_RADIUS);
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

        //添加要素 - 路灯
        ArrayList<Field> roadlampFeatures = new ArrayList<Field>();
        {
            roadlampFeatures.add(Field.createInteger("id", "标识号"));
            roadlampFeatures.add(Field.createDouble("lng", "经度"));
            roadlampFeatures.add(Field.createDouble("lat", "纬度"));
            roadlampFeatures.add(Field.createString("address", "位置", 255));
            roadlampFeatures.add(Field.createString("isBroken", "是否损坏", 5));
            roadlampFeatures.add(Field.createString("onTime", "开灯时间", 10));
            roadlampFeatures.add(Field.createString("offTime", "关灯时间", 10));
        }
        ArrayList<FeatureCollectionTable> raodlampFeatureCollectionTables = new ArrayList<FeatureCollectionTable>();
        final FeatureCollectionTable roadlampFeatureCollectionTable = new FeatureCollectionTable(roadlampFeatures, GeometryType.POINT, SpatialReferences.getWgs84());
        {
            PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol((BitmapDrawable)getResources().getDrawable(R.drawable.roadlamp, null));
            {
                pictureMarkerSymbol.setHeight(30);
                pictureMarkerSymbol.setWidth(30);
                pictureMarkerSymbol.setOffsetX(0);
                pictureMarkerSymbol.setOffsetY(15);
            }
            SimpleRenderer simpleRenderer = new SimpleRenderer(pictureMarkerSymbol);
            roadlampFeatureCollectionTable.setRenderer(simpleRenderer);
            roadlampFeatureCollectionTable.setTitle("RoadlampInfo");
            raodlampFeatureCollectionTables.add(roadlampFeatureCollectionTable);
        }

        final FeatureCollectionTable roadlampBrokenFeatureCollectionTable = new FeatureCollectionTable(roadlampFeatures, GeometryType.POINT, SpatialReferences.getWgs84());
        {
            PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol((BitmapDrawable)getResources().getDrawable(R.drawable.roadlamp_broken, null));
            {
                pictureMarkerSymbol.setHeight(30);
                pictureMarkerSymbol.setWidth(30);
                pictureMarkerSymbol.setOffsetX(0);
                pictureMarkerSymbol.setOffsetY(15);
            }
            SimpleRenderer simpleRenderer = new SimpleRenderer(pictureMarkerSymbol);
            roadlampBrokenFeatureCollectionTable.setRenderer(simpleRenderer);
            roadlampBrokenFeatureCollectionTable.setTitle("RoadlampBrokenInfo");
            raodlampFeatureCollectionTables.add(roadlampBrokenFeatureCollectionTable);
        }
        //将要素集添加到要素图层
        FeatureCollection roadlampFeatureCollection = new FeatureCollection(raodlampFeatureCollectionTables);
        mRoadlampsFeatureCollectionLayer = new FeatureCollectionLayer(roadlampFeatureCollection);
        mRoadlampsFeatureCollectionLayer.setVisible(false);
        mArcGISMap.getOperationalLayers().add(mRoadlampsFeatureCollectionLayer);

        //添加要素 - 景点
        ArrayList<Field> interestViewsFeatures = new ArrayList<Field>();
        {
            interestViewsFeatures.add(Field.createDouble("lng", "经度"));
            interestViewsFeatures.add(Field.createDouble("lat", "纬度"));
            interestViewsFeatures.add(Field.createString("name", "景点名称", 20));
            interestViewsFeatures.add(Field.createString("description", "景点简介", 255));
            interestViewsFeatures.add(Field.createString("imageName", "景区图片", 50));
        }
        ArrayList<FeatureCollectionTable> insteretViewFeatureCollectionTables = new ArrayList<FeatureCollectionTable>();
        FeatureCollectionTable interestViewsFeatureCollectionTable = new FeatureCollectionTable(interestViewsFeatures, GeometryType.POINT, SpatialReferences.getWgs84());
        {
            PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol((BitmapDrawable)getResources().getDrawable(R.drawable.ount, null));
            {
                pictureMarkerSymbol.setHeight(30);
                pictureMarkerSymbol.setWidth(30);
            }
            SimpleRenderer simpleRenderer = new SimpleRenderer(pictureMarkerSymbol);
            interestViewsFeatureCollectionTable.setRenderer(simpleRenderer);
            interestViewsFeatureCollectionTable.setTitle("InterestViews");
            insteretViewFeatureCollectionTables.add(interestViewsFeatureCollectionTable);
        }
        //将要素集添加到要素图层
        FeatureCollection interestViewsFeatureCollection = new FeatureCollection(insteretViewFeatureCollectionTables);
        mInterestViewsFeatureCollectionLayer = new FeatureCollectionLayer(interestViewsFeatureCollection);
        mInterestViewsFeatureCollectionLayer.setVisible(false);
        mArcGISMap.getOperationalLayers().add(mInterestViewsFeatureCollectionLayer);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mMapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event)
            {
                Point point = mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY()));
                if(mFragmentStatus != fragmentStatus.RoutingByStops_Unstart)
                {
                    ShowRoadLampInfo(point);
                    ShowInterestViewsInfo(point);
                    return false;
                }
                else
                {
                    stopPointsForShortestRoute.add(point);
                    DrawPoint(point, R.drawable.flag, 30, 30, 15, 15);
                    return false;
                }
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
                                InitFragment();
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
                                            ShowNearDrinks(new Point(minLng, maxLat, SpatialReferences.getWgs84()), new Point(maxLng, minLat, SpatialReferences.getWgs84()), 200);
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
                    InitFragment();
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
                        GetShortestRouteByStopPoints(true);
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

        SwitchCompat lamp_switch = (SwitchCompat)view.findViewById(R.id.show_lamp_switch);
        {
            lamp_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        if(userPosition != null)
                            mMapView.setViewpointCenterAsync(userPosition);
                        mRoadlampsFeatureCollectionLayer.setVisible(true);
                    }
                    else
                    {
                        mRoadlampsFeatureCollectionLayer.setVisible(false);
                    }
                }
            });
        }

        SwitchCompat interest_view_switch = (SwitchCompat)view.findViewById(R.id.interest_view_switch);
        {
            interest_view_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        if(userPosition != null)
                            mMapView.setViewpointCenterAsync(userPosition);
                        mInterestViewsFeatureCollectionLayer.setVisible(true);
                    }
                    else
                        mInterestViewsFeatureCollectionLayer.setVisible(false);
                }
            });
        }

        SwitchCompat drink_switch = (SwitchCompat)view.findViewById(R.id.drink_switch);
        {
            drink_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                    {
                        if(userPosition == null)
                        {
                            ShowToast("无法定位您到当前的位置", Toast.LENGTH_SHORT);
                            return;
                        }
                        else
                        {
                            mMapView.setViewpointCenterAsync(userPosition);
                            if(!haveGotRoute)
                                ShowNearDrinks(userPosition, userPosition, 500);
                        }
                        mDrinkOverlay.setVisible(true);
                    }
                    else
                        mDrinkOverlay.setVisible(false);
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
                    mDrinkOverlay.getGraphics().clear();
                    drinkList.clear();
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
                                SwitchCompat drink_switch = (SwitchCompat)getActivity().findViewById(R.id.drink_switch);
                                drink_switch.setChecked(true);
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

        mShowRoadLampHandler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                try
                {
                    String response = ((Bundle)msg.getData()).getString("RoadLampsString");
                    JSONTokener jsonParser = new JSONTokener(response);
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    JSONArray pointsArray = json.getJSONArray("RoadLamps");

                    boolean roadlamp_Visible = mRoadlampsFeatureCollectionLayer.isVisible();
                    if(roadlamp_Visible)
                    {
                        mRoadlampsFeatureCollectionLayer.setVisible(false);
                        ShowToast("正在更新附近路灯信息", Toast.LENGTH_SHORT);
                    }
                    Iterator<Feature> featureIterator = mRoadlampsFeatureCollectionLayer.getFeatureCollection().getTables().get(ROADLAMP_LAYER_INDEX).iterator();
                    Iterator<Feature> featureIterator_broken = mRoadlampsFeatureCollectionLayer.getFeatureCollection().getTables().get(ROADLAMP_BROKEN_LAYER_INDEX).iterator();
                    while (featureIterator.hasNext())
                    {
                        Feature feature = featureIterator.next();
                        if(roadlampFeatureCollectionTable.canDelete(feature))
                            roadlampFeatureCollectionTable.deleteFeatureAsync(feature);
                    }
                    while (featureIterator_broken.hasNext())
                    {
                        Feature feature = featureIterator_broken.next();
                        if(roadlampBrokenFeatureCollectionTable.canDelete(feature))
                            roadlampBrokenFeatureCollectionTable.deleteFeatureAsync(feature);
                    }

                    FeatureTable roadlampFeatureTable = mRoadlampsFeatureCollectionLayer.getFeatureCollection().getTables().get(ROADLAMP_LAYER_INDEX);

                    for(int i=0;i<pointsArray.length();i++)
                    {
                        JSONObject tempRoadLamp = (JSONObject) pointsArray.get(i);
                        int lampStatus = tempRoadLamp.getInt("IsBroken");
                        Feature tempFeature = roadlampFeatureTable.createFeature();
                        {
                            tempFeature.getAttributes().put("id", tempRoadLamp.getInt("ID"));
                            tempFeature.getAttributes().put("lng", tempRoadLamp.getDouble("Lng"));
                            tempFeature.getAttributes().put("lat", tempRoadLamp.getDouble("Lat"));
                            tempFeature.getAttributes().put("isBroken", lampStatus == 0 ? "false" : "true");
                            tempFeature.getAttributes().put("address", "武汉大学");
                            tempFeature.getAttributes().put("onTime", "18:00");
                            tempFeature.getAttributes().put("offTime", "05:00");
                            tempFeature.setGeometry(new Point(tempRoadLamp.getDouble("Lng"), tempRoadLamp.getDouble("Lat"), SpatialReferences.getWgs84()));
                        }
                        if(lampStatus == 0)
                            roadlampFeatureCollectionTable.addFeatureAsync(tempFeature);
                        else
                            roadlampBrokenFeatureCollectionTable.addFeatureAsync(tempFeature);
                    }
                    if(roadlamp_Visible)
                        mRoadlampsFeatureCollectionLayer.setVisible(true);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        mShowInterestViewsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try
                {
                    String response = ((Bundle)msg.getData()).getString("InterestViews");
                    JSONTokener jsonParser = new JSONTokener(response);
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    JSONArray pointsArray = json.getJSONArray("InterestViews");

                    FeatureTable roadlampFeatureTable = mInterestViewsFeatureCollectionLayer.getFeatureCollection().getTables().get(INTEREST_VIEWS_LAYER_INDEX);

                    for(int i=0;i<pointsArray.length();i++)
                    {
                        JSONObject tempView = (JSONObject) pointsArray.get(i);
                        Feature tempFeature = roadlampFeatureTable.createFeature();
                        {
                            tempFeature.getAttributes().put("lng", tempView.getDouble("Lng"));
                            tempFeature.getAttributes().put("lat", tempView.getDouble("Lat"));
                            tempFeature.getAttributes().put("name", tempView.getString("Name"));
                            tempFeature.getAttributes().put("description", tempView.getString("Description"));
                            tempFeature.getAttributes().put("imageName", tempView.getString("Picture"));
                            tempFeature.setGeometry(new Point(tempView.getDouble("Lng"), tempView.getDouble("Lat"), SpatialReferences.getWgs84()));
                        }
                        mInterestViewsFeatureCollectionLayer.getFeatureCollection().getTables().get(INTEREST_VIEWS_LAYER_INDEX).addFeatureAsync(tempFeature);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        mSetInterestViewSourceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try
                {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if(bitmap != null)
                        ((AppCompatImageView)getActivity().findViewById(R.id.view_image)).setImageBitmap(bitmap);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };

        search_show_roadlamp_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //加载附近search_roadlamp_radius米内的路灯
                if(userPosition != null)
                    ShowNearRoadLamps(userPosition, userPosition, SEARCH_ROADLAMP_RADIUS);
            }
        }, 0, SEARCH_SHOW_ROADLAMP_TIMER_PERIOD);

        //加载景点
        ShowInterestViews(new Point(-180, 90), new Point(180, -90));

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

    private void GetShortestRouteByStopPoints(final boolean isCircle)
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
                if(isCircle)
                    resultPooints.add((Point)GeometryEngine.project(userPosition, SpatialReferences.getWgs84()));
                else
                    resultPooints.add((Point)GeometryEngine.project(stops.get(stops.size() - 1), SpatialReferences.getWgs84()));
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
                ShowNearDrinks(new Point(minLng, maxLat, SpatialReferences.getWgs84()), new Point(maxLng, minLat, SpatialReferences.getWgs84()), 200);
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
                ((SwitchCompat)getActivity().findViewById(R.id.show_lamp_switch)).setChecked(false);
                ((SwitchCompat)getActivity().findViewById(R.id.drink_switch)).setChecked(false);
                ((SwitchCompat)getActivity().findViewById(R.id.interest_view_switch)).setChecked(false);
            }
        });
    }

    private void ShowNearDrinks(Point leftTop, Point rightBottom, double tolerance)
    {
        Point leftTopMkt = (Point)GeometryEngine.project(leftTop, SpatialReferences.getWebMercator());
        Point rightBottomMkt = (Point)GeometryEngine.project(rightBottom, SpatialReferences.getWebMercator());
        final double minLngMkt = leftTopMkt.getX() - tolerance;
        final double maxLngMkt = rightBottomMkt.getX() + tolerance;
        final double minLatMkt = rightBottomMkt.getY() - tolerance;
        final double maxLatMkt = leftTopMkt.getY() + tolerance;

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

    public void ShowNearRoadLamps(Point leftTop, Point rightBottom, double tolerance)
    {
        Point leftTopMkt = (Point)GeometryEngine.project(leftTop, SpatialReferences.getWebMercator());
        Point rightBottomMkt = (Point)GeometryEngine.project(rightBottom, SpatialReferences.getWebMercator());
        final double minLngMkt = leftTopMkt.getX() - tolerance;
        final double maxLngMkt = rightBottomMkt.getX() + tolerance;
        final double minLatMkt = rightBottomMkt.getY() - tolerance;
        final double maxLatMkt = leftTopMkt.getY() + tolerance;

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
                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_roadlamps) + "?minLng=" + minLng +  "&maxLng=" + maxLng + "&minLat=" + minLat + "&maxLat=" + maxLat);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(4000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String content = reader.readLine();
                    Bundle bundle = new Bundle();
                    bundle.putString("RoadLampsString", content);
                    Message message = new Message();
                    message.setData(bundle);
                    mShowRoadLampHandler.sendMessage(message);
                }
                catch (Exception e)
                {
                    ShowToast("获取路灯信息失败", Toast.LENGTH_SHORT);
                }
            }
        });
        showNearDrinkThread.start();
    }

    public void ShowInterestViews(Point leftTop, Point rightBottom)
    {
        final double minLng = leftTop.getX();
        final double maxLng = rightBottom.getX();
        final double minLat = rightBottom.getY();
        final double maxLat = leftTop.getY();
        Thread showInterestViewsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_get_interest_views) + "?minLng=" + minLng +  "&maxLng=" + maxLng + "&minLat=" + minLat + "&maxLat=" + maxLat);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(4000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String content = reader.readLine();
                    Bundle bundle = new Bundle();
                    bundle.putString("InterestViews", content);
                    Message message = new Message();
                    message.setData(bundle);
                    mShowInterestViewsHandler.sendMessage(message);
                }
                catch (Exception e)
                {
                    ShowToast("获取景点信息失败", Toast.LENGTH_SHORT);
                }
            }
        });
        showInterestViewsThread.start();
    }

    /*
    * return:如果路灯要素图层可见且选中要素则返回true，否则返回false
    * */
    public boolean ShowRoadLampInfo(Point clickPoint)
    {
        if(mRoadlampsFeatureCollectionLayer.isVisible())
        {
            double mapTolerance = 15 * mMapView.getUnitsPerDensityIndependentPixel();
            Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mArcGISMap.getSpatialReference());
            QueryParameters query = new QueryParameters();
            query.setGeometry(envelope);
            final ListenableFuture<FeatureQueryResult> feature = mRoadlampsFeatureCollectionLayer.getLayers().get(ROADLAMP_LAYER_INDEX).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
            feature.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        FeatureQueryResult result = feature.get();
                        Iterator<Feature> iterator = result.iterator();
                        while (iterator.hasNext()) {
                            final Feature feature = iterator.next();
                            final Callout callout = mMapView.getCallout();
                            callout.setLocation((Point) feature.getGeometry());
                            callout.setStyle(new Callout.Style(getContext(), R.xml.calloutstyle));
                            View calloutView = getActivity().getLayoutInflater().inflate(R.layout.show_lamp_info_layout, null);
                            ((TextView) calloutView.findViewById(R.id.lamp_address)).setText("位置：" + (String) feature.getAttributes().get("address"));
                            ((TextView) calloutView.findViewById(R.id.lamp_is_broken)).setText("是否损坏：否");
                            ((TextView) calloutView.findViewById(R.id.lamp_on_time)).setText("开灯时间：" + (String) feature.getAttributes().get("onTime"));
                            ((TextView) calloutView.findViewById(R.id.lamp_off_time)).setText("关灯时间：" + (String) feature.getAttributes().get("offTime"));
                            ((Button) calloutView.findViewById(R.id.callout_close)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    callout.dismiss();
                                }
                            });
                            callout.setContent(calloutView);
                            callout.show();
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            final ListenableFuture<FeatureQueryResult> feature_brokenlamp = mRoadlampsFeatureCollectionLayer.getLayers().get(ROADLAMP_BROKEN_LAYER_INDEX).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
            feature.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        FeatureQueryResult result = feature_brokenlamp.get();
                        Iterator<Feature> iterator = result.iterator();
                        while (iterator.hasNext()) {
                            final Feature feature = iterator.next();
                            final Callout callout = mMapView.getCallout();
                            callout.setLocation((Point) feature.getGeometry());
                            callout.setStyle(new Callout.Style(getContext(), R.xml.calloutstyle));
                            View calloutView = getActivity().getLayoutInflater().inflate(R.layout.show_lamp_info_layout, null);
                            ((TextView) calloutView.findViewById(R.id.lamp_address)).setText("位置：" + (String) feature.getAttributes().get("address"));
                            ((TextView) calloutView.findViewById(R.id.lamp_is_broken)).setText("是否损坏：是");
                            ((TextView) calloutView.findViewById(R.id.lamp_on_time)).setText("开灯时间：" + (String) feature.getAttributes().get("onTime"));
                            ((TextView) calloutView.findViewById(R.id.lamp_off_time)).setText("关灯时间：" + (String) feature.getAttributes().get("offTime"));
                            ((Button) calloutView.findViewById(R.id.callout_close)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    callout.dismiss();
                                }
                            });
                            callout.setContent(calloutView);
                            callout.show();
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            return true;
        }
        else
        {
            return false;
        }
    }

    /*
    * return:如果景点要素图层可见则返回true，否则返回false
    * */
    public boolean ShowInterestViewsInfo(final Point clickPoint)
    {
        if(mInterestViewsFeatureCollectionLayer.isVisible())
        {
            double mapTolerance = 15 * mMapView.getUnitsPerDensityIndependentPixel();
            Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mArcGISMap.getSpatialReference());
            QueryParameters query = new QueryParameters();
            query.setGeometry(envelope);
            final ListenableFuture<FeatureQueryResult> feature = mInterestViewsFeatureCollectionLayer.getLayers().get(INTEREST_VIEWS_LAYER_INDEX).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
            feature.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        FeatureQueryResult result = feature.get();
                        Iterator<Feature> iterator = result.iterator();
                        while (iterator.hasNext()) {
                            final Feature feature = iterator.next();
                            final Callout callout = mMapView.getCallout();
                            callout.setLocation((Point) feature.getGeometry());
                            callout.setStyle(new Callout.Style(getContext(), R.xml.calloutstyle));
                            View calloutView = getActivity().getLayoutInflater().inflate(R.layout.show_interest_views_layers, null);
                            final String imageUrlPath = getString(R.string.webapi_host) + getString(R.string.webapi_image_path);
//                            ImageManagerImpl.registerInstance();
//                            x.image().bind((AppCompatImageView)calloutView.findViewById(R.id.view_image), imageUrl, ImageOptions.DEFAULT);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message msg = mSetInterestViewSourceHandler.obtainMessage();
                                    msg.obj = DownLoadImageFromNet(imageUrlPath, (String) feature.getAttributes().get("imageName"));
                                    msg.what =0;
                                    mSetInterestViewSourceHandler.sendMessage(msg);
                                }
                            }).start();
                            ((TextView) calloutView.findViewById(R.id.view_name)).setText("景点名称：" + (String) feature.getAttributes().get("name"));
                            ((TextView) calloutView.findViewById(R.id.view_description)).setText("景点简介：" + (String) feature.getAttributes().get("description"));
                            ((Button) calloutView.findViewById(R.id.callout_close)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    callout.dismiss();
                                }
                            });
                            ((Button) calloutView.findViewById(R.id.near_people_call_it)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    callout.dismiss();
                                    InitFragment();
                                    if(userPosition == null)
                                    {
                                        ShowToast("无法定位您到当前的位置", Toast.LENGTH_SHORT);
                                        return;
                                    }
                                    layoutOfRoutingButton.setVisibility(View.GONE);
                                    layoutOfStartButton.setVisibility(View.VISIBLE);
                                    layoutOfClearPoints.setVisibility(View.VISIBLE);
                                    layoutOfReturnInit.setVisibility(View.VISIBLE);
                                    mFragmentStatus = fragmentStatus.RoutingByStops_Unstart;
                                    stopPointsForShortestRoute.clear();
                                    mMapView.setViewpointCenterAsync(userPosition);
                                    mCurPointGraphic.setGeometry(userPosition);
                                    mCurPointGraphic.setVisible(true);
                                    btnStart.setEnabled(true);

                                    mFragmentStatus = fragmentStatus.RoutingByStops;
                                    layoutOfReturnInit.setVisibility(View.GONE);
                                    btnStart.setText("获取路径...");
                                    btnStart.setEnabled(false);
                                    showLoctionAlways = false;
                                    mCurPointGraphic.setVisible(true);
                                    showLoctionAlways = true;
                                    layoutOfClearPoints.setVisibility(View.GONE);

                                    stopPointsForShortestRoute.add(userPosition);
                                    stopPointsForShortestRoute.add(clickPoint);
                                    GetShortestRouteByStopPoints(false);
                                    DrawPoint(userPosition, R.drawable.start, 22, 33, 0, (float)13.5);
                                    DrawPoint(clickPoint, R.drawable.terminal, 22, 33, 0, (float)13.5);

                                }
                            });
                            callout.setContent(calloutView);
                            callout.show();
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            return true;
        }
        else
        {
            return false;
        }
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
            ShowToast("本次跑步距离过短，将不上传服务器", Toast.LENGTH_SHORT);
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

    public static Bitmap DownLoadImageFromNet(String UrlPath, String UrlName)
    {
        Bitmap bitmap = null;
        InputStream inputStream;
        try {
            URL url = new URL(UrlPath + URLEncoder.encode(UrlName, "UTF-8"));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            {
                httpURLConnection.setRequestProperty("contentType", "GBK");
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(10000);
            }
            int requestCode = httpURLConnection.getResponseCode();
            if(requestCode == 200)
            {
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return bitmap;
    }

}
