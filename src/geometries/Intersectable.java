package geometries;

import lighting.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Interface for geometric objects that can be intersected by rays.
 */
public abstract class Intersectable {
    /**
     * Represents an intersection between a ray and a geometric object.
     */
    public static class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;
        public Vector normal;
        public Vector v;
        public double vNormal;
        public LightSource light;
        public Vector l;
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
     *
     * find intersectionshelper method to find all intersection points between a given ray and the geometric object.
     */
    protected abstract List<Intersection> findIntersectionsHelper(Ray ray);
    /**
     * Calculates the intersections between a ray and the geometric object.
     *
     * @param ray the ray to intersect with the object
     * @return a list of intersection points
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return findIntersectionsHelper(ray);
    }

}
