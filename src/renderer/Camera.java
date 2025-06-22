package renderer;

import primitives.*;
import scene.Scene;
import renderer.PixelManager.Pixel;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * The {@code Camera} class represents a virtual camera in a 3D scene.
 * It is responsible for constructing rays through a view plane, used in ray tracing or other rendering techniques.
 * The camera is defined by:
 * <ul>
 *     <li>Position (location) in 3D space</li>
 *     <li>Three orthogonal vectors: Vto (view direction), Vup (upward direction), Vright (right direction)</li>
 *     <li>View plane dimensions and distance</li>
 * </ul>
 * <p>
 * The class uses the builder pattern to ensure safe and flexible construction.
 */
public class Camera implements Cloneable {
    /// The camera's position in 3D space
    private Point p0 = null;
    /// The direction the camera is facing (toward the view plane)
    private Vector Vto = null;
    /// The upward direction of the camera (typically (0,1,0))
    private Vector Vup = null;
    /// The right direction of the camera (orthogonal to Vto and Vup)
    private Vector Vright = null;
    /// The distance from the camera to the view plane
    private double distance = 0;
    /// The width of the view plane
    private double width = 0;
    /// The height of the view plane
    private double height = 0;
    /// The image writer used for rendering
    private ImageWriter imageWriter;
    /// The ray tracer used for rendering
    private RayTracerBase rayTracerBase;
    /// The number of horizontal pixels
    private int nX = 1;
    /// The number of vertical pixels
    private int nY = 1;
    /// The settings for improvement, such as anti-aliasing or other enhancements
    private Blackboard improvementSettings = Blackboard.getBuilder().build();
    /**
     * Amount of threads to use fore rendering image by the camera
     */
    private int threadsCount = 0;
    /**
     * Amount of threads to spare for Java VM threads:<br>
     * Spare threads if trying to use all the cores
     */
    private static final int SPARE_THREADS = 2;
    /**
     * Debug print interval in seconds (for progress percentage)<br>
     * if it is zero - there is no progress output
     */
    private double printInterval = 0;
    /**
     * Pixel manager for supporting:
     * <ul>
     * <li>multi-threading</li>
     * <li>debug print of progress percentage in Console window/tab</li>
     * </ul>
     */
    private PixelManager pixelManager;
    /**
     * The distance to the focus point for depth of field effects.
     * Default is 100, meaning the camera focuses on objects at this distance.
     */
    private double focusPointDistance = 100;
    /**
     * The aperture of the camera, used for depth of field effects.
     * Default is 0.5, meaning no depth of field effect.
     */
    private double aperture = 0.5;
    /**
     * The number of rays used for adaptive anti-aliasing.
     * Default is 4, meaning the camera will use 4 rays for adaptive sampling.
     */
    private static final int AMOUNT_OF_RAYS = 4;

    /**
     * Private constructor to enforce use of builder.
     */
    private Camera() {
    }

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
        private Scene scene;
        private RayTracerType rayTracerType = RayTracerType.SIMPLE;
        /**
         * Sets the camera's location.
         *
         * @param p the point representing the camera's position
         * @return the builder instance
         */
        public Builder setLocation(Point p) {
            cam.p0 = p;
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
         * @param vUp    the upward vector
         * @return the builder instance
         * @throws IllegalArgumentException if target equals the camera location
         */
        public Builder setDirection(Point target, Vector vUp) {
            if (target.equals(cam.p0)) {
                throw new IllegalArgumentException("Target point cannot be the same as the camera location");
            }
            cam.Vto = target.subtract(cam.p0).normalize();
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
         * @param width  the width of the view plane
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
            if (nX <= 0 || nY <= 0)
                throw new IllegalArgumentException("Resolution parameters must be positive");
            cam.imageWriter = new ImageWriter(nX, nY);
            this.cam.nX = nX;
            this.cam.nY = nY;

            if (cam.rayTracerBase == null) {
                cam.rayTracerBase = new SimpleRayTracer(null);
            }
            return this;
        }

        /**
         * Sets the blackboard settings for the camera.
         *
         * @param improvementSettings the builder for the blackboard settings to be applied
         * @return the builder instance
         */
        public Builder setBlackboard(Blackboard improvementSettings) {
            cam.improvementSettings = improvementSettings;
            return this;
        }

        /**
         * Sets the ray tracer for the camera based on the given scene and ray tracer type.
         *
         * @param scene         the scene to be rendered, used to initialize the ray tracer
         * @param rayTracerType the type of ray tracer to use, determining the ray tracing behavior
         * @return the builder instance
         */
        public Builder setRayTracer(Scene scene, RayTracerType rayTracerType) {
            this.scene = scene;
            this.rayTracerType = rayTracerType;
            return this;
        }

        /**
         * Set multi-threading <br>
         * Parameter value meaning:
         * <ul>
         * <li>-2 - number of threads is number of logical processors less 2</li>
         * <li>-1 - stream processing parallelization (implicit multi-threading) is used</li>
         * <li>0 - multi-threading is not activated</li>
         * <li>1 and more - literally number of threads</li>
         * </ul>
         *
         * @param threads number of threads
         * @return builder object itself
         */
        public Builder setMultithreading(int threads) {
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                cam.threadsCount = cores <= 2 ? 1 : cores;
            } else
                cam.threadsCount = threads;
            return this;
        }

