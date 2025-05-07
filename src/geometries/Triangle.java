package geometries;

import primitives.Ray;
import primitives.Vector;
import primitives.Point;

import java.util.List;
import java.util.Objects;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * The Triangle class represents a triangle in 3D space.
 * It extends the Polygon class and is defined by three points.
 */
public class Triangle extends Polygon {
    // The edges of the triangle
    private final Vector edge1;
    // The edges of the triangle
    private final Vector edge2;
    /**
     * Constructs a Triangle with the specified vertices.
     *
     * @param x the first vertex of the triangle
     * @param y the second vertex of the triangle
     * @param z the third vertex of the triangle
     */
    public Triangle(Point x, Point y, Point z) {
        super(x, y, z);
        this.edge1 = y.subtract(x);
        this.edge2 = z.subtract(x);
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
    /**
     * Finds the intersection points between the triangle and a given ray.
     * The method uses the Möller-Trumbore intersection algorithm to determine
     * if the ray intersects the triangle and calculates the intersection points.
     *
     * @param ray the ray to intersect with the triangle
     * @return a list of intersection points between the ray and the triangle,
     *         or null if there are no intersections
     */

    @Override
    protected List<Intersection> findIntersectionsHelper(Ray ray) {

        Vector h = ray.getDir().crossProduct(this.edge2);
        Vector s = ray.getHead().subtract(vertices.getFirst());
        Vector q = s.crossProduct(this.edge1);
        double a, f, u, v;
        a = alignZero(this.edge1.dotProduct(h));

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
            // convert the plane points to intersection points
            return Objects.requireNonNull(plane.findIntersections(ray)).stream()
                    .map(p -> new Intersection(this,p))
                    .toList();
        }
        else // This means that there is a line intersection but not a ray intersection.
        {
            return null;
        }
    }

}