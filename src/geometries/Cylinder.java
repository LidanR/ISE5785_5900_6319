package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * The `Cylinder` class represents a finite cylinder in 3D space.
 * It extends the `Tube` class by adding a height attribute.
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder
      */
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
        Point p0 = axis.getPoint(0d);
        Vector dir = this.axis.getDirection();

        //  If p0 is the head of the axis
        if (point.equals(p0))
            return dir.scale(-1);

        // If p1 is the end of the axis
        if (point.equals(axis.getPoint(height)))
            return dir;

        // If the point is on the top or bottom surface of the cylinder
        if (Util.isZero(p0.subtract(point).dotProduct(dir)))
            return dir.scale(-1d);

        if (Util.isZero(axis.getPoint(height).subtract(point).dotProduct(dir)))
            return dir;

        // Otherwise, call the superclass method
        return super.getNormal(point);
    }
    /**
     * Finds intersections of a ray with the cylinder.
     *
     * @param ray         the ray to find intersections with
     * @param maxDistance the maximum distance to find intersections
     * @return a list of intersection points, or null if no intersections found
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        // Initialize intersections list
        List<Point> intersections = new LinkedList<>();

        // Find intersections with the infinite cylinder
        Tube tube = new Tube(radius,axis);
        List<Point> infiniteCylinderIntersections = tube.findIntersections(ray);
        if (infiniteCylinderIntersections != null) {
            intersections.addAll(infiniteCylinderIntersections);
        }

        // Remove intersections outside the cylinder height
        Iterator<Point> iterator = intersections.iterator();
        while (iterator.hasNext()) {
            Point intersection = iterator.next();
            double t = axis.getDirection().dotProduct(intersection.subtract(axis.getPoint(0d)));
            if (t <= 0d || t >= height || alignZero(intersection.distanceSquared(ray.getPoint(0)) - maxDistance * maxDistance) > 0d) {
                iterator.remove();
            }
        }

        // Define planes for the bottom and top bases
        Plane bottomBase = new Plane(axis.getPoint(0d), axis.getDirection());
        Plane topBase = new Plane(axis.getPoint(height), axis.getDirection());

        // Return intersections if there are exactly 2 (so they are on the sides of the cylinder)
        if (intersections.size() == 2) {
            return List.of(new Intersection(this, intersections.get(0)), new Intersection(this, intersections.get(1)));
        }


        // Find intersections with the bottom base
        List<Point> bottomBaseIntersections = bottomBase.findIntersections(ray);
        if (bottomBaseIntersections != null && alignZero(bottomBaseIntersections.getFirst().distanceSquared(ray.getPoint(0)) - maxDistance) <= 0d) {
            Point intersection = bottomBaseIntersections.getFirst();
            if (axis.getPoint(0d).distanceSquared(intersection) <= radius * radius) {
                intersections.add(intersection);
            }
        }

        // Find intersections with the top base
        List<Point> topBaseIntersections = topBase.findIntersections(ray);
        if (topBaseIntersections != null && alignZero(topBaseIntersections.getFirst().distanceSquared(ray.getPoint(0)) - maxDistance) <= 0d) {
            Point intersection = topBaseIntersections.getFirst();
            if (axis.getPoint(height).distanceSquared(intersection) <= radius * radius) {
                intersections.add(intersection);
            }
        }

        // if the ray is tangent to the cylinder
        if (intersections.size() == 2 && axis.getPoint(0).distanceSquared(intersections.get(0)) == radius * radius &&
                axis.getPoint(height).distanceSquared(intersections.get(1)) == radius * radius) {
            Vector v = intersections.get(1).subtract(intersections.get(0));
            if (v.normalize().equals(axis.getDirection()) || v.normalize().equals(axis.getDirection().scale(-1d)))
                return null;
        }

        // Return null if no valid intersections found
        List<Intersection> geoPoints = new LinkedList<>();
        for (Point p : intersections) {
            geoPoints.add(new Intersection(this, p));
        }

        return geoPoints.isEmpty() ? null : geoPoints;
    }

}
