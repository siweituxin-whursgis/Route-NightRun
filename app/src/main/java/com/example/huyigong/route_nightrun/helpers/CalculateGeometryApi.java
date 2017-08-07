package com.example.huyigong.route_nightrun.helpers;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;

import java.util.ArrayList;

/**
 * Created by CS_Tao on 2017/8/6.
 */

public class CalculateGeometryApi
{
    public static Point GetFootPoint(Point lineNode1, Point lineNode2, Point singlePoint)
    {
        double x = 0;
        double y = 0;
        double x1 = lineNode1.getX();
        double y1 = lineNode1.getY();
        double x2 = lineNode2.getX();
        double y2 = lineNode2.getY();
        double x3 = singlePoint.getX();
        double y3 = singlePoint.getY();
        double k1 = Double.MAX_VALUE;
        double k2 = Double.MAX_VALUE;

        if (Math.abs(x1 - x2) < 0.000001 && Math.abs(y1 - y2) < 0.000001)
        {
            return null;
        }
        else if (Math.abs(x1 - x2) < 0.000001)
        {
            x = x1;
            y = singlePoint.getY();
        }
        else if (Math.abs(y1 - y2) < 0.000001)
        {
            x = singlePoint.getX();
            y = y1;
        }
        else
        {
            k1 = (y2 - y1) / (x2 - x1);
            k2 = -1 / k1;

            x = (k1 * x1 - k2 * x3 - y1 + y3) / (k1 - k2);
            y = k1 * (x - x1) + y1;

            double d1 = Math.sqrt((x - x1)*(x - x1) + (y - y1)*(y - y1));
            double d2 = Math.sqrt((x - x2)*(x - x2) + (y - y2)*(y - y2));
            double d = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));

            if(Math.abs(d1 + d2 - d) > 5)
            {
                if(d1 > d2)
                {
                    x = x2;
                    y = y2;
                }
                else
                {
                    x = x1;
                    y = y1;
                }
            }
        }

        return new Point(x, y, SpatialReference.create(3857));
    }

    public static Point GetNearestPointInPolyLine(ArrayList<Point> polyline, Point singlePoint)
    {
        ArrayList<Point> polylineMkt = new ArrayList<Point>();
        for (Point tempPoint : polyline)
            polylineMkt.add((Point) GeometryEngine.project(tempPoint, SpatialReference.create(3857)));
        ArrayList<Point> footPointList = new ArrayList<Point>();
        ArrayList<Double> distancesOfLines = new ArrayList<Double>();
        Point p = (Point) GeometryEngine.project(singlePoint, SpatialReference.create(3857));

        int pointCount = polylineMkt.size();
        for(int i = 1;i < pointCount; i++)
        {
            Point p1 = polylineMkt.get(i - 1);
            Point p2 = polylineMkt.get(i);
            Point footPoint = GetFootPoint(p1, p2, p);
            if(footPoint == null)
                continue;
            Double distance = Math.sqrt((p.getX() - footPoint.getX())*(p.getX() - footPoint.getX()) + (p.getY() - footPoint.getY())*(p.getY() - footPoint.getY()));
            footPointList.add(footPoint);
            distancesOfLines.add(distance);
        }

        if(footPointList.size() != 0)
        {
            double minDistance = distancesOfLines.get(0);
            Point nearestPoint = footPointList.get(0);
            for(int i=1;i<footPointList.size();i++)
            {
                if(distancesOfLines.get(i) < minDistance)
                {
                    minDistance = distancesOfLines.get(i);
                    nearestPoint = footPointList.get(i);
                }
            }
            return nearestPoint;
        }
        else
        {
            return null;
        }
    }
}
