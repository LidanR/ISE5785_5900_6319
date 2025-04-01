package geometries;

import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * Interface for geometric objects that can be intersected by rays.
 */
public interface Intersectable {
    /**
     * Finds all intersection points between a given ray and the geometric object.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    List<Point> findIntersections(Ray ray);
}