        /**
         * Set debug printing interval. If it's zero - there won't be printing at all
         *
         * @param interval printing interval in %
         * @return builder object itself
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            cam.printInterval = interval;
            return this;
        }

        public Builder setFocusPointDistance(double focusPointDistance) {
            if (focusPointDistance < 0) {
                throw new IllegalArgumentException("Focus point distance must be non-negative");
            }
            cam.focusPointDistance = focusPointDistance;
            return this;
        }

        public Builder setAperture(double aperture) {
            if (aperture < 0) {
                throw new IllegalArgumentException("Aperture must be non-negative");
            }
            cam.aperture = aperture;
            return this;
        }

        /**
         * Moves the camera in the specified direction by a given distance.
         *
         * @param direction the direction to move in
         * @param distance  the distance to move
         * @return the camera instance for method chaining
         */
        public Builder move(Vector direction, double distance) {
            if (direction == null)
                throw new IllegalArgumentException("Direction vector cannot be null");

            Vector movement = direction.normalize().scale(distance);
            cam.p0 = cam.p0.add(movement);
            return this;
        }

        /**
         * Moves the camera in the forward direction (Vto) by a specified distance.
         *
         * @param distance the distance to move forward
         * @return the camera instance for method chaining
         */
        public Builder moveForward(double distance) {
            return move(cam.Vto, distance);
        }

        /**
         * Moves the camera in the backward direction (opposite to Vto) by a specified distance.
         *
         * @param distance the distance to move backward
         * @return the camera instance for method chaining
         */
        public Builder moveRight(double distance) {
            return move(cam.Vright, distance);
        }

        /**
         * Moves the camera in the left direction (opposite to Vright) by a specified distance.
         *
         * @param distance the distance to move left
         * @return the camera instance for method chaining
         */
        public Builder moveUp(double distance) {
            return move(cam.Vup, distance);
        }

        /**
         * Rotates the camera horizontally around a given target point and axis by a certain angle.
         *
         * @param angleDegrees The angle in degrees to rotate.
         * @return The builder instance.
         */
        public Builder orbitAroundTargetHorizontal(double angleDegrees, double distance) {
            Point target = cam.p0.add(cam.Vto.scale(distance));

            Vector axis = cam.Vup;

            Vector toCamera = cam.p0.subtract(target);
            Vector rotatedToCamera = rotateVector(toCamera, axis, angleDegrees);

            cam.p0 = target.add(rotatedToCamera);
            cam.Vto = target.subtract(cam.p0).normalize();
            cam.Vright = cam.Vto.crossProduct(axis).normalize();
            cam.Vup = cam.Vright.crossProduct(cam.Vto).normalize();

            return this;
        }

