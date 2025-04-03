package geometries;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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
        Point vertex0 = vertices.get(0);
        Point vertex1 = vertices.get(1);
        Point vertex2 = vertices.get(2);
        Vector edge1 = vertex1.subtract(vertex0);
        Vector edge2 = vertex2.subtract(vertex0);
        Vector h = ray.getDir().crossProduct(edge2);
        Vector s = ray.getHead().subtract(vertex0);
        Vector q = s.crossProduct(edge1);
        double a, f, u, v;
        a = alignZero(edge1.dotProduct(h));

        if (isZero(a)) {
            return null;    // This ray is parallel to this triangle.
        }

        f = 1.0 / a;
        u = f * (s.dotProduct(h));

        if (u <= 0.0 || u >= 1.0) {
            return null;
        }

        v = f * ray.getDir().dotProduct(q);

        if (v <= 0.0 || u + v >= 1.0) {
            return null;
        }

        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * edge2.dotProduct(q);
        if (!isZero(t) && t > 0) // ray intersection
        {
            return plane.findIntersections(ray);
        }
        else // This means that there is a line intersection but not a ray intersection.
        {
            return null;
        }
    }

}