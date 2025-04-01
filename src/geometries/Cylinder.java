package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * The `Cylinder` class represents a finite cylinder in 3D space.
 * It extends the `Tube` class by adding a height attribute.
 */
public class Cylinder extends Tube {
    private final double height;

    /**
     * Constructs a new `Cylinder` with the specified radius, axis ray, and height.
     *
     * @param radius the radius of the cylinder
     * @param vector the axis ray of the cylinder
     * @param height the height of the cylinder
     * @throws IllegalArgumentException if the height is not positive
     */
    public Cylinder(double radius, Ray vector, double height) {
        super(radius, vector);
        if (height <= 0) throw new IllegalArgumentException("Height must be positive");
        this.height = height;
    }

    /**
     * Returns the normal vector to the cylinder at a given point.
     *
     * @param point the point on the cylinder
     * @return the normal vector to the cylinder at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        // The cylinder's axis
        Ray axisRay = super.axis;

        if(axisRay.getPoint().equals(point)) return axisRay.getDir().scale(-1);

        Point p0 = axisRay.getPoint(); // The starting point of the axis
        Vector dir = axisRay.getDir(); // The direction of the axis

        // Project the given point onto the cylinder's axis
        double t = point.subtract(p0).dotProduct(dir);
        Point o = p0.add(dir.scale(t)); // The projection of the point onto the axis

        // Check if the point is on the bottom base
        if (t <= 0) return dir.scale(-1);
        // Check if the point is on the top base
        if (t >= height) return dir;

        // If the point is on the lateral surface, return the normal to the surface
        return point.subtract(o).normalize();
    }
    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}
