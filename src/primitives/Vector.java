package primitives;

            /**
             * The Vector class represents a vector in 3D space.
             * It extends the Point class and provides additional vector-specific operations.
             */
            public class Vector extends Point {

                /**
                 * Constructs a Vector with the specified Double3 object.
                 * Throws an IllegalArgumentException if the vector is the zero vector.
                 *
                 * @param xyz the Double3 object representing the coordinates of the vector
                 * @throws IllegalArgumentException if the vector is the zero vector
                 */
                public Vector(Double3 xyz) {
                    super(xyz);
                    if (xyz.equals(Double3.ZERO)) {
                        throw new IllegalArgumentException("Vector cannot be the zero vector");
                    }
                }

                /**
                 * Constructs a Vector with the specified coordinates.
                 * Throws an IllegalArgumentException if the vector is the zero vector.
                 *
                 * @param x the x-coordinate of the vector
                 * @param y the y-coordinate of the vector
                 * @param z the z-coordinate of the vector
                 * @throws IllegalArgumentException if the vector is the zero vector
                 */
                public Vector(double x, double y, double z) {
                    super(x, y, z);
                    if (xyz.equals(Double3.ZERO)) {
                        throw new IllegalArgumentException("Vector cannot be the zero vector");
                    }
                }

                /**
                 * Checks if this vector is equal to another object.
                 *
                 * @param obj the object to compare with
                 * @return true if the object is a Vector with the same coordinates, false otherwise
                 */
                @Override
                public boolean equals(Object obj) {
                    return super.equals(obj);
                }

                /**
                 * Returns a string representation of the vector.
                 *
                 * @return a string representation of the vector
                 */
                @Override
                public String toString() {
                    return super.toString();
                }

                /**
                 * Adds another vector to this vector and returns the resulting vector.
                 *
                 * @param v the vector to add
                 * @return the resulting vector after adding the vector
                 */
                public Vector add(Vector v) {
                    return new Vector(xyz.add(v.xyz));
                }

                /**
                 * Calculates the squared length of the vector.
                 *
                 * @return the squared length of the vector
                 */
                public double lengthSquared() {
                    return xyz.d1() * xyz.d1() + xyz.d2() * xyz.d2() + xyz.d3() * xyz.d3();
                }

                /**
                 * Calculates the length of the vector.
                 *
                 * @return the length of the vector
                 */
                public double length() {
                    return Math.sqrt(lengthSquared());
                }

                /**
                 * Scales the vector by a scalar and returns the resulting vector.
                 *
                 * @param scalar the scalar to scale the vector by
                 * @return the resulting vector after scaling
                 */
                public Vector scale(double scalar) {
                    return new Vector(xyz.scale(scalar));
                }

                /**
                 * Calculates the dot product of this vector and another vector.
                 *
                 * @param v the vector to calculate the dot product with
                 * @return the dot product of the two vectors
                 */
                public Double dotProduct(Vector v) {
                    return xyz.d1() * v.xyz.d1() + xyz.d2() * v.xyz.d2() + xyz.d3() * v.xyz.d3();
                }

                /**
                 * Calculates the cross product of this vector and another vector.
                 *
                 * @param v the vector to calculate the cross product with
                 * @return the resulting vector after calculating the cross product
                 */
                public Vector crossProduct(Vector v) {
                    return new Vector(new Double3(
                            xyz.d2() * v.xyz.d3() - xyz.d3() * v.xyz.d2(),
                            xyz.d3() * v.xyz.d1() - xyz.d1() * v.xyz.d3(),
                            xyz.d1() * v.xyz.d2() - xyz.d2() * v.xyz.d1()));
                }

                /**
                 * Normalizes the vector and returns the resulting unit vector.
                 *
                 * @return the resulting unit vector after normalization
                 */
                public Vector normalize() {
                    return scale(1 / length());
                }
            }