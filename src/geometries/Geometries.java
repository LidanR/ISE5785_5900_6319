package geometries;

import acceleration.AABB;
import primitives.Ray;

import java.util.Collections;
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
     * Adds a list of geometries to the object.
     *
     * @param intersectables the list of geometries to add to the object
     */
    public void add(Intersectable... intersectables){
        Collections.addAll(geometries, intersectables);
    }

    /**
     * find intersectionshelper method to find all intersection points between a given ray and the geometric object.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> intersections = new LinkedList<>();

        // Iterate over all geometries and collect intersection points
        for (Intersectable geometry : geometries) {
            List<Intersection> intersectionsOfGeometry = geometry.calculateIntersectionsHelper(ray, maxDistance);
            if (intersectionsOfGeometry != null) {
                intersections.addAll(intersectionsOfGeometry);
            }
        }

        // Return null if no intersections were found
        return intersections.isEmpty() ? null : intersections;
    }

    /**
     * getter method to get the geometries list.
     * @return the list of geometries
     */
    public List<Intersectable> getGeometries() {
        return geometries;
    }

    /**
     * Returns the number of geometries in the collection.
     * @return the size of the geometries list
     */
    public int getGeomitriesSize() {
        return geometries.size();
    }

    @Override
    public AABB getAABB() {
        if (geometries.isEmpty()) {
            return null;
        }
        AABB box = geometries.get(0).getAABB();
        for (Intersectable geometry : geometries) {
            AABB geometryBox = geometry.getAABB();
            if (geometryBox != null) {
                box = box == null ? geometryBox : box.union(geometryBox);
            }
        }
        return box;
    }

}
