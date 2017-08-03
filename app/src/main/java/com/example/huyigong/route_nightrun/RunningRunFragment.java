package com.example.huyigong.route_nightrun;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.internal.jni.CoreRoute;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.google.gson.JsonObject;

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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
//import org.gjt.mm.mysql.Driver;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunningRunFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunningRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningRunFragment extends Fragment{
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

    MapView mMapView;   // 地图
    ArcGISMap mArcGISMap;
    Graphic mCurPointGraphic; // 当前点
    GraphicsOverlay mPointsOverlay;
    GraphicsOverlay mLinesOverlay;
    GraphicsOverlay mPolygonsOverlay;
    GraphicsOverlay mOthersOverlay;
    LocationManager mLocationManager; // 定位服务
    ArrayList<Point> allStopPointsList = new ArrayList<Point>();
    ArrayList<Point> stopPointsForShortestRoute = new ArrayList<Point>();

    //用于引用传递
    private class DoubleReferance{
        private double mValue;

        public  DoubleReferance()
        {
            mValue = 0;
        }

        public  DoubleReferance(double initValue)
        {
            mValue = initValue;
        }

        public void setValue(double value)
        {
            mValue = value;
        }

        public double getValue()
        {
            return mValue;
        }
    }

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null)
            {
//                Point pinPoint = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
//                mMapView.setViewpointCenterAsync(pinPoint);
//                mCurPointGraphic.setGeometry(pinPoint);
            }
            else
            {
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
//       super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_running, container, false);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try
        {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        catch (SecurityException se)
        {
            Toast.makeText(getContext(), "未开启定位权限", Toast.LENGTH_SHORT).show();
        }
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mArcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 30.541093, 114.360734, 16);
        mPointsOverlay = new GraphicsOverlay();
        {
            mCurPointGraphic = new Graphic(new Point(mMapView.getPivotX(), mMapView.getY(), SpatialReferences.getWgs84()), new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 16));
            mPointsOverlay.getGraphics().add(mCurPointGraphic);
        }
        mLinesOverlay = new GraphicsOverlay();
        mPolygonsOverlay = new GraphicsOverlay();
        mOthersOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mPointsOverlay);
        mMapView.getGraphicsOverlays().add(mLinesOverlay);
        mMapView.getGraphicsOverlays().add(mPolygonsOverlay);
        mMapView.getGraphicsOverlays().add(mOthersOverlay);
        mMapView.setMap(mArcGISMap);

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getContext(), mMapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event)
            {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event)
            {
//                final Point point = mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY()));
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        GenerateRouteByDistance(point ,1000);
//                    }
//                });
//                t.start();
                Point point = mMapView.screenToLocation(new android.graphics.Point((int)event.getX(), (int)event.getY()));
                stopPointsForShortestRoute.add(point);
                final Graphic g = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED,16));
                mPointsOverlay.getGraphics().add(g);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DoubleReferance dr = new DoubleReferance(0);
                        ArrayList<Point> points = GetShortestRoute(stopPointsForShortestRoute, dr);
                        stopPointsForShortestRoute.clear();
                        if(points == null)
                            return;
                        double distance1 = dr.getValue();
                        ArrayList<Point> backPoints = GetShortestRoute(points.get(points.size() - 1), points.get(0), dr);
                        if(backPoints == null)
                            return;
                        mLinesOverlay.getGraphics().clear();
                        mPointsOverlay.getGraphics().clear();
                        DrawRoute(points, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 4));
                        DrawRoute(backPoints, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2));
                        Looper.prepare();
                        Toast.makeText(getActivity(), "路径长度：" + (dr.getValue() + distance1) + "m", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                });
                t.start();
            }
        });

        GenerateEndPoinrsFromFile();
        return view;
    }

    private void GenerateRouteByDistance(Point point ,double lengthOfRouth)
    {
        try
        {
            final Graphic g = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED,16));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPointsOverlay.getGraphics().add(g);
                }
            });

            Point leftTop = new Point(point.getX() - lengthOfRouth, point.getY() - lengthOfRouth, point.getSpatialReference());
            Point rightButtom = new Point(point.getX() + lengthOfRouth, point.getY() + lengthOfRouth, point.getSpatialReference());
            Point leftTopGps = (Point)GeometryEngine.project(leftTop, SpatialReference.create(4326));
            Point rightButtomGps = (Point)GeometryEngine.project(rightButtom, SpatialReference.create(4326));

            ArrayList<Point> pointForRoutePlanning = new ArrayList<Point>();
            for (Point tempPoint : allStopPointsList)
            {
                if (tempPoint.getX() < rightButtomGps.getX() && tempPoint.getX() > leftTopGps.getX() && tempPoint.getY() < rightButtomGps.getY() && tempPoint.getY() > leftTopGps.getY())
                    pointForRoutePlanning.add(tempPoint);
            }

            if(pointForRoutePlanning.size() == 0)
            {
                Looper.prepare();
                Toast.makeText(getActivity(), "无适合您当前位置的路径规划", Toast.LENGTH_LONG).show();
                Looper.loop();
                return;
            }

            double[] distanceOfAllRoutes = new double[pointForRoutePlanning.size()];
            {
                int index = 0;
                for(Point tempPoint : pointForRoutePlanning)
                {
                    if((distanceOfAllRoutes[index++] = GetDistanceOfShortestRoute(point, tempPoint)) == -1)
                    {
                        Looper.prepare();
                        Toast.makeText(getActivity(), "网络错误，路径规划失败", Toast.LENGTH_LONG).show();
                        Looper.loop();
                        return;
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
            DrawRoute(pointsArrayList, new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3));
            Looper.prepare();
            Toast.makeText(getActivity(), "路径规划长度：" + NearestDistence + "m", Toast.LENGTH_LONG).show();
            Looper.loop();
        }
        catch (Exception ex)
        {
            Looper.prepare();
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Looper.loop();
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

                Graphic graphic = new Graphic(point, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLUE, 8));
                mPointsOverlay.getGraphics().add(graphic);
            }
        }
        catch(Exception e)
        {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
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
            String urlString = "https://graphhopper.com/api/1/route?point=" + start.getY() + "," + start.getX() + "&point=" + end.getY() + "," + end.getX() + "&vehicle=car&debug=true&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
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
            String othersParamString = "vehicle=car&debug=true&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
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
            String urlString = "https://graphhopper.com/api/1/route?point=" + start.getY() + "," + start.getX() + "&point=" + end.getY() + "," + end.getX() + "&vehicle=car&debug=true&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
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
        PointCollection pointCollection = new PointCollection(pointsList);
        Polyline path = new Polyline(pointCollection, SpatialReference.create(4326));

        final Graphic graphic = new Graphic(path, symbol);getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
            mLinesOverlay.getGraphics().add(graphic);
        }
    });
    }
}
