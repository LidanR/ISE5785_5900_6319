package geometries;

    import primitives.Vector;
    import primitives.Point;

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
    }