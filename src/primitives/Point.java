package primitives;

    /**
     * The Point class represents a point in 3D space.
     * It is defined by three coordinates (x, y, z).
     */
    public class Point {
        /// The zero point in 3D space
        public static final Point ZERO = new Point(0, 0, 0);
        /// The coordinates of the point
        final protected Double3 xyz;

        /**
         * Constructs a Point with the specified coordinates.
         *
         * @param x the x-coordinate of the point
         * @param y the y-coordinate of the point
         * @param z the z-coordinate of the point
         */
        public Point(double x, double y, double z) {
            xyz = new Double3(x, y, z);
        }

        /**
         * Constructs a Point with the specified Double3 object.
         *
         * @param xyz the Double3 object representing the coordinates of the point
         */
        public Point(Double3 xyz) {
            this.xyz = xyz;
        }

        /**
         * Checks if this point is equal to another object.
         *
         * @param obj the object to compare with
         * @return true if the object is a Point with the same coordinates, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return (obj instanceof Point other) && xyz.equals(other.xyz);
        }

        /**
         * Returns a string representation of the point.
         *
         * @return a string representation of the point
         */
        @Override
        public String toString() { return xyz.toString(); }

        /**
         * Adds a vector to this point and returns the resulting point.
         *
         * @param v the vector to add
         * @return the resulting point after adding the vector
         */
        public Point add(Vector v) {
            return new Point(xyz.add(v.xyz));
        }

        /**
         * Subtracts another point from this point and returns the resulting vector.
         *
         * @param p the point to subtract
         * @return the resulting vector after subtracting the point
         */
        public Vector subtract(Point p) {
            return new Vector(xyz.subtract(p.xyz));
        }

        /**
         * Calculates the squared distance between this point and another point.
         *
         * @param p the point to calculate the distance to
         * @return the squared distance between the two points
         */
        public double distanceSquared(Point p) {
            double dx = xyz.d1() - p.xyz.d1();
            double dy = xyz.d2() - p.xyz.d2();
            double dz = xyz.d3() - p.xyz.d3();
            return dx * dx + dy * dy + dz * dz;
        }

        /**
         * Calculates the distance between this point and another point.
         *
         * @param p the point to calculate the distance to
         * @return the distance between the two points
         */
        public double distance(Point p) {
            return Math.sqrt(distanceSquared(p));
        }
        /**
         * Gets the x-coordinate of the point.
         */
        public double getX() {
            return xyz.d1();
        }
        /**
         * Gets the y-coordinate of the point.
         */
        public double getY() {
            return xyz.d2();
        }
        /**
         * Gets the z-coordinate of the point.
         */
        public double getZ() {
            return xyz.d3();
        }

        /**
         * Gets a coordinate value by index (0=X, 1=Y, 2=Z)
         * @param axis Index of the coordinate (0, 1, or 2)
         * @return The coordinate value
         * @throws IllegalArgumentException if axis is not 0, 1, or 2
         */
        public double get(int axis) {
            return switch (axis) {
                case 0 -> xyz.d1(); // X coordinate
                case 1 -> xyz.d2(); // Y coordinate
                case 2 -> xyz.d3(); // Z coordinate
                default -> throw new IllegalArgumentException("Invalid coordinate index: " + axis);
            };
        }

    }