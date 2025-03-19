package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class Tube extends RadialGeometry{
    protected final Ray axis;

    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    public Vector getNormal(Point point) {
        return null;
    }
}
