package geometries;

import primitives.Point;
import primitives.Vector;

public class Plane  extends Geometry{
    private final Point point;
    private final Vector normal;

    public Plane(Point x, Point y, Point z) {
        this.point = x;
        this.normal = y.subtract(x).crossProduct(z.subtract(x)).normalize();
    }

    public Plane(Point q, Vector normal) {
        this.point = q;
        this.normal = normal.normalize();
    }

    public Vector getNormal(){
        return normal;
    }

    @Override
    public Vector getNormal(Point point) {
        return normal;
    }
}
