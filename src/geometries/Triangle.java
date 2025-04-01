package geometries;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;

import java.util.List;

/**
 * The Triangle class represents a triangle in 3D space.
 * It extends the Polygon class and is defined by three points.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a Triangle with the specified vertices.
     *
     * @param x the first vertex of the triangle
     * @param y the second vertex of the triangle
     * @param z the third vertex of the triangle
     */
    public Triangle(Point x, Point y, Point z) {
        super(x, y, z);
    }

    /**
     * Returns the normal vector to the triangle at a given point.
     * Currently, this method returns null.
     *
     * @param point a point on the triangle
     * @return the normal vector to the triangle at the given point
     */
    public Vector getNormal(Point point) {
        Vector vector1 = vertices.get(1).subtract(vertices.get(0));
        Vector vector2 = vertices.get(2).subtract(vertices.get(0));
        return vector1.crossProduct(vector2).normalize();
    }
    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector v1 = vertices.get(0).subtract(ray.getHead());
        Vector v2 = vertices.get(1).subtract(ray.getHead());
        Vector v3 = vertices.get(2).subtract(ray.getHead());

        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        Vector rayDir = ray.getDir();

        boolean posSide = (rayDir.dotProduct(n1) > 0) && (rayDir.dotProduct(n2) > 0) && (rayDir.dotProduct(n3) > 0);
        boolean negSide = (rayDir.dotProduct(n1) < 0) && (rayDir.dotProduct(n2) < 0) && (rayDir.dotProduct(n3) < 0);

        if (!posSide && !negSide) {
            return null;
        }

        List<Point> planeIntersections = this.plane.findIntersections(ray);

        if (planeIntersections == null || planeIntersections.isEmpty()) {
            return null;
        }

        return List.of(planeIntersections.get(0));
    }

}