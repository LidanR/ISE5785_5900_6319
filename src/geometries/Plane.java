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
    /// The point on the plane
    private final Point point;
    /// The normal vector to the plane
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
     * Finds the intersections of the given ray with the plane.
     *
     * The method calculates the intersection point of a ray with the plane using
     * the parametric equation of the ray and the plane equation. It checks for
     * special cases such as:
     * - The ray starting on the plane's reference point.
     * - The ray being parallel to the plane.
     * - The intersection point being behind the ray's origin.
     *
     * @param ray the ray to intersect with the plane
     * @return a list containing the intersection point, or null if there are no intersections
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        if(point.equals(ray.getHead())) return null;
        double nv = normal.dotProduct(ray.getDir());
        if(isZero(nv)) return null;
        double t = alignZero(normal.dotProduct(point.subtract(ray.getHead())) / nv);
        if (t <= 0) return null;
        return List.of(ray.getPoint(t));
    }
}