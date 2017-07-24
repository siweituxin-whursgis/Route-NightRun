package com.example.huyigong.route_nightrun;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;



public class Indoor_run extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_run);
        MapView mMapView = (MapView) findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP,30.5303,114.3547, 16);
        mMapView.setMap(map);
        Button gym_btn = (Button)findViewById(R.id.gym);

        gym_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.BLUE, 12);
                Point gymPt1 = new Point(114.36271, 30.523924, SpatialReferences.getWgs84());
                Point gymPt2 = new Point(114.355965,30.526486, SpatialReferences.getWgs84());
                Point gymPt3 = new Point(114.354347,30.526725, SpatialReferences.getWgs84());
                Point gymPt4 = new Point(114.354282,30.527035, SpatialReferences.getWgs84());
                Point gymPt5 = new Point(114.354149,30.526823, SpatialReferences.getWgs84());
                Point gymPt6 = new Point(114.354313,30.525823, SpatialReferences.getWgs84());
                Point gymPt7 = new Point(114.35427,30.525883, SpatialReferences.getWgs84());
                Point gymPt8 = new Point(114.355221,30.524623, SpatialReferences.getWgs84());
                Point gymPt9 = new Point(114.354034,30.529648, SpatialReferences.getWgs84());
                Point gymPt10 = new Point(114.353956,30.529826, SpatialReferences.getWgs84());
                Point gymPt11 = new Point(114.349833,30.528244, SpatialReferences.getWgs84());
                Point gymPt12= new Point(114.34904,30.528371, SpatialReferences.getWgs84());
                Point gymPt13 = new Point(114.373353,30.532449, SpatialReferences.getWgs84());
                Point gymPt14 = new Point(114.374725,30.528779, SpatialReferences.getWgs84());
                Point gymPt15 = new Point(114.375398,30.529055, SpatialReferences.getWgs84());

                BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(Indoor_run.this, R.drawable.gymicon);
                final PictureMarkerSymbol campsiteSymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
                campsiteSymbol.setHeight(20);

                campsiteSymbol.setWidth(20);



                Graphic graphic1 = new Graphic(gymPt1, campsiteSymbol);
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

                GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
                MapView mMapView = (MapView) findViewById(R.id.mapView);
                ArcGISMap map = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP,30.5303,114.3547, 16);
                mMapView.setMap(map);
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

            }
        });
    }
}
