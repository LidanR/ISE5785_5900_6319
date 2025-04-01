package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import static primitives.Util.*;
/**
 * The `Plane` class represents a plane in 3D space.
 * It extends the `Geometry` class and includes a point on the plane and the normal vector to the plane.
 */
public class Plane extends Geometry {
    private final Point point;
    private final Vector normal;

    /**
     * Constructs a new `Plane` with three points on the plane.
     * The normal vector is calculated using the cross product of the vectors formed by these points.
     *
     * @param x the first point on the plane
     * @param y the second point on the plane
     * @param z the third point on the plane
     */
    public Plane(Point x, Point y, Point z) {
        this.point = x;
        this.normal = y.subtract(x).crossProduct(z.subtract(x)).normalize();
    }

    /**
     * Constructs a new `Plane` with a point on the plane and a normal vector.
     * The normal vector is normalized.
     *
     * @param q the point on the plane
     * @param normal the normal vector to the plane
     */
    public Plane(Point q, Vector normal) {
        this.point = q;
        this.normal = normal.normalize();
    }

    /**
     * Returns the normal vector to the plane.
     *
     * @return the normal vector to the plane
     */
    public Vector getNormal() {
        return normal;
    }

    /**
     * Returns the normal vector to the plane at a given point.
     * This method overrides the abstract method in the `Geometry` class.
     *
     * @param point the point on the plane
     * @return the normal vector to the plane at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        return normal;
    }

    /**
     * @param ray the ray to intersect with the object
     * @return
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        if(point.equals(ray.getPoint())) return null;
        double nv = normal.dotProduct(ray.getDir());
        if(isZero(nv)) return null;
        double t =alignZero(normal.dotProduct(point.subtract(ray.getPoint())) / nv);
        if (t <= 0) return null;
        return List.of(ray.getPoint().add(ray.getDir().scale(t)));
    }
}