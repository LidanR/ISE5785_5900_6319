package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * The `Geometry` class is an abstract base class for all geometric objects.
 * It provides a method to get the normal vector to the geometry at a given point.
 */
public abstract class Geometry {

    /**
     * Returns the normal vector to the geometry at a given point.
     * This method should be implemented by subclasses to provide the specific normal vector calculation.
     *
     * @param point the point on the geometry
     * @return the normal vector to the geometry at the given point
     */
    Vector getNormal(Point point) { return null; }
}