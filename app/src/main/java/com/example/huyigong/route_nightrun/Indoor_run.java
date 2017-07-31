package com.example.huyigong.route_nightrun;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
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
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.popup.Popup;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.Iterator;


public class Indoor_run extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_run);
       final MapView mMapView = (MapView) findViewById(R.id.mapView);
        final ArcGISMap map = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP,30.5303,114.3547, 14);
        mMapView.setMap(map);
        Button gym_info = (Button)findViewById(R.id.info);
        gym_info.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent togym = new Intent(Indoor_run.this,GymInfo.class);
                startActivity(togym);
            }

        });

        Button gym_btn = (Button)findViewById(R.id.gym);

        gym_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.BLUE, 12);

               final Point gymPt1 = new Point( 114.35718503343882, 30.52625513686283 , SpatialReferences.getWgs84());
                final Point gymPt2 = new Point( 114.35044813881815, 30.52882666083795 , SpatialReferences.getWgs84());
                final Point gymPt3 = new Point( 114.34883228396752, 30.52906821858169 , SpatialReferences.getWgs84());
                Point gymPt4 = new Point( 114.34876734522572, 30.529378225631543, SpatialReferences.getWgs84());
                Point gymPt5 = new Point( 114.3486345445385,  30.529166513124604, SpatialReferences.getWgs84());
                Point gymPt6 = new Point( 114.34879840864122, 30.52816656472647 , SpatialReferences.getWgs84());
                Point gymPt7 = new Point( 114.34875546181757, 30.528226616234285, SpatialReferences.getWgs84());
                Point gymPt8 = new Point( 114.3497052895769,  30.52696546362511 , SpatialReferences.getWgs84());
                Point gymPt9 = new Point( 114.34851945528689, 30.531990788512932, SpatialReferences.getWgs84());
                Point gymPt10 = new Point(114.34844154623579, 30.53216885950179 , SpatialReferences.getWgs84());
                Point gymPt11 = new Point(114.34432448934331, 30.530594349028082, SpatialReferences.getWgs84());
                Point gymPt12= new Point( 114.34353263236011, 30.53072268427925 , SpatialReferences.getWgs84());
                Point gymPt13 = new Point(114.36781690060872, 30.53476354086254 , SpatialReferences.getWgs84());
                Point gymPt14 = new Point(114.36918816438971, 30.53109320776469 , SpatialReferences.getWgs84());
                Point gymPt15 = new Point(114.36986064754089, 30.531368390793016, SpatialReferences.getWgs84());
                
                BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(Indoor_run.this, R.drawable.gymicon);
                final PictureMarkerSymbol campsiteSymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
                campsiteSymbol.setHeight(20);

                campsiteSymbol.setWidth(20);



                final Graphic graphic1 = new Graphic(gymPt1, campsiteSymbol);
                Graphic graphic2 = new Graphic(gymPt2, campsiteSymbol);
                Graphic graphic3 = new Graphic(gymPt3, campsiteSymbol);
                Graphic graphic4 = new Graphic(gymPt4, campsiteSymbol);
                Graphic graphic5 = new Graphic(gymPt5, campsiteSymbol);
                Graphic graphic6 = new Graphic(gymPt6, campsiteSymbol);
                Graphic graphic7 = new Graphic(gymPt7, campsiteSymbol);
                Graphic graphic8 = new Graphic(gymPt8, campsiteSymbol);
                Graphic graphic9 = new Graphic(gymPt9, campsiteSymbol);
                Graphic graphic10 = new Graphic(gymPt10, campsiteSymbol);
                Graphic graphic11= new Graphic(gymPt11, campsiteSymbol);
                Graphic graphic12= new Graphic(gymPt12, campsiteSymbol);
                Graphic graphic13= new Graphic(gymPt13, campsiteSymbol);
                Graphic graphic14= new Graphic(gymPt14, campsiteSymbol);
                Graphic graphic15= new Graphic(gymPt15, campsiteSymbol);


                final GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

                graphicsOverlay.getGraphics().add(graphic1);
                graphicsOverlay.getGraphics().add(graphic2);
                graphicsOverlay.getGraphics().add(graphic3);
                graphicsOverlay.getGraphics().add(graphic4);
                graphicsOverlay.getGraphics().add(graphic5);
                graphicsOverlay.getGraphics().add(graphic6);
                graphicsOverlay.getGraphics().add(graphic7);
                graphicsOverlay.getGraphics().add(graphic8);
                graphicsOverlay.getGraphics().add(graphic9);
                graphicsOverlay.getGraphics().add(graphic10);
                graphicsOverlay.getGraphics().add(graphic11);
                graphicsOverlay.getGraphics().add(graphic12);
                graphicsOverlay.getGraphics().add(graphic13);
                graphicsOverlay.getGraphics().add(graphic14);
                graphicsOverlay.getGraphics().add(graphic15);
                mMapView.getGraphicsOverlays().add(graphicsOverlay);
                mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(Indoor_run.this,mMapView){
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        android.graphics.Point clickPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                        final ListenableFuture<IdentifyGraphicsOverlayResult> future = mMapView.identifyGraphicsOverlayAsync(graphicsOverlay, clickPoint, 10.0, false, 2);
                        future.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    IdentifyGraphicsOverlayResult result = future.get();
                                    Iterator<Graphic> iterator = result.getGraphics().iterator();
                                    Graphic graphic;
                                    while (iterator.hasNext()) {
                                        graphic = iterator.next();
                                        Callout callout = mMapView.getCallout();
                                        callout.setLocation((Point) graphic.getGeometry());
                                        View calloutView ;

                                       // callout.setContent(calloutView);
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

            }
        });
    }
}
