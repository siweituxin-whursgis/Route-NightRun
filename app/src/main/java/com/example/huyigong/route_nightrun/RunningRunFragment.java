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
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.example.huyigong.route_nightrun.helpers.CalculateGeometryApi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
    boolean haveGotRoute = false;
    boolean showLoctionAlways = false;
    ArrayList<Point> planningRoute = new ArrayList<Point>();

    LinearLayout layoutOfRoutingButton = null;
    GridLayout layoutOfStartButton = null;
    GridLayout layoutOfContinueButton = null;
    GridLayout layoutOfClearPoints = null;
    Button btnRoutingByDistance = null;
    Button btnRoutingByStops= null;
    Button btnStart = null;
    Button btnContinue = null;
    Button btnClearPoints = null;
    MapView mMapView;   // 地图
    ArcGISMap mArcGISMap;
    Graphic mCurPointGraphic; // 当前点
    PictureMarkerSymbol mCurrentPointSymbol;
    GraphicsOverlay mPointsOverlay;
    GraphicsOverlay mPositionOverlay;
    GraphicsOverlay mLinesOverlay;
    GraphicsOverlay mPolygonsOverlay;
    GraphicsOverlay mOthersOverlay;
    LocationManager mLocationManager; // 定位服务
    ArrayList<Point> allStopPointsList = new ArrayList<Point>();
    ArrayList<Point> stopPointsForShortestRoute = new ArrayList<Point>();
    Point userPosition = null;
    boolean textBool = true;

    //用于引用传递
    private class DoubleReferance{
        private double mValue;

        public  DoubleReferance(double initValue) { mValue = initValue; }

        public void setValue(double value) { mValue = value; }

        public double getValue() { return mValue; }
    }

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null && textBool)
            {
                textBool = false;
//                userPosition = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                userPosition = new Point(114.364, 30.534, SpatialReference.create(4326));
                if((mFragmentStatus == fragmentStatus.RoutingByStops || mFragmentStatus == fragmentStatus.RoutingByDistance) && planningRoute != null && planningRoute.size() > 1)
                {
                    Point nearestPoint = CalculateGeometryApi.GetNearestPointInPolyLine(planningRoute, userPosition);
                    if(nearestPoint != null && Math.sqrt((nearestPoint.getX() - userPosition.getX())*(nearestPoint.getX() - userPosition.getX()) - (nearestPoint.getY() - userPosition.getY())*(nearestPoint.getY() - userPosition.getY())) < 50)
                        userPosition = new Point(nearestPoint.getX(), nearestPoint.getY(), nearestPoint.getSpatialReference());
                }
                if(showLoctionAlways)
                {
                    mMapView.setViewpointCenterAsync(userPosition);
                    mCurPointGraphic.setGeometry(userPosition);
                }
            }
            else
            {
//                Toast.makeText(getContext(), "位置获取出错", Toast.LENGTH_LONG).show();
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
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mArcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 30.541093, 114.360734, 16);
        mPointsOverlay = new GraphicsOverlay();
        mPositionOverlay = new GraphicsOverlay();
        {
            BitmapDrawable pinBitmap = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.boy);
            mCurrentPointSymbol = new PictureMarkerSymbol(pinBitmap);
            mCurrentPointSymbol.setHeight(20);
            mCurrentPointSymbol.setWidth(20);
            // 加载当前点
            mCurrentPointSymbol.loadAsync();
            mCurrentPointSymbol.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurPointGraphic = new Graphic(new Point(mMapView.getPivotX(), mMapView.getY(), SpatialReferences.getWgs84()), mCurrentPointSymbol);
                            mCurPointGraphic.setVisible(false);
                            mPositionOverlay.getGraphics().add(mCurPointGraphic);
                        }
                    });
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
        mMapView.getGraphicsOverlays().add(mPolygonsOverlay);
        mMapView.getGraphicsOverlays().add(mLinesOverlay);
        mMapView.getGraphicsOverlays().add(mPointsOverlay);
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
                Graphic g = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE,8));
                mPointsOverlay.getGraphics().add(g);
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event)
            {
                userPosition = (Point)GeometryEngine.project(mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY())), SpatialReference.create(4326));
                if((mFragmentStatus == fragmentStatus.RoutingByStops || mFragmentStatus == fragmentStatus.RoutingByDistance) && planningRoute != null && planningRoute.size() > 1)
                {
                    Point nearestPoint = CalculateGeometryApi.GetNearestPointInPolyLine(planningRoute, userPosition);
                    Point userPositionMkt = (Point)GeometryEngine.project(userPosition, mMapView.getSpatialReference());
                    double d = Math.sqrt((nearestPoint.getX() - userPositionMkt.getX())*(nearestPoint.getX() - userPositionMkt.getX()) + (nearestPoint.getY() - userPositionMkt.getY())*(nearestPoint.getY() - userPositionMkt.getY()));
                    if(nearestPoint != null && d < 50)
                        userPosition = new Point(nearestPoint.getX(), nearestPoint.getY(), nearestPoint.getSpatialReference());
                }
                if(showLoctionAlways)
                {
                    mMapView.setViewpointCenterAsync(userPosition);
                    mCurPointGraphic.setGeometry(userPosition);
                }
                return false;
            }
        });

        GenerateEndPoinrsFromFile();

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
                        Toast.makeText(getContext(), "获取用户位置失败，请检查定位服务是否开启", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final EditText inputServer = new EditText(getContext());
                    inputServer.setHint("夜跑距离");
                    inputServer.setInputType(InputType.TYPE_CLASS_NUMBER);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("请输入夜跑距离").setIcon(android.R.drawable.ic_dialog_map).setView(inputServer).setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try
                            {
                                planningDistance = Double.parseDouble(inputServer.getText().toString());
                                if(planningDistance > 5000 || planningDistance < 500)
                                {
                                    planningDistance = 0;
                                    Toast.makeText(getContext(), "输入有效值为500~5000，请重新输入", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(getContext(), "本方法较为耗时，请耐心等待", Toast.LENGTH_SHORT).show();
                                    Thread t = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayList<Point> points =  GenerateRouteByDistance(userPosition, planningDistance / 2);
                                            if(points == null)
                                            {
                                                Toast.makeText(getContext(), "路径规划失败", Toast.LENGTH_LONG).show();
                                                InitFragment();
                                                return;
                                            }
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    btnStart.setText("开始夜跑");
                                                    btnStart.setEnabled(true);
                                                    Toast.makeText(getContext(), "点击开始夜跑启动实时定位", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            planningRoute = points;
                                            DrawRoute(points, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3));
                                            mFragmentStatus = fragmentStatus.RoutingByDistance_Unstart;
                                        }
                                    });
                                    t.start();
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                Toast.makeText(getContext(), "输入格式有误", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(), "获取用户位置失败，请检查定位服务是否开启", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getContext(), "双击确定经过站点，点击\"开始夜跑按钮\"产生夜跑路线", Toast.LENGTH_LONG).show();
                    layoutOfRoutingButton.setVisibility(View.GONE);
                    layoutOfStartButton.setVisibility(View.VISIBLE);
                    layoutOfClearPoints.setVisibility(View.VISIBLE);
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
                        mCurPointGraphic.setVisible(true);
                        showLoctionAlways = true;
                    }
                    else if(mFragmentStatus == fragmentStatus.RoutingByStops_Unstart)
                    {
                        if(stopPointsForShortestRoute.size() < 1)
                        {
                            Toast.makeText(getActivity(), "请选择站点", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mFragmentStatus = fragmentStatus.RoutingByStops;
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

        return view;
    }

    private ArrayList<Point> GenerateRouteByDistance(Point pointGps ,double lengthOfRouth)
    {
        try
        {
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
                Toast.makeText(getActivity(), "无适合您当前位置的路径规划", Toast.LENGTH_LONG).show();
                return null;
            }

            double[] distanceOfAllRoutes = new double[pointForRoutePlanning.size()];
            {
                int index = 0;
                for(Point tempPoint : pointForRoutePlanning)
                {
                    if((distanceOfAllRoutes[index++] = GetDistanceOfShortestRoute(point, tempPoint)) == -1)
                    {
                        Toast.makeText(getActivity(), "网络错误，路径规划失败", Toast.LENGTH_LONG).show();
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

            ArrayList<Point> pointsArrayList = GetShortestRoute(point, pointForRoutePlanning.get(indexOfNeareatRoute), null);
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

    public void GenerateEndPoinrsFromMySql()
    {
        try{
            Class.forName("org.gjt.mm.mysql.Driver");
            String url ="jdbc:mysql://139.129.166.245:3306/swtx?user=admin&password=admin&useUnicode=true&characterEncoding=UTF-8";//链接数据库语句
            Connection conn= (Connection) DriverManager.getConnection(url); //链接数据库
            Statement stmt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql="select * from user";//查询user表语句
            ResultSet rs=stmt.executeQuery(sql);//执行查询
            while (rs.next()){
                Log.d("tag", rs.getInt(0) + "\t");
                Log.d("tag", rs.getDouble(1) + "\t");
                Log.d("tag", rs.getDouble(2) + "\t");
                System.out.println();
            }
            rs.close();
            stmt.close();
            conn.close();

        }catch(Exception e)
        {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
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
                ArrayList<Point> points = GetShortestRoute(stops, dr);
                if(points == null)
                {
                    Toast.makeText(getActivity(), "获取路径失败，请检查网络状态或定位服务", Toast.LENGTH_LONG).show();
                    InitFragment();
                    return;
                }

                ArrayList<Point> resultPooints = new ArrayList<Point>();
                resultPooints.add(userPosition);
                for(Point tempPoint : points)
                {
                    resultPooints.add(tempPoint);
                }
                resultPooints.add(userPosition);
                planningRoute = resultPooints;

                DrawRoute(resultPooints, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 4));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMapView.setViewpointCenterAsync(userPosition);
                        mCurPointGraphic.setGeometry(userPosition);
                        mFragmentStatus = fragmentStatus.RoutingByStops;
                        btnStart.setText("暂停");
                        mCurPointGraphic.setVisible(true);
                        showLoctionAlways = true;
                        btnStart.setEnabled(true);
                    }
                });
            }
        });
        t.start();
    }

    @Nullable
    private ArrayList<Point> GetShortestRoute(Point start, Point end, @Nullable DoubleReferance distance)
    {
        ArrayList<Point> pointsArrayList = new ArrayList<Point>();
        try
        {
            start = (Point)GeometryEngine.project(start, SpatialReference.create(4326));
            end = (Point)GeometryEngine.project(end, SpatialReference.create(4326));
            //ArcGIS最短路径服务
//            String urlString = "https://utility.arcgis.com/usrsvcs/appservices/7lLQlGpstsTR8hpm/rest/services/World/Route/NAServer/Route_World/solve?token=OU0KQBwzj2vm3AV7EvTzWMtwkFRK0VxLkWiZKT6TwQoRIr1mEIESvPHP50ufoQAJ-qhiEne93ThUi5rxvHFMSMULBt5qb2CEpu05EDMjKagstbDxK7sCj4qrAEJywpq70qxEPPQfHOirXv2Umol21w..&f=json&stops=114,30;114.5,31";
            //graphhopper最短路径服务
            String urlString = "https://graphhopper.com/api/1/route?point=" + start.getY() + "," + start.getX() + "&point=" + end.getY() + "," + end.getX() + "&vehicle=foot&debug=false&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
            HttpURLConnection connection = null;
            {
                URL url = new URL(urlString);
                connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(4000);
            }
            InputStream in = connection.getInputStream();
            BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
            String response = "";
            String line = "";
            while((line = bufr.readLine()) != null){
                response += line;
            }
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject json = (JSONObject)jsonParser.nextValue();
            JSONObject pathsJsonObject = ((JSONObject)json.getJSONArray("paths").get(0));
            JSONArray pointsArray = pathsJsonObject.getJSONObject("points").getJSONArray("coordinates");

            int count = pointsArray.length();
            for(int i=0;i<count;i++)
            {
                JSONArray pointJsonArray = (JSONArray) pointsArray.get(i);
                Double lng = pointJsonArray.getDouble(0);
                Double lat = pointJsonArray.getDouble(1);

                pointsArrayList.add(new Point(lng, lat, SpatialReferences.getWgs84()));
            }
            if(distance != null)
                distance.setValue(pathsJsonObject.getDouble("distance"));

            return  pointsArrayList;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Nullable
    private ArrayList<Point> GetShortestRoute(ArrayList<Point> stopPoints, @Nullable DoubleReferance distance)
    {
        ArrayList<Point> pointsArrayList = new ArrayList<Point>();
        ArrayList<Point> stopPointsWgs84 = new ArrayList<Point>();
        try
        {
            for(int i=0;i<stopPoints.size();i++)
            {
                stopPointsWgs84.add((Point)GeometryEngine.project(stopPoints.get(i), SpatialReference.create(4326)));
            }
            //ArcGIS最短路径服务
//            String urlString = "https://utility.arcgis.com/usrsvcs/appservices/7lLQlGpstsTR8hpm/rest/services/World/Route/NAServer/Route_World/solve?token=OU0KQBwzj2vm3AV7EvTzWMtwkFRK0VxLkWiZKT6TwQoRIr1mEIESvPHP50ufoQAJ-qhiEne93ThUi5rxvHFMSMULBt5qb2CEpu05EDMjKagstbDxK7sCj4qrAEJywpq70qxEPPQfHOirXv2Umol21w..&f=json&stops=114,30;114.5,31";
            //graphhopper最短路径服务
            String baseUrlString = "https://graphhopper.com/api/1/route";
            String pointParamString = "";
            {
                for(int i=0;i<stopPoints.size();i++)
                {
                    pointParamString += "point=" + (stopPointsWgs84.get(i).getY() + "," + stopPointsWgs84.get(i).getX() + "&");
                }
            }
            String othersParamString = "vehicle=foot&debug=false&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
            String urlString = baseUrlString + "?" + pointParamString + othersParamString;
            HttpURLConnection connection = null;
            URL url = new URL(urlString);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(4000);
            InputStream in = connection.getInputStream();
            BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
            String response = "";
            String line = "";
            while((line = bufr.readLine()) != null){
                response += line;
            }
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject json = (JSONObject)jsonParser.nextValue();
            JSONObject pathsJsonObject = ((JSONObject)json.getJSONArray("paths").get(0));
            JSONArray pointsArray = pathsJsonObject.getJSONObject("points").getJSONArray("coordinates");

            int count = pointsArray.length();
            for(int i=0;i<count;i++)
            {
                JSONArray pointJsonArray = (JSONArray) pointsArray.get(i);
                Double lng = pointJsonArray.getDouble(0);
                Double lat = pointJsonArray.getDouble(1);

                pointsArrayList.add(new Point(lng, lat, SpatialReferences.getWgs84()));
            }
            if(distance != null)
                distance.setValue(pathsJsonObject.getDouble("distance"));

            return  pointsArrayList;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    private double GetDistanceOfShortestRoute(Point start, Point end)
    {
        double distance = -1;
        try
        {
            start = (Point)GeometryEngine.project(start, SpatialReference.create(4326));
            end = (Point)GeometryEngine.project(end, SpatialReference.create(4326));
            //ArcGIS最短路径服务
//            String urlString = "https://utility.arcgis.com/usrsvcs/appservices/7lLQlGpstsTR8hpm/rest/services/World/Route/NAServer/Route_World/solve?token=OU0KQBwzj2vm3AV7EvTzWMtwkFRK0VxLkWiZKT6TwQoRIr1mEIESvPHP50ufoQAJ-qhiEne93ThUi5rxvHFMSMULBt5qb2CEpu05EDMjKagstbDxK7sCj4qrAEJywpq70qxEPPQfHOirXv2Umol21w..&f=json&stops=114,30;114.5,31";
            //graphhopper最短路径服务
            String urlString = "https://graphhopper.com/api/1/route?point=" + start.getY() + "," + start.getX() + "&point=" + end.getY() + "," + end.getX() + "&vehicle=foot&debug=false&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
            HttpURLConnection connection = null;
            URL url = new URL(urlString);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(4000);
            InputStream in = connection.getInputStream();
            BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
            String response = "";
            String line = "";
            while((line = bufr.readLine()) != null){
                response += line;
            }
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject json = (JSONObject)jsonParser.nextValue();
            JSONObject pathsJsonObject = ((JSONObject)json.getJSONArray("paths").get(0));
            distance = pathsJsonObject.getDouble("distance");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            return distance;
        }
    }

    private void DrawRoute(ArrayList<Point> pointsList, Symbol symbol)
    {
        try
        {
            PointCollection pointCollection = new PointCollection(pointsList);
            Polyline path = new Polyline(pointCollection, SpatialReference.create(4326));
            final Graphic graphic = new Graphic(path, symbol);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLinesOverlay.getGraphics().add(graphic);
                }
            });
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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
                planningDistance = 0;
                haveGotRoute = false;
                mCurPointGraphic.setVisible(false);
                btnStart.setText("开始夜跑");
                btnStart.setEnabled(false);
                showLoctionAlways = false;
                mPointsOverlay.getGraphics().clear();
                mLinesOverlay.getGraphics().clear();
                planningRoute.clear();
            }
        });
    }
}
