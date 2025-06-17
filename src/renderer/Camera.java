package renderer;

import primitives.*;
import scene.Scene;
import renderer.PixelManager.Pixel;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
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
            Vector vright = cam.Vto.crossProduct(vUp).normalize();
            // we need to calculate the Vup vector again to ensure orthogonality
            cam.Vup = vright.crossProduct(cam.Vto).normalize();
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
    Camera writeToImage(String fileName) {
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

        // First handle antiAliasing
        List<Ray> antiAliasingRays = List.of(baseRay);
        if (improvementSettings.useAntiAliasing()) {
            antiAliasingRays = improvementSettings.constructRays(baseRay, distance, this.height / this.nY);
        }

        Color color = Color.BLACK;

        // For each antiAliasing ray, apply depth of field if needed
        for (Ray antiAliasingRay : antiAliasingRays) {
            List<Ray> dofRays = List.of(antiAliasingRay);

            if (improvementSettings.useDepthOfField()) {
                Point basePoint = antiAliasingRay.getPoint(distance);
                Point focusPoint = antiAliasingRay.getPoint( focusPointDistance);
                dofRays = improvementSettings.constructRays(
                        new Ray(
                                focusPoint,
                                basePoint.subtract(focusPoint).normalize()
                        ),
                       focusPointDistance- distance, aperture
                );

                // reverse the rays' direction
                dofRays = dofRays.stream()
                        .map(r -> new Ray(r.getPoint(focusPointDistance), r.getDirection().scale(-1)))
                        .toList();
            }

            // Trace all the depth of field rays for this antiAliasing ray
            Color rayColor = Color.BLACK;
            for (Ray dofRay : dofRays) {
                rayColor = rayColor.add(rayTracerBase.traceRay(dofRay));
            }

            // Average the color for this set of depth of field rays
            rayColor = rayColor.reduce(dofRays.size());
            color = color.add(rayColor);
        }

        // Average the color across all anti-aliasing rays
        color = color.reduce(antiAliasingRays.size());

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
                .forEach(i -> IntStream.range(0, nX).parallel()
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

}
