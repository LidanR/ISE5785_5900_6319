package geometries;

import primitives.Ray;

import java.util.LinkedList;
import java.util.List;
/**
 * The `Geometries` class represents a collection of geometries in 3D space.
 * It implements the `Intersectable` interface and includes a list of geometries.
 * This is a composite class that allows for the grouping of multiple geometries.
 */
public class Geometries extends Intersectable{
    /// The list of geometries in the collection
    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Constructs a new `Geometries` object.
     */
    public Geometries(){}
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
     * find intersectionshelper method to find all intersection points between a given ray and the geometric object.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    @Override
    protected List<Intersection> findIntersectionsHelper(Ray ray,double maxDistance) {
        List<Intersection> intersections = null;

        // Iterate over all geometries and collect intersection points
        for (Intersectable geometry : geometries) {
            List<Intersection> geoIntersections = geometry.findIntersectionsHelper(ray, maxDistance);
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