        /**
         * Rotates the camera vertically around a given target point and axis by a certain angle.
         *
         * @param angleDegrees The angle in degrees to rotate.
         * @return The builder instance.
         */
        public Builder orbitAroundTargetVertical(double angleDegrees, double distance) {
            Point target = cam.p0.add(cam.Vto.scale(distance));
            Vector axis = cam.Vright;
            Vector toCamera = cam.p0.subtract(target);

            Vector rotatedToCamera = rotateVector(toCamera, axis, angleDegrees);

            cam.p0 = target.add(rotatedToCamera);

            Vector up = new Vector(0, 1, 0);
            if(Math.abs(cam.Vto.dotProduct(up)) > 0.99){
                up = new Vector(1, 0, 0);
            }

            cam.Vto = target.subtract(cam.p0).normalize();
            //cam.Vright = cam.Vto.crossProduct(up).normalize();
            cam.Vup = cam.Vright.crossProduct(cam.Vto).normalize();

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

            if (cam.p0 == null) {
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
            if (rayTracerType == null) {
                throw new MissingResourceException(MISSING_DATA_ERROR, CAMERA_CLASS_NAME, "rayTracerType");
            }
            if (cam.imageWriter == null) {
                throw new MissingResourceException("ImageWriter is not set", "Camera", "imageWriter");
            }
            if (cam.aperture < 0) {
                throw new IllegalArgumentException("Aperture must be non-negative");
            }
            if (cam.focusPointDistance <= 0) {
                throw new IllegalArgumentException("Focus point distance must be non-negative");
            }
            switch (rayTracerType) {
                case SIMPLE:
                    cam.rayTracerBase = new SimpleRayTracer(scene,cam.improvementSettings);
                    break;
                case VOXEL:
                    cam.rayTracerBase = new VoxelRayTracer(scene, cam.improvementSettings);
                    break;
                default:
                    cam.rayTracerBase = null;
            }
            // Ensure Vright is calculated
            cam.Vright = cam.Vto.crossProduct(cam.Vup).normalize();

            try {
                return (Camera) cam.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }

        }
    }

    /**
     * Constructs a ray from the camera through the (i,j) pixel of the view plane.
     *
     * @param nX number of pixels in the X axis (columns)
     * @param nY number of pixels in the Y axis (rows)
     * @param j  the pixel's column index
     * @param i  the pixel's row index
     * @return a {@link Ray} that starts at the camera location and goes through the pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        Point pIJ = this.p0.add(this.Vto.scale(distance));

        double Rx = width / nX;
        double Ry = height / nY;

        double xJ = (j - ((nX - 1) / 2.0)) * Rx;
        double yI = -(i - ((nY - 1) / 2.0)) * Ry;

        if (!Util.isZero(xJ)) {
            pIJ = pIJ.add(Vright.scale(xJ));
        }
        if (!Util.isZero(yI)) {
            pIJ = pIJ.add(Vup.scale(yI));
        }

        Vector dir = pIJ.subtract(p0).normalize();
        return new Ray(p0, dir);
    }

    /**
     * This function renders image's pixel color map from the scene
     * included in the ray tracer object
     *
     * @return the camera object itself
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Prints a grid on the image writer.
     *
     * @param interval the interval between grid lines
     * @param color    the color of the grid lines
     * @return the camera instance for method chaining
     * @throws MissingResourceException if the image writer or ray tracer is not set
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(i, j, color);
                }
            }
        }
        return this;
    }

    /**
     * Writes the rendered image to a file.
     *
     * @param fileName the name of the file to write to
     * @return the camera instance for method chaining
     * @throws MissingResourceException if the image writer is not set
     */
    public Camera writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
        return this;
    }

    /**
     * Casts a ray through the specified pixel and writes the color to the image writer.
     *
     * @param x the pixel's column index
     * @param y the pixel's row index
     * @throws MissingResourceException if the image writer or ray tracer is not set
     */
    private void castRay(int x, int y) {
        Ray baseRay = constructRay(nX, nY, x, y);
        Color color = Color.BLACK;


        List<Ray> rays = List.of(baseRay);
        color = rayTracerBase.traceRay(baseRay);
        if (improvementSettings.useAntiAliasing()) {
            if(improvementSettings.useAdaptive())
                color = calcAdaptive(baseRay);
            else {
                // Construct anti-aliasing rays based on the base ray and pixel size
                rays = improvementSettings.constructRays(baseRay, distance, (this.height / this.nY)/2);
                for(Ray aaRay : rays) {
                    color = color.add(rayTracerBase.traceRay(aaRay));
                }
            }
        }
        if(improvementSettings.useDepthOfField())
        {
            Point basePoint = baseRay.getPoint(distance);
            Point focusPoint = baseRay.getPoint( focusPointDistance);
            rays = improvementSettings.constructRays(
                    new Ray(
                            focusPoint,
                            basePoint.subtract(focusPoint).normalize()
                    ),
                    focusPointDistance- distance, aperture
            );

            // reverse the rays' direction
            rays = rays.stream()
                    .map(r -> new Ray(r.getPoint(focusPointDistance), r.getDirection().scale(-1)))
                    .toList();

            for (Ray dofRay : rays) {
                color = color.add(rayTracerBase.traceRay(dofRay));
            }
        }


        if(rays.size() > 0)
            color = color.reduce(rays.size());

        imageWriter.writePixel(x, y, color);
        pixelManager.pixelDone();
    }

    /**
     * Render image using multi-threading by parallel streaming
     *
     * @return the camera object itself
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX)
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Render image without multi-threading
     *
     * @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i)
            for (int j = 0; j < nX; ++j)
                castRay(j, i);
        return this;
    }

    /**
     * Render image using multi-threading by creating and running raw threads
     *
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    /**
     * Rotates a vector around a given axis using Rodrigues' rotation formula.
     *
     * @param vec          the vector to rotate
     * @param axis         the axis to rotate around (must be normalized)
     * @param angleDegrees the angle in degrees
     * @return the rotated vector
     */
    private static Vector rotateVector(Vector vec, Vector axis, double angleDegrees) {
        double angleRad = Math.toRadians(angleDegrees);
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);

        Vector result = null;

        try {
            Vector term1 = vec.scale(cos);
            result = term1;
        } catch (IllegalArgumentException ignored) {
            // vec is zero → term1 is zero → ignore
        }

        try {
            Vector term2 = axis.crossProduct(vec).scale(sin);
            result = (result == null) ? term2 : result.add(term2);
        } catch (IllegalArgumentException ignored) {
            // cross product or scaling failed → ignore
        }

        try {
            double dot = axis.dotProduct(vec);
            Vector term3 = axis.scale(dot * (1 - cos));
            result = (result == null) ? term3 : result.add(term3);
        } catch (IllegalArgumentException ignored) {
            // axis or dot==0 → term3 is zero → ignore
        }

        if (result == null)
            throw new IllegalArgumentException("Resulting rotated vector is zero");

        return result;
    }
    /**
     * Calculates the color of a pixel using adaptive sampling.
     * This method constructs rays through the pixel and averages their colors.
     *
     * @param baseRay the ray through the center of the pixel
     * @return the calculated color for the pixel
     */
    private Color calcAdaptive(Ray baseRay) {
        // Create blackboard
        Blackboard.Builder builder = Blackboard.getBuilder();
        builder.setAmountOfRays(4).setMethod(Blackboard.MethodsOfPoints.GRID);
        Blackboard blackboard = builder.build();

        // Calculate pixel region radius
        double pixelSize = (this.height / this.nY);
        double radius = pixelSize / 2.0;

        // Construct 5 rays: center + 4 corners
        List<Ray> rays = blackboard.constructRays(baseRay, distance, radius);

        // Precompute basis vectors (camera space)
        Vector v = baseRay.getDirection();
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
        Vector u = v.crossProduct(w).normalize();
        Point p0 = baseRay.getHead();

        return calcAdaptive(
                radius,
                rays.get(0), rays.get(1), rays.get(2), rays.get(3), rays.get(4),
                0,
                u, w, p0,
                rayTracerBase::traceRay
        );
    }
    /**
     * Recursive method to calculate the color of a pixel using adaptive sampling.
     *
     * @param radius        the radius of the pixel region
     * @param centerRay    the ray through the center of the pixel
     * @param upLeft       the ray through the upper left corner of the pixel
     * @param upRight      the ray through the upper right corner of the pixel
     * @param downLeft     the ray through the lower left corner of the pixel
     * @param downRight    the ray through the lower right corner of the pixel
     * @param level        current recursion level
     * @param u            camera space basis vector u
     * @param w            camera space basis vector w
     * @param p0           camera position in 3D space
     * @param colorFunc    function to get color from a ray
     * @return calculated color for this pixel region
     */
    private Color calcAdaptive(
            double radius,
            Ray centerRay, Ray upLeft, Ray upRight, Ray downLeft, Ray downRight,
            int level,
            Vector u, Vector w, Point p0,
            Function<Ray, Color> colorFunc) {

        Color[] colors = new Color[AMOUNT_OF_RAYS];
        colors[0] = colorFunc.apply(upLeft);
        colors[1] = colorFunc.apply(upRight);
        colors[2] = colorFunc.apply(downLeft);
        colors[3] = colorFunc.apply(downRight);

        if (level >= improvementSettings.getMaxAdaptiveLevel() ||
                converged(colors, improvementSettings.getAdaptiveThreshold())) {
            Color avg = Color.BLACK;
            for (Color c : colors) avg = avg.add(c);
            return avg.reduce(colors.length);
        }

        Point centerPoint = centerRay.getPoint(distance);

        double halfRadius = radius / 2.0;
        double nextRadius = radius / Math.pow(2.0, level + 2);

        // Calculate midpoints of sub-regions
        Point centerUpLeftPoint    = centerPoint.add(u.scale(halfRadius)).add(w.scale(-halfRadius));
        Point centerUpRightPoint   = centerPoint.add(u.scale(halfRadius)).add(w.scale(halfRadius));
        Point centerDownLeftPoint  = centerPoint.add(u.scale(-halfRadius)).add(w.scale(-halfRadius));
        Point centerDownRightPoint = centerPoint.add(u.scale(-halfRadius)).add(w.scale(halfRadius));

        Point upPoint    = centerPoint.add(u.scale(radius));
        Point downPoint  = centerPoint.add(u.scale(-radius));
        Point leftPoint  = centerPoint.add(w.scale(-radius));
        Point rightPoint = centerPoint.add(w.scale(radius));

        // Construct rays from camera to those points
        Ray centerUpLeft    = new Ray(p0, centerUpLeftPoint.subtract(p0));
        Ray centerUpRight   = new Ray(p0, centerUpRightPoint.subtract(p0));
        Ray centerDownLeft  = new Ray(p0, centerDownLeftPoint.subtract(p0));
        Ray centerDownRight = new Ray(p0, centerDownRightPoint.subtract(p0));
        Ray up    = new Ray(p0, upPoint.subtract(p0));
        Ray down  = new Ray(p0, downPoint.subtract(p0));
        Ray left  = new Ray(p0, leftPoint.subtract(p0));
        Ray right = new Ray(p0, rightPoint.subtract(p0));

        // Recursive calls
        Color c1 = calcAdaptive(nextRadius, centerUpLeft, upLeft, up, left, centerRay, level + 1, u, w, p0, colorFunc);
        Color c2 = calcAdaptive(nextRadius, centerUpRight, up, upRight, centerRay, right, level + 1, u, w, p0, colorFunc);
        Color c3 = calcAdaptive(nextRadius, centerDownLeft, left, centerRay, downLeft, down, level + 1, u, w, p0, colorFunc);
        Color c4 = calcAdaptive(nextRadius, centerDownRight, centerRay, right, down, downRight, level + 1, u, w, p0, colorFunc);

        return c1.add(c2).add(c3).add(c4).reduce(AMOUNT_OF_RAYS);
    }
    /**
     * Checks whether all sample colors differ by no more than the threshold.
     *
     * @param s array of sample colors
     * @param t color difference threshold per channel
     * @return  true if converged
     */
    private boolean converged(Color[] s, double t) {
        for (int i = 0; i < s.length; i++) {
            for (int j = i + 1; j < s.length; j++) {
                if (Math.abs(s[i].getColor().getRed()   - s[j].getColor().getRed())   > t ||
                        Math.abs(s[i].getColor().getGreen() - s[j].getColor().getGreen()) > t ||
                        Math.abs(s[i].getColor().getBlue()  - s[j].getColor().getBlue())  > t) {
                    return false;
                }
            }
        }
        return true;
    }
}
