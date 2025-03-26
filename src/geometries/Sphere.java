package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * The `Sphere` class represents a sphere in 3D space.
 * It extends the `RadialGeometry` class and includes the center point of the sphere.
 */
public class Sphere extends RadialGeometry {
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
}