package primitives;

import java.util.List;

import static primitives.Util.isZero;

/**
     * The Ray class represents a ray in 3D space.
     * It is defined by a starting point (head) and a direction vector.
     */
    public class Ray {
        /// The starting point of the ray
        private final Point head;
        /// The direction vector of the ray
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
         * Returns the starting point of the ray.
         * @return the starting point of the ray
         */
        public Point getHead() {
            return head;
        }

        /**
         *
         * @return the direction vector of the ray
         */
        public Vector getDir() {
            return direction;
        }

        /**
         * Returns a point on the ray at a given distance from the starting point.
         *
         * @param t the distance from the starting point
         * @return the point on the ray at the given distance
         */
        public Point getPoint(double t) {
            if(isZero(t)) return head;
            return head.add(direction.scale(t));
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

        /**
         * Finds the closest point to the ray from a list of points.
         *
         * @param points the list of points to search
         * @return the closest point to the ray, or null if the list is empty
         */
        public Point findClosestPoint(List<Point> points) {
            if (points == null || points.isEmpty()) return null;
            Point closestPoint = points.get(0);
            double minDistance = head.distanceSquared(closestPoint);
            for (Point point : points) {
                double distance = head.distanceSquared(point);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = point;
                }
            }
            return closestPoint;
        }
    }