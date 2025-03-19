package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class Cylinder extends Tube {
    private final double height;

    public Cylinder(double radius, Ray vector, double height) {
        super(radius, vector);
        this.height = height;
    }

    public Vector getNormal(Point point) {
        return null;
    }
}
