package geometries;

import primitives.Vector;
import primitives.Point;

public class Triangle extends Polygon{
    public Triangle(Point x, Point y, Point z) {
        super(x, y, z);
    }

    public Vector getNormal(Point point) {
        return null;
    }
}
