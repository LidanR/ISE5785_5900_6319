package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;
/**
 * The `Geometries` class represents a collection of geometries in 3D space.
 * It implements the `Intersectable` interface and includes a list of geometries.
 */
public class Geometries implements Intersectable{
    private final List<Intersectable> geometries = new LinkedList<Intersectable>();

    /**
     * Constructs a new `Geometries` object.
     */
    public Geometries(){};
    /**
     * Constructs a new `Geometries` object with the specified geometries.
     *
     * @param geometries the geometries to add to the object
     */
    public Geometries(List<Intersectable> geometries){
        for (Intersectable intersectable : geometries){
            add(intersectable);
        }
    }
    /**
     * Adds a geometry to the object.
     *
     * @param geometry the geometries to add to the object
     */
    public void add(Intersectable geometry){
       geometries.addLast(geometry);
    }

    /**
     * Adds a list of geometries to the object.
     *
     * @param intersectables the list of geometries to add to the object
     */
    public void add(Intersectable... intersectables){
        for (Intersectable geometry : intersectables) {
            geometries.addLast(geometry);
        }
    }

    /**
     * The `Geometries` class represents a collection of geometries in 3D space.
     * It implements the `Intersectable` interface and provides methods to manage
     * and find intersections of the geometries.
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> intersections = null;

        // Iterate over all geometries and collect intersection points
        for (Intersectable geometry : geometries) {
            List<Point> geoIntersections = geometry.findIntersections(ray);
            if (geoIntersections != null) {
                if (intersections == null) {
                    intersections = new LinkedList<>();
                }
                intersections.addAll(geoIntersections);
            }
        }

        // Return null if no intersections were found
        return intersections;
    }
}
