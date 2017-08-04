package com.example.huyigong.route_nightrun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.example.huyigong.route_nightrun.Substances.Gym;
import com.example.huyigong.route_nightrun.Substances.GymsInfo;
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
    MapView mMapView;
    ArcGISMap mArcGISMap;
    FeatureCollectionTable mGymFeatureTable;
    GraphicsOverlay mRouteOverlay;

    Handler mShowGymHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_run);
        // 设置位置管理器
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        FeatureCollectionLayer featureCollectionLayer = new FeatureCollectionLayer(gymFeatureCollection);
        mArcGISMap.getOperationalLayers().add(featureCollectionLayer);
        mMapView.setMap(mArcGISMap);
        mRouteOverlay = new GraphicsOverlay();
        mRouteOverlay.setRenderer(createRouteRenderer());
        mMapView.getGraphicsOverlays().add(mRouteOverlay);
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
                mGymFeatureTable.addFeaturesAsync(features);
            }
        };
        Button gym_btn = (Button) findViewById(R.id.gym);
        if (gym_btn != null) {
            gym_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
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
                    }).start();
                }
            });
        }
        // 根据Intent，显示路径导航
        Intent intent = getIntent();
        Parcelable parcelable = intent.getParcelableExtra("Gym");
        if (parcelable != null && parcelable instanceof Gym) {
            final Gym gym = (Gym) parcelable;
            final Location curLocation = getLocation();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 获取Token
                        String tokenUrlString = "https://hu-yigong.maps.arcgis.com/sharing/oauth2/token" +
                                "?client_id=" + getString(R.string.ClientID) +
                                "&client_secret=" + getString(R.string.ClientSecret) +
                                "&grant_type=client_credentials";
                        URL tokenURL = new URL(tokenUrlString);
                        HttpURLConnection tokenConnection = (HttpURLConnection) tokenURL.openConnection();
                        Gson gson = new Gson();
                        ArcGISToken token = gson.fromJson(new InputStreamReader(tokenConnection.getInputStream()), ArcGISToken.class);
//                         查询路线
                        String routeUrlString = "https://utility.arcgis.com/usrsvcs/appservices/7lLQlGpstsTR8hpm/rest/services/World/Route/NAServer/Route_World/solve" +
                                "?token=" + token.access_token +
                                "&f=json" +
                                "&stops=" + 114.3547 + "," + 30.5303 + ";" + gym.getGymLng() + "," + gym.getGymLat();
                        URL routeURL = new URL(routeUrlString);
                        HttpURLConnection routeConnection = (HttpURLConnection) routeURL.openConnection();
                        BufferedReader br = new BufferedReader(new InputStreamReader(routeConnection.getInputStream()));
                        String jsonString = br.readLine();
                        JSONTokener jsonTokener = new JSONTokener(jsonString);
                        JSONObject root = (JSONObject) jsonTokener.nextValue();
                        JSONObject routeJsonObj = root.getJSONObject("routes");
                        showRoutes(routeJsonObj);


//                        String routeService = "https://utility.arcgis.com/usrsvcs/appservices/7lLQlGpstsTR8hpm/rest/services/World/Route/NAServer/Route_World/";
//                        final RouteTask routeTask = new RouteTask(getApplicationContext(), routeService);
//                        routeTask.loadAsync();
//                        routeTask.addDoneLoadingListener(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (routeTask.getLoadError() == null && routeTask.getLoadStatus() == LoadStatus.LOADED) {
//                                    try {
//                                        RouteParameters parameters = routeTask.createDefaultParametersAsync().get();
//                                        ArrayList<Stop> stops = new ArrayList<Stop>();
//                                        stops.add(new Stop(new Point(114.3547, 30.5303)));
//                                        stops.add(new Stop(new Point(gym.getGymLng(), gym.getGymLat())));
//                                        parameters.setStops(stops);
//                                        parameters.setReturnDirections(false);
////                                        RouteResult result = routeTask.solveRouteAsync(parameters).get();
//                                        final ListenableFuture<RouteResult> resultListenableFuture = routeTask.solveRouteAsync(parameters);
//                                        resultListenableFuture.addDoneListener(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    RouteResult result = resultListenableFuture.get();
//                                                    System.out.println();
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
                        System.out.println();
                    } catch (Exception e) {
                        Log.i("获取导航路径失败", e.getMessage());
                    }
                }
            }).start();
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
        PictureMarkerSymbol symbol = new PictureMarkerSymbol(bitmap);
        symbol.setHeight(30);
        symbol.setWidth(30);
        return new SimpleRenderer(symbol);
    }

    /**
     * 创建路径渲染器
     * @return 渲染器
     */
    private Renderer createRouteRenderer() {
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5);
        return new SimpleRenderer(lineSymbol);
    }

    LocationManager mLocationManager;

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
     */
    void showRoutes(JSONObject routes) {
        try {
            JSONArray featuresJSON = routes.getJSONArray("features");
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
                    mRouteOverlay.getGraphics().add(new Graphic(polyline));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class ArcGISToken {
    String access_token;
    int expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
