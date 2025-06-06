package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import static primitives.Util.*;

/**
 * The `Sphere` class represents a sphere in 3D space.
 * It extends the `RadialGeometry` class and includes the center point of the sphere.
 */
public class Sphere extends RadialGeometry {
    /// The center point of the sphere
    private final Point center;

    /**
     * Constructs a new `Sphere` with the specified center point and radius.
     *
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    /**
     * Returns the normal vector to the sphere at a given point.
     * This method calculates the normal vector by subtracting the center point from the given point and normalizing the result.
     *
     * @param point the point on the sphere
     * @return the normal vector to the sphere at the given point
     */
    public Vector getNormal(Point point) {
        return point.subtract(center).normalize();
    }


    /**
     * Finds the intersection points of the sphere with a given ray.
     * The method calculates the intersection points by solving the quadratic equation
     * derived from the sphere equation and the ray equation.
     *
     * @param ray the ray to intersect with the sphere
     * @return a list of intersection points, or null if there are no intersections
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        if(ray.getHead().equals(center)) return List.of(new Intersection(this,ray.getHead().add(ray.getDirection().scale(super.getRadius())))) ;
        Vector u = this.center.subtract(ray.getHead());
        double tm = ray.getDirection().dotProduct(u);
        double d = alignZero(Math.sqrt(u.lengthSquared() - tm * tm));
        if (isZero(d - super.getRadius()) || d > super.getRadius()) return null;
        double th = alignZero(Math.sqrt(super.getRadius() * super.getRadius() - d * d));
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);
        if (t1 > 0 && t2 > 0) {
            if (t1 <= maxDistance && t2 <= maxDistance)
                return List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2)));
            if (t1 <= maxDistance)
                return List.of(new Intersection(this, ray.getPoint(t1)));
            if (t2 <= maxDistance)
                return List.of(new Intersection(this, ray.getPoint(t2)));
            return null;
        }
        if (t1 > 0 && t1 <= maxDistance)
            return List.of(new Intersection(this, ray.getPoint(t1)));
        if (t2 > 0 && t2 <= maxDistance)
            return List.of(new Intersection(this, ray.getPoint(t2)));
        return null;
    }

}