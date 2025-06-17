package geometries;

import acceleration.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * The Tube class represents a tube in 3D space.
 * It extends the RadialGeometry class and is defined by a radius and an axis ray.
 */
public class Tube extends RadialGeometry {
    /// The axis ray of the tube
    protected final Ray axis;

    /**
     * Constructs a Tube with the specified radius and axis ray.
     *
     * @param radius the radius of the tube
     * @param axis the axis ray of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    /**
     * Get the normal vector at a given point on the tube.
     * @param point the point on the geometry
     * @return the normal vector at the given point
     */
    @Override
    public Vector getNormal(Point point) {
        //calculate the projection of the point on the axis
        double t = alignZero(this.axis.getDirection().dotProduct(point.subtract(this.axis.getPoint(0d))));

        //find center of the tube
        //return the normalized vector from the center of the tube to the point
        return point.subtract(this.axis.getPoint(t)).normalize();
    }

    /**
     * Calculates the intersections of a ray with the tube.
     *
     * @param ray the ray to check for intersections
     * @param maxDistance the maximum distance to consider for intersections
     * @return a list of intersection points, or null if there are no intersections
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {

        Vector vAxis = axis.getDirection();
        Vector v = ray.getDirection();
        Vector deltaP;

        try {
            deltaP = ray.getPoint(0).subtract(axis.getPoint(0));
        } catch (IllegalArgumentException e) {
            deltaP = null;
        }

        double a, b, c;

        a = v.dotProduct(v) - Math.pow(v.dotProduct(vAxis), 2);

        if (deltaP == null) {
            // Special case: deltaP is zero, meaning the ray starts on the axis of the tube
            b = 0;
            c = -radius * radius;
        } else {
            b = 2 * (v.dotProduct(deltaP) - (v.dotProduct(vAxis) * deltaP.dotProduct(vAxis)));
            c = deltaP.dotProduct(deltaP) - Math.pow(deltaP.dotProduct(vAxis), 2) - radius * radius;
        }

        double discriminant = alignZero(b * b - 4 * a * c);

        if (discriminant <= 0) {
            return null;
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);

        List<Point> intersections = new LinkedList<>();
        // Two intersection points
        double t1 = alignZero((-b - sqrtDiscriminant) / (2d * a));
        double t2 = alignZero((-b + sqrtDiscriminant) / (2d * a));

        if (t1 > 0d && alignZero(t1 - maxDistance) <= 0d) {
            intersections.add(ray.getPoint(t1));
        }

        if (t2 > 0d && alignZero(t2 - maxDistance) <= 0d) {
            intersections.add(ray.getPoint(t2));
        }
        return intersections.isEmpty() ? null : intersections.stream().map(p -> new Intersection(this, p)).toList();
    }

    /**
     * returns the AABB of the tube.
     * @return null, as the tube is infinite in length and has no bounding box
     */
    @Override
    public AABB getAABB() {
        return null; // infinite plane has no bounding box
    }
}