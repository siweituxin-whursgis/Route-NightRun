package com.example.huyigong.route_nightrun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
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
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.example.huyigong.route_nightrun.substances.Gym;
import com.example.huyigong.route_nightrun.substances.GymsInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class IndoorRunActivity extends AppCompatActivity {
    /**
     * 地图视图
     */
    MapView mMapView;
    /**
     * 地图
     */
    ArcGISMap mArcGISMap;
    /**
     * 健身房要素表
     */
    FeatureCollectionTable mGymFeatureTable;
    /**
     * 健身房要素图层
     */
    FeatureCollectionLayer mGymFeatureCollectionLayer;
    /**
     * 路径图层
     */
    GraphicsOverlay mRouteOverlay;
    /**
     * 起点、终点图层
     */
    GraphicsOverlay mStartEndOverlay;
    /**
     * 显示健身房处理
     */
    Handler mShowGymHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_run);
        // 设置位置管理器
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        } catch (SecurityException se) {
            Toast.makeText(getApplicationContext(), "未开启定位权限", Toast.LENGTH_SHORT).show();
        }
        // 初始化地图
        initMap();
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getApplicationContext(), mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                double mapTolerance = 10 * mMapView.getUnitsPerDensityIndependentPixel();
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mArcGISMap.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);
                final ListenableFuture<FeatureQueryResult> future = mGymFeatureCollectionLayer.getLayers().get(0).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                try {
                    future.addDoneListener(new ShowCalloutRunnable(future.get()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        // 显示健身房详情按钮
        Button gym_info = (Button) findViewById(R.id.info);
        if (gym_info != null) {
            gym_info.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent togym = new Intent(IndoorRunActivity.this,GymInfoActivity.class);
                    startActivity(togym);
                }
            });
        }
        // 显示健身房按钮
        mShowGymHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Gym[] gyms = (Gym[]) msg.getData().getParcelableArray("Gyms");
                ArrayList<Feature> features = new ArrayList<>();
                if (gyms != null) {
                    for (Gym gym : gyms) {
                        Feature feature = mGymFeatureTable.createFeature();
                        feature.getAttributes().put("GymID", gym.getGymID());
                        feature.getAttributes().put("GymName", gym.getGymName());
                        feature.getAttributes().put("GymAddress", gym.getGymAddress());
                        feature.getAttributes().put("GymCall", gym.getGymCall());
                        feature.getAttributes().put("GymLng", gym.getGymLng());
                        feature.getAttributes().put("GymLat", gym.getGymLat());
                        feature.setGeometry(new Point(gym.getGymLng(), gym.getGymLat()));
                        features.add(feature);
                    }
                }
                mGymFeatureTable.addFeaturesAsync(features);
            }
        };
        Button gym_btn = (Button) findViewById(R.id.gym);
        if (gym_btn != null) {
            gym_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new FetchGymRunnable()).start();
                }
            });
        }
        // 根据Intent，显示路径导航
        Intent intent = getIntent();
        Parcelable parcelable = intent.getParcelableExtra("Gym");
        if (parcelable != null && parcelable instanceof Gym) {
            final Gym gym = (Gym) parcelable;
            if (mCurLocation == null) {
                mCurLocation = getLocation();
            }
            if (mCurLocation != null) {
                new Thread(new RouteRunnable(
                        new Point(mCurLocation.getLongitude(), mCurLocation.getLatitude(), SpatialReferences.getWgs84()),
                        new Point(gym.getGymLng(), gym.getGymLat(), SpatialReferences.getWgs84()))).start();
            } else {
                Toast.makeText(getApplicationContext(), "无法获取当前位置", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.mapView);
        mArcGISMap= new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 30.5303, 114.3547, 14);
        mGymFeatureTable = new FeatureCollectionTable(createGymFields(), GeometryType.POINT, SpatialReferences.getWgs84());
        mGymFeatureTable.setRenderer(createGymRenderer());
        mGymFeatureTable.setTitle("健身房");
        ArrayList<FeatureCollectionTable> tables = new ArrayList<>();
        tables.add(mGymFeatureTable);
//        FeatureLayer featureLayer = new FeatureLayer(mGymFeatureTable);
        FeatureCollection gymFeatureCollection = new FeatureCollection(tables);
        mGymFeatureCollectionLayer = new FeatureCollectionLayer(gymFeatureCollection);
        mArcGISMap.getOperationalLayers().add(mGymFeatureCollectionLayer);
        mMapView.setMap(mArcGISMap);
        mRouteOverlay = new GraphicsOverlay();
        mStartEndOverlay = new GraphicsOverlay();
        mRouteOverlay.setRenderer(createRouteRenderer());
        mMapView.getGraphicsOverlays().add(mRouteOverlay);
        mMapView.getGraphicsOverlays().add(mStartEndOverlay);
    }

    /**
     * 点击图标显示弹窗线程
     */
    private class ShowCalloutRunnable implements Runnable {
        FeatureQueryResult mResult;

        /**
         * 显示弹窗线程构造函数
         * @param result 要素窗口查询结果
         */
        ShowCalloutRunnable(FeatureQueryResult result) {
            mResult = result;
        }

        @Override
        public void run() {
            try {
                for (final Feature feature : mResult) {
                    final Callout callout = mMapView.getCallout();
                    callout.setLocation((Point) feature.getGeometry());
                    callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.calloutstyle));
                    View calloutView = View.inflate(getApplicationContext(), R.layout.gym_callout_layout, null);
                    ((TextView) calloutView.findViewById(R.id.gym_callout_name)).setText((String) feature.getAttributes().get("GymName"));
                    ((TextView) calloutView.findViewById(R.id.gym_callout_address)).setText((String) feature.getAttributes().get("GymAddress"));
                    ((TextView) calloutView.findViewById(R.id.gym_callout_call)).setText((String) feature.getAttributes().get("GymCall"));
                    ((Button) calloutView.findViewById(R.id.gym_callout_close)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMapView.getCallout().dismiss();
                        }
                    });
                    ((Button) calloutView.findViewById(R.id.gym_callout_goto)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCurLocation == null) {
                                mCurLocation = getLocation();
                            }
                            if (mCurLocation != null) {
                                Point cur = new Point(mCurLocation.getLongitude(), mCurLocation.getLatitude(), SpatialReferences.getWgs84());
                                Point gym = new Point((double) feature.getAttributes().get("GymLng"), (double) feature.getAttributes().get("GymLat"), SpatialReferences.getWgs84());
                                new Thread(new RouteRunnable(cur, gym)).start();
                                callout.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "无法获取当前位置", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    callout.setContent(calloutView);
                    callout.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取健身房线程
     */
    private class FetchGymRunnable implements Runnable{
        @Override
        public void run() {
            try {
                URL url = new URL(getString(R.string.webapi_host) + getString(R.string.webapi_root) + getString(R.string.webapi_gym_info));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gson = new Gson();
                Gym[] gyms = gson.fromJson(reader.readLine(), GymsInfo.class).getGyms();
                Bundle bundle = new Bundle();
                bundle.putParcelableArray("Gyms", gyms);
                Message message = new Message();
                message.setData(bundle);
                mShowGymHandler.sendMessage(message);
            } catch (Exception e) {
                Log.i("获取健身房Feature失败", e.getMessage());
            }
        }
    }


    /**
     * 规划路线后台线程
     */
    private class RouteRunnable implements Runnable {
        Point mGym;
        Point mLocal;

        /**
         * 规划路线首台线程构造函数
         * @param location 当前位置
         * @param gym 目标健身房
         */
        RouteRunnable(Point location, Point gym) {
            mLocal = location;
            mGym = gym;
        }

        @Override
        public void run() {
            try {
                String routeUrlFormat = "https://graphhopper.com/api/1/route?point=%f,%f&point=%f,%f&vehicle=foot&key=f8821850-c1f8-4f8f-befb-f976c887ebfb&type=json&points_encoded=false";
                String routeUrlString = String.format(routeUrlFormat, mLocal.getY(),mLocal.getX(),mGym.getY(),mGym.getX());
                URL routeURL = new URL(routeUrlString);
                HttpURLConnection routeConnection = (HttpURLConnection) routeURL.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(routeConnection.getInputStream()));
                if (routeConnection.getResponseCode() == 200) {
                    String jsonString = br.readLine();
                    JSONTokener jsonTokener = new JSONTokener(jsonString);
                    JSONObject root = (JSONObject) jsonTokener.nextValue();
                    showGraphHopperRoutes(root, mLocal, mGym);
                    System.out.println();
                } else {
                    throw new Exception("服务器返回错误" + routeConnection.getResponseCode());
                }
            } catch (Exception e) {
                Log.i("获取导航路径失败", e.getMessage());
            }
        }
    }

    /**
     * 创建健身房Fields
     * @return 健身房Fields
     */
    private ArrayList<Field> createGymFields() {
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(Field.createInteger("GymID", "编号"));
        fields.add(Field.createString("GymName", "名称", 255));
        fields.add(Field.createString("GymAddress", "地址", 255));
        fields.add(Field.createString("GymCall", "电话", 255));
        fields.add(Field.createDouble("GymLng", "经度"));
        fields.add(Field.createDouble("GymLat", "纬度"));
        return fields;
    }

    /**
     * 创建渲染器
     * @return 渲染器
     */
    private Renderer createGymRenderer() {
        BitmapDrawable bitmap = (BitmapDrawable) getDrawable(R.drawable.gymicon);
        Symbol symbol = null;
        if (bitmap != null) {
            symbol = new PictureMarkerSymbol(bitmap);
            ((PictureMarkerSymbol) symbol).setHeight(30);
            ((PictureMarkerSymbol) symbol).setWidth(30);
        }
        else {
            symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.argb(192, 0, 16, 233), 15);
        }
        return new SimpleRenderer(symbol);
    }

    /**
     * 创建路径渲染器
     * @return 渲染器
     */
    private Renderer createRouteRenderer() {
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(255, 0, 16, 233), 3);
        return new SimpleRenderer(lineSymbol);
    }

    /**
     * 当前位置
     */
    Location mCurLocation;
    /**
     * 定位管理器
     */
    LocationManager mLocationManager;
    /**
     * 位置监听器
     */
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurLocation = location;
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

    /**
     * 获取设备当前坐标
     * @return 坐标
     */
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
            Toast.makeText(this, "未开启定位权限" + se.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        } catch (Exception e) {
            Toast.makeText(this, "其他错误" + e.getMessage(), Toast.LENGTH_LONG).show();
//            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * 显示路线
     * @param routes 路线JSON对象
     * @param start 起点
     * @param end 终点
     */
    void showGraphHopperRoutes(JSONObject routes, Point start, Point end) {
        try {
            JSONArray pathsArray = routes.getJSONArray("paths");
            for (int i = 0; i < pathsArray.length(); i++) {
                JSONObject pathObject = pathsArray.getJSONObject(i);
                PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                JSONArray pointsArray = pathObject.getJSONObject("points").getJSONArray("coordinates");
                for (int p = 0; p < pointsArray.length(); p++) {
                    JSONArray pointArray = pointsArray.getJSONArray(p);
                    Point point = new Point(pointArray.getDouble(0), pointArray.getDouble(1));
                    points.add(point);
                }
                Polyline polyline = new Polyline(points);
                mRouteOverlay.getGraphics().add(new Graphic(polyline));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BitmapDrawable startDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.start, null);
            BitmapDrawable endDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.terminal, null);
            Symbol startSymbol = new PictureMarkerSymbol(startDraw);
            Symbol endSymbol = new PictureMarkerSymbol(endDraw);
            ((PictureMarkerSymbol) startSymbol).setWidth(25);
            ((PictureMarkerSymbol) startSymbol).setHeight(30);
            ((PictureMarkerSymbol) startSymbol).setOffsetY(10);
            ((PictureMarkerSymbol) endSymbol).setWidth(25);
            ((PictureMarkerSymbol) endSymbol).setHeight(30);
            ((PictureMarkerSymbol) endSymbol).setOffsetY(10);
            mStartEndOverlay.getGraphics().add(new Graphic(start, startSymbol));
            mStartEndOverlay.getGraphics().add(new Graphic(end, endSymbol));
        }
    }
    /**
     * 显示路线
     * @param routes 路线JSON对象
     * @param start 起点
     * @param end 终点
     */
    void showArcGISRoutes(JSONObject routes, Point start, Point end) {
        try {
            JSONArray featuresJSON = routes.getJSONObject("points").getJSONArray("coordinates");
            for (int i = 0; i < featuresJSON.length(); i++) {
                JSONObject featureJSON = featuresJSON.getJSONObject(i);
                JSONObject geometryJSON = featureJSON.getJSONObject("geometry");
                JSONArray pathsJSON = geometryJSON.getJSONArray("paths");
                for (int j = 0; j < pathsJSON.length(); j++) {
                    JSONArray pathJSON = pathsJSON.getJSONArray(j);
                    PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                    for (int p = 0; p < pathJSON.length(); p++) {
                        JSONArray pointJSON = pathJSON.getJSONArray(p);
                        points.add(pointJSON.getDouble(0), pointJSON.getDouble(1));
                    }
                    Polyline polyline = new Polyline(points);
//                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.DKGRAY, 3);
                    mRouteOverlay.getGraphics().add(new Graphic(polyline));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureMarkerSymbol startSymbol = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.start, null));
            PictureMarkerSymbol endSymbol = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.terminal, null));
            startSymbol.setWidth(25);
            startSymbol.setHeight(30);
            startSymbol.setOffsetY(10);
            endSymbol.setWidth(25);
            endSymbol.setHeight(30);
            endSymbol.setOffsetY(10);
            mStartEndOverlay.getGraphics().add(new Graphic(start, startSymbol));
            mStartEndOverlay.getGraphics().add(new Graphic(end, endSymbol));
        }
    }

}
