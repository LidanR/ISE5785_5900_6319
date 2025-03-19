package primitives;

    /**
     * The Ray class represents a ray in 3D space.
     * It is defined by a starting point (head) and a direction vector.
     */
    public class Ray {
        private final Point head;
        private final Vector direction;

        /**
         * Constructs a Ray with the specified starting point and direction vector.
         *
         * @param p the starting point of the ray
         * @param v the direction vector of the ray
         */
        public Ray(Point p, Vector v) {
            this.head = p;
            this.direction = v.normalize();
        }

        /**
         * Returns a string representation of the ray.
         *
         * @return a string representation of the ray
         */
        @Override
        public String toString() {
            return "head: " + head + ", direction: " + direction;
        }

        /**
         * Checks if this ray is equal to another object.
         *
         * @param obj the object to compare with
         * @return true if the object is a Ray with the same head and direction, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return (obj instanceof Ray other) && head.equals(other.head) && direction.equals(other.direction);
        }
    }