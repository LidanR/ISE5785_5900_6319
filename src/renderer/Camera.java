package renderer;

import primitives.*;

import java.util.MissingResourceException;

/**
 * The {@code Camera} class represents a virtual camera in a 3D scene.
 * It is responsible for constructing rays through a view plane, used in ray tracing or other rendering techniques.
 *
 * The camera is defined by:
 * <ul>
 *     <li>Position (location) in 3D space</li>
 *     <li>Three orthogonal vectors: Vto (view direction), Vup (upward direction), Vright (right direction)</li>
 *     <li>View plane dimensions and distance</li>
 * </ul>
 *
 * The class uses the builder pattern to ensure safe and flexible construction.
 *
 * @author ...
 */
public class Camera implements Cloneable {
    private Point location;
    private Vector Vto;
    private Vector Vup;
    private Vector Vright;
    private double distance = 0;
    private double width = 0;
    private double height = 0;

    /**
     * Private constructor to enforce use of builder.
     */
    private Camera() {}

    /**
     * Returns a new {@code Builder} instance for constructing a Camera.
     *
     * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Builder class for creating a Camera instance using the Builder pattern.
     */
    public static class Builder {
        private final Camera cam = new Camera();

        /**
         * Sets the camera's location.
         *
         * @param p the point representing the camera's position
         * @return the builder instance
         */
        public Builder setLocation(Point p) {
            cam.location = p;
            return this;
        }

        /**
         * Sets the direction vectors of the camera.
         *
         * @param vTo the forward direction vector (toward the view plane)
         * @param vUp the upward direction vector (typically (0,1,0))
         * @return the builder instance
         * @throws IllegalArgumentException if vTo and vUp are not orthogonal
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (!Util.isZero(vTo.dotProduct(vUp))) {
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            }
            cam.Vto = vTo.normalize();
            cam.Vup = vUp.normalize();
            cam.Vright = cam.Vto.crossProduct(cam.Vup).normalize();
            return this;
        }

        /**
         * Sets the direction of the camera based on a target point and up vector.
         *
         * @param target the point the camera should look at
         * @param vUp the upward vector
         * @return the builder instance
         * @throws IllegalArgumentException if target equals the camera location
         */
        public Builder setDirection(Point target, Vector vUp) {
            if (target.equals(cam.location)) {
                throw new IllegalArgumentException("Target point cannot be the same as the camera location");
            }
            cam.Vto = target.subtract(cam.location).normalize();
            cam.Vright = cam.Vto.crossProduct(vUp).normalize();
            cam.Vup = cam.Vright.crossProduct(cam.Vto).normalize();
            return this;
        }

        /**
         * Sets the direction of the camera using only a target point,
         * assuming an upward direction of (0,1,0).
         *
         * @param target the point to look at
         * @return the builder instance
         */
        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0));
        }

        /**
         * Sets the view plane size.
         *
         * @param width the width of the view plane
         * @param height the height of the view plane
         * @return the builder instance
         * @throws IllegalArgumentException if width or height is non-positive
         */
        public Builder setVpSize(double height, double width) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            cam.width = width;
            cam.height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance the distance value
         * @return the builder instance
         * @throws IllegalArgumentException if distance is not positive
         */
        public Builder setVpDistance(double distance) {
            if (distance <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            cam.distance = distance;
            return this;
        }

        /**
         * Sets the resolution (currently unused).
         *
         * @param nX number of horizontal pixels
         * @param nY number of vertical pixels
         * @return the builder instance
         */
        public Builder setResolution(int nX, int nY) {
            return this;
        }

        /**
         * Builds and returns a valid Camera object after validating all required parameters.
         *
         * @return a fully constructed Camera object
         * @throws MissingResourceException if required fields are missing
         * @throws IllegalArgumentException if invalid values are provided
         */
        public Camera build() {
            final String MISSING_DATA_ERROR = "Missing rendering data";
            final String CAMERA_CLASS_NAME = "Camera";

            if (cam.location == null) {
                throw new MissingResourceException(MISSING_DATA_ERROR, CAMERA_CLASS_NAME, "position");
            }
            if (cam.Vto == null) {
                throw new MissingResourceException(MISSING_DATA_ERROR, CAMERA_CLASS_NAME, "Vto");
            }
            if (cam.Vup == null) {
                throw new MissingResourceException(MISSING_DATA_ERROR, CAMERA_CLASS_NAME, "Vup");
            }
            if (cam.distance <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            if (cam.width <= 0 || cam.height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }

            // Ensure Vright is calculated
            cam.Vright = cam.Vto.crossProduct(cam.Vup).normalize();

            try {
                return (Camera) cam.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Failed to clone Camera object", e);
            }
        }
    }

    /**
     * Constructs a ray from the camera through the (i,j) pixel of the view plane.
     *
     * @param nX number of pixels in the X axis (columns)
     * @param nY number of pixels in the Y axis (rows)
     * @param j the pixel's column index
     * @param i the pixel's row index
     * @return a {@link Ray} that starts at the camera location and goes through the pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        Point pCenter = this.location.add(this.Vto.scale(distance));

        double Rx = width / nX;
        double Ry = height / nY;

        double xJ = (j - ((nX - 1) / 2.0)) * Rx;
        double yI = -(i - ((nY - 1) / 2.0)) * Ry;

        Point pIJ = pCenter;
        if (!Util.isZero(xJ)) {
            pIJ = pIJ.add(Vright.scale(xJ));
        }
        if (!Util.isZero(yI)) {
            pIJ = pIJ.add(Vup.scale(yI));
        }

        Vector dir = pIJ.subtract(location).normalize();
        return new Ray(location, dir);
    }
}
