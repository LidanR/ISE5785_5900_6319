package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * The `Cylinder` class represents a cylinder in 3D space.
 * It extends the `Tube` class and includes an additional height attribute.
 */
public class Cylinder extends Tube {
    private final double height;

    /**
     * Constructs a new `Cylinder` with the specified radius, axis ray, and height.
     *
     * @param radius the radius of the cylinder
     * @param vector the axis ray of the cylinder
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, Ray vector, double height) {
        super(radius, vector);
        if(height <= 0) throw new IllegalArgumentException("Height must be positive");
        this.height = height;
    }

    /**
     * Returns the normal vector to the cylinder at a given point.
     *
     * @param point the point on the cylinder
     * @return the normal vector to the cylinder at the given point
     */
    public Vector getNormal(Point point) {
        return null;
    }
}