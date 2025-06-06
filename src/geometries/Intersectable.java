package geometries;

import lighting.LightSource;
import primitives.*;

import java.util.List;

/**
 * Interface for geometric objects that can be intersected by rays.
 */
public abstract class Intersectable {
    /**
     * Represents an intersection between a ray and a geometric object.
     */
    public static class Intersection {
        /// The geometric object that was intersected
        public final Geometry geometry;
        /// The point of intersection
        public final Point point;
        /// The material of the intersected geometry
        public final Material material;
        /// The normal vector at the intersection point
        public Vector normal;
        /// The direction vector of the ray
        public Vector v;
        /// The distance from the ray's head to the intersection point
        public double vNormal;
        /// The light source at the intersection point
        public LightSource light;
        /// The direction vector from the light source to the intersection point
        public Vector l;
        /// The dot product of the normal vector and the light direction vector
        public double lNormal;

        /**
         * Constructor for Intersection.
         *
         * @param geometry the geometry that was intersected
         * @param point    the intersection point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material =geometry!=null ? geometry.getMaterial(): null;
        }
        /**
         * equals method to compare two Intersection objects.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            return ((Intersection) obj).geometry==this.geometry &&
                    ((Intersection) obj).point.equals(this.point);
        }
        /**
         * toString method to represent the Intersection object as a string.
         */
        @Override
        public String toString() {
            return "Intersection{" +
                    "geometry=" + geometry +
                    ", point=" + point +
                    '}';
        }
    }

    /**
     * Finds all intersection points between a given ray and the geometric object.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    public final List<Point> findIntersections(Ray ray){
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }
    /**
     * Finds all intersection points between a given ray and the geometric object within a specified distance.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    public final List<Point> findIntersections(Ray ray,double maxDistance){
        if(Util.isZero(maxDistance)) return null;
        var list = calculateIntersections(ray,maxDistance);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }
    /**
     *
     * find intersectionshelper method to find all intersection points between a given ray and the geometric object.
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance);
    /**
     * Calculates the intersections between a ray and the geometric object.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray,Double.POSITIVE_INFINITY);
    }
    /**
     * Calculates the intersections between a ray and the geometric object within a specified distance.
     *
     * @param ray         the ray to intersect with the object
     * @param maxDistance the maximum distance for the intersection
     * @return a list of intersection points
     */
    public final List<Intersection> calculateIntersections(Ray ray, double maxDistance) {
        return calculateIntersectionsHelper(ray, maxDistance);
    }

}
