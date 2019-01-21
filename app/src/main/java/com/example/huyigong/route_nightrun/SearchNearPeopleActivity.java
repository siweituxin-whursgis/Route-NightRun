package com.example.huyigong.route_nightrun;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.example.huyigong.route_nightrun.substances.NightRunner;
import com.example.huyigong.route_nightrun.substances.NightRunnersInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SearchNearPeopleActivity extends AppCompatActivity {
    static String NEAR_PEOPLE_SEARCH;  // 查找附近的人
    static String NEAR_PEOPLE_CALL; // 呼叫附近的人
    MapView mMapView;   // 地图
    ArcGISMap mArcGISMap;
    Graphic mCurPointGraphic; // 当前点
    PictureMarkerSymbol mPinSymbol;
    GraphicsOverlay mGraphicsOverlay;
    private final int NEAR_PEOPLE_TABLE_INDEX = 0;
    FeatureCollectionLayer mFeatureCollectionLayer;
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
                Toast.makeText(getApplicationContext(), "位置获取出错", Toast.LENGTH_LONG).show();
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

    Timer mNearPeopleTimer;

    ArrayList<Field> createFields() {
        ArrayList<Field> fields = new ArrayList<Field>();
        fields.add(Field.createInteger("UserID", "ID"));
        fields.add(Field.createString("UserName", "姓名", 255));
        fields.add(Field.createInteger("UserGender", "性别"));
        fields.add(Field.createInteger("UserAverageRunTime", "平均奔跑时长"));
        fields.add(Field.createString("UserAddress", "地址", 255));
        fields.add(Field.createDouble("PositionLat", "纬度"));
        fields.add(Field.createDouble("PositionLng", "经度"));
        return fields;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_near_people);
        // wo
        NEAR_PEOPLE_SEARCH = getResources().getString(R.string.webapi_host)
                + getResources().getString(R.string.webapi_root)
                + getResources().getString(R.string.webapi_nearpeople);
        NEAR_PEOPLE_CALL = getResources().getString(R.string.webapi_host)
                + getResources().getString(R.string.webapi_root)
                + getResources().getString(R.string.webapi_nearpeoplecall);
        // 创建地图
        mArcGISMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 30.541093, 114.360734, 16);
        mGraphicsOverlay = new GraphicsOverlay();
        // 创建要素图层
        FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(createFields(), GeometryType.POINT, SpatialReferences.getWgs84());
        PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol((BitmapDrawable) getDrawable(R.drawable.boy_red));
        pictureMarkerSymbol.setHeight(30);
        pictureMarkerSymbol.setWidth(30);
        SimpleRenderer simpleRenderer = new SimpleRenderer(pictureMarkerSymbol);
        featureCollectionTable.setRenderer(simpleRenderer);
        featureCollectionTable.setTitle("NearPeople");
        ArrayList<FeatureCollectionTable> mFeatureCollectionTables = new ArrayList<>();
        mFeatureCollectionTables.add(featureCollectionTable);
        FeatureCollection mFeatureCollection = new FeatureCollection(mFeatureCollectionTables);
        mFeatureCollectionLayer = new FeatureCollectionLayer(mFeatureCollection);
        mArcGISMap.getOperationalLayers().add(mFeatureCollectionLayer);
        // 显示地图
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        } catch (SecurityException se) {
            Toast.makeText(getApplicationContext(), "未开启定位权限", Toast.LENGTH_SHORT).show();
        }
        // 显示控件
        mMapView = (MapView) findViewById(R.id.talk_map_view);
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        mMapView.setMap(mArcGISMap);
        // 添加当前点
        BitmapDrawable pinBitmap = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.boy);
        mPinSymbol = new PictureMarkerSymbol(pinBitmap);
        mPinSymbol.setHeight(40);
        mPinSymbol.setWidth(40);
        // 加载当前点
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
                    Toast.makeText(getApplicationContext(), "无法获取位置", Toast.LENGTH_LONG).show();
                }
            }
        });
        // 添加单击显示要素
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getApplicationContext(), mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                double mapTolerance = 10 * mMapView.getUnitsPerDensityIndependentPixel();
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mArcGISMap.getSpatialReference());
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);
                final ListenableFuture<FeatureQueryResult> future = mFeatureCollectionLayer.getLayers().get(NEAR_PEOPLE_TABLE_INDEX).selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                future.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FeatureQueryResult result = future.get();
                            Iterator<Feature> iterator = result.iterator();

                            while (iterator.hasNext()) {
                                final Feature feature = iterator.next();
                                final Callout callout = mMapView.getCallout();
                                callout.setLocation((Point) feature.getGeometry());
                                callout.setStyle(new Callout.Style(getApplicationContext(), R.xml.calloutstyle));
                                View calloutView = View.inflate(getApplicationContext(), R.layout.search_friend_callout_layout, null);
                                ((TextView) calloutView.findViewById(R.id.near_people_name)).setText((String) feature.getAttributes().get("UserName"));
                                String gender = ((int) feature.getAttributes().get("UserGender")) == 1 ? "男生" : "女生";
                                ((TextView) calloutView.findViewById(R.id.near_people_sex)).setText(gender);
                                Date date = new Date(((int) feature.getAttributes().get("UserAverageRunTime")) * 1000);
                                SimpleDateFormat format = new SimpleDateFormat("HH时mm分ss秒");
                                ((TextView) calloutView.findViewById(R.id.near_people_runtime)).setText(format.format(date));
                                ((TextView) calloutView.findViewById(R.id.near_people_address)).setText((String) feature.getAttributes().get("UserAddress"));
                                ((Button) calloutView.findViewById(R.id.near_people_call_it)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String strURL = NEAR_PEOPLE_CALL + "?UserName=" + (String) feature.getAttributes().get("UserName");
                                        try {
                                            int statusCode = 1;
                                            if (statusCode == 1) {
                                                Toast.makeText(getApplicationContext(), "已发送请求", Toast.LENGTH_SHORT).show();
                                                mReceiveReportTimer.schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        mReceiveReportHandler.sendMessage(new Message());
                                                    }
                                                }, 10000);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "发送请求失败", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "发送请求失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                ((Button) calloutView.findViewById(R.id.callout_close)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        callout.dismiss();
                                    }
                                });
                                callout.setContent(calloutView);
                                callout.show();
                            }
                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "Select feature Failed: " + e.getMessage());
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });

        // 获取最近的人
        mNearPeopleTimer = new Timer();
        mNearPeopleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    FeatureCollectionTable table = mFeatureCollectionLayer.getFeatureCollection().getTables().get(NEAR_PEOPLE_TABLE_INDEX);
                    String strURL = NEAR_PEOPLE_SEARCH + "?UserID=1";
                    URL url = new URL(strURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    String jsonString = br.readLine();
                    Gson gson = new Gson();
                    NightRunnersInfo nightRunnersInfo = gson.fromJson(jsonString, NightRunnersInfo.class);
                    for (NightRunner nightRunner : nightRunnersInfo.getNearPeople()) {
                        Feature feature = table.createFeature();
                        feature.getAttributes().put("UserID", nightRunner.getUserID());
                        feature.getAttributes().put("UserName", nightRunner.getUserName());
                        feature.getAttributes().put("UserGender", nightRunner.getUserGender());
                        feature.getAttributes().put("UserAverageRunTime", nightRunner.getUserAverageRunTime());
                        feature.getAttributes().put("UserAddress", nightRunner.getUserAddress());
                        feature.getAttributes().put("PositionLat", nightRunner.getPositionLat());
                        feature.getAttributes().put("PositionLng", nightRunner.getPositionLng());
                        feature.setGeometry(new Point(nightRunner.getPositionLng(), nightRunner.getPositionLat(), SpatialReferences.getWgs84()));
                        table.addFeatureAsync(feature);
                    }
                } catch (MalformedURLException me) {
//                    Toast.makeText(getContext(), me.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println("无法连接到网络：" + me.getMessage());
                } catch (Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println("出现错误：" + e.getMessage());
                }
            }
        }, 0, 10000);
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
            Toast.makeText(getApplicationContext(), "未开启定位权限" + se.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "其他错误" + e.getMessage(), Toast.LENGTH_LONG).show();
//            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    Timer mReceiveReportTimer = new Timer();
    Handler mReceiveReportHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("对方已接收你的请求")
                    .setMessage("是否需要规划路线？")
                    .setPositiveButton("是", null)
                    .setNegativeButton("否", null)
                    .show();
        }
    };
}
