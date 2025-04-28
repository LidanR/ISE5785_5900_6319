package geometries;

    import primitives.Point;
    import primitives.Ray;
    import primitives.Vector;

    import java.util.List;

/**
     * The Tube class represents a tube in 3D space.
     * It extends the RadialGeometry class and is defined by a radius and an axis ray.
     */
    public class Tube extends RadialGeometry {
        /// The axis ray of the tube
        protected final Ray axis;

        /**
         * Constructs a Tube with the specified radius and axis ray.
         *
         * @param radius the radius of the tube
         * @param axis the axis ray of the tube
         */
        public Tube(double radius, Ray axis) {
            super(radius);
            this.axis = axis;
        }

        /**
         * Returns the normal vector to the tube at a given point.
         * Currently, this method returns null.
         *
         * @param point a point on the tube
         * @return the normal vector to the tube at the given point
         */
        public Vector getNormal(Point point) {
            double t=axis.getDir().dotProduct(point.subtract(axis.getHead()));

            Point O = axis.getPoint(t);
            return point.subtract(O).normalize();
        }

    /**
     * @param ray the ray to intersect with the object
     * @return
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}