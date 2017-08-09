package com.example.huyigong.route_nightrun.helpers;

import android.support.annotation.Nullable;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.example.huyigong.route_nightrun.RunningRunFragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by CS_Tao on 2017/8/7.
 */

public class RouteApi {
    @Nullable
    public static ArrayList<Point> GetShortestRoute(Point start, Point end, @Nullable RunningRunFragment.DoubleReferance distance)
    {
        ArrayList<Point> pointsArrayList = new ArrayList<Point>();
        try
        {
            start = (Point) GeometryEngine.project(start, SpatialReference.create(4326));
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
    public static ArrayList<Point> GetShortestRoute(ArrayList<Point> stopPoints, @Nullable RunningRunFragment.DoubleReferance distance)
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

    public static double GetDistanceOfShortestRoute(Point start, Point end)
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

    public static double GetDistanceOfTwoPoint(Point p1, Point p2)
    {
        Point p1Mkt = (Point)GeometryEngine.project(p1, SpatialReference.create(3857));
        Point p2Mkt = (Point)GeometryEngine.project(p2, SpatialReference.create(3857));
        return Math.sqrt((p1Mkt.getX() - p2Mkt.getX())*(p1Mkt.getX() - p2Mkt.getX()) + (p1Mkt.getY() - p2Mkt.getY())*(p1Mkt.getY() - p2Mkt.getY()));
    }
}
