package renderer;

import primitives.*;
import primitives.Vector;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Blackboard class is responsible for generating a set of points based on specified methods.
 * It supports three methods of point generation: GRID, RANDOM, and JITTERED.
 * The points can be generated within a unit circle or a unit square based on the useCircle flag.
 * This class is designed to be used in conjunction with a Ray and a center Point,
 * allowing for flexible point generation in 3D space.
 * The class uses a Builder pattern for construction,
 **/
public class Blackboard implements Cloneable {
    /**
     * Indicates whether to use a circle for point generation.
     * If true, points will be generated within a unit circle.
     * If false, points will be generated within a unit square.
     * Default is false.
     */
    private boolean useCircle = false;
    /**
     * Indicates whether to use soft shadows.
     * If true, the rays will be generated in a way that simulates soft shadows.
     * Default is false.
     */
    private boolean softShadows = false;
    /**
     * Indicates whether to use anti-aliasing.
     * If true, the rays will be generated in a way that reduces aliasing artifacts.
     * Default is false.
     */
    private boolean antiAliasing = false;
    /**
     * Indicates whether to use depth of field.
     * If true, the rays will be generated in a way that simulates depth of field effects.
     * Default is false.
     */
    private boolean depthOfField = false;
    /**
     * Indicates whether to use blurry and glossy effects.
     * If true, the rays will be generated in a way that simulates blurry and glossy effects.
     * Default is false.
     */
    private boolean blurryAndGlossy = false;
    /**
     * Indicates whether to use adaptive sampling.
     * If true, the number of rays generated will adapt based on the scene complexity.
     * Default is false.
     */
    private boolean adaptiveSampling = false;
    /**
     * Enum representing the methods of generating points.
     * GRID: Generates points in a grid pattern.
     * RANDOM: Generates points randomly within a unit square.
     * JITTERED: Generates points in a grid pattern with random offsets.
     */
    public enum MethodsOfPoints {
        GRID,
        RANDOM,
        JITTERED
    }
    /**
     * The method used to generate points.
     * Default is GRID, which generates points in a grid pattern.
     */
    private MethodsOfPoints method = MethodsOfPoints.JITTERED;
    /**
     * The number of rays to be generated.
     * Default is 80, which is suitable for a 10x10 grid.
     */
    private int amountOfRays= 80;
    /**
     * The size of the grid for point generation.
     * This is used to determine the number of points in a grid pattern.
     * Default is 10, which corresponds to a 10x10 grid.
     */
    private int gridSize = 10;
    /**
     * Default radius for point generation.
     * This is used to define the size of the area in which points are generated.
     * Default is 0.5, which corresponds to a unit circle or square of size 1x1.
     */
    private static final double DEFAULT_RADIUS = 0.5;
    /**
     * The threshold for adaptive sampling.
     * This value determines the sensitivity of the adaptive sampling algorithm.
     * Default is 0.001, which means that if the difference in color intensity is less than this value,
     * the algorithm will adaptively reduce the number of rays.
     */
    private double adaptiveThereHold = 0.01;
    /**
     * The maximum adaptive level for adaptive sampling.
     * This value determines the maximum number of rays that can be generated in adaptive sampling.
     * Default is 5, which means that the algorithm can generate up to 5 times the base number of rays.
     */
    private int maxAdaptiveLevel = 5;

    /**
     * Private constructor to prevent direct instantiation.
     * Use the Builder class to create an instance of Blackboard.
     */
    private Blackboard() {}

    /**
     * Static method to get a new Builder instance for constructing a Blackboard.
     * This method allows for fluent construction of the Blackboard with various configurations.
     *
     * @return a new instance of the Blackboard.Builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * The Builder class is used to construct instances of the Blackboard class.
     * It allows for setting various configurations such as point generation method,
     * number of rays, grid size, radius, and whether to use soft shadows or anti-aliasing.
     */
    public static class Builder {
        /**
         * The Blackboard instance being built.
         * This instance is configured through the Builder methods.
         */
        private final Blackboard blackboard;
        /**
         * Default constructor for the Builder class.
         * Initializes a new Blackboard instance with default configurations.
         */
        public Builder() {
            blackboard = new Blackboard();
        }

        /**
         * Sets whether to use a circular method for generating points.
         * This configuration determines the shape of the area used by the blackboard.
         *
         * @param useCircle true to use a circular point-generation method, false to use a different method
         * @return this Builder instance for method chaining
         */
        public Builder setUseCircle(boolean useCircle) {
            blackboard.useCircle = useCircle;
            return this;
        }

        /**
         * Sets the method for generating points in the blackboard configuration.
         * The chosen method determines how points will be distributed within the area.
         *
         * @param method the method of generating points, such as GRID, RANDOM, or JITTERED
         * @return this Builder instance for method chaining
         */
        public Builder setMethod(MethodsOfPoints method) {
            blackboard.method = method;
            return this;
        }

        /**
         * Sets the number of rays used in the configuration.
         * The amount of rays determines the density or quantity of rays being generated.
         *
         * @param amountOfRays the number of rays to be set
         * @return this Builder instance for method chaining
         */
        public Builder setAmountOfRays(int amountOfRays) {
            blackboard.amountOfRays = amountOfRays;
            return this;
        }

        /**
         * Sets the grid size for the underlying blackboard configuration.
         * The grid size determines the spacing between grid points in the blackboard.
         *
         * @param gridSize the size of the grid, represented as a double
         * @return this Builder instance for method chaining
         */
        public Builder setGridSize(int gridSize) {
            blackboard.gridSize = gridSize;
            return this;
        }
        /**
         * Sets whether soft shadows should be used.
         * If true, the rays will be generated in a way that simulates soft shadows.
         * Default is false.
         *
         * @param softShadows true to enable soft shadows, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setSoftShadows(boolean softShadows) {
            blackboard.softShadows = softShadows;
            return this;
        }
        /**
         * Sets whether anti-aliasing should be used.
         * If true, the rays will be generated in a way that reduces aliasing artifacts.
         * Default is false.
         *
         * @param antiAliasing true to enable anti-aliasing, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setAntiAliasing(boolean antiAliasing) {
            blackboard.antiAliasing = antiAliasing;
            return this;
        }
        /**
         * Sets whether depth of field should be used.
         * If true, the rays will be generated in a way that simulates depth of field effects.
         * Default is false.
         *
         * @param depthOfField true to enable depth of field, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setDepthOfField(boolean depthOfField) {
            blackboard.depthOfField = depthOfField;
            return this;
        }
        /**
         * Sets whether to use blurry and glossy effects.
         * If true, the rays will be generated in a way that simulates blurry and glossy effects.
         * Default is false.
         *
         * @param blurryAndGlossy true to enable blurry and glossy effects, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setBlurryAndGlossy(boolean blurryAndGlossy) {
            blackboard.blurryAndGlossy = blurryAndGlossy;
            return this;
        }
        /**
         * Sets whether to use adaptive sampling.
         * If true, the number of rays generated will adapt based on the scene complexity.
         * Default is false.
         *
         * @param adaptiveSampling true to enable adaptive sampling, false to disable
         * @return this Builder instance for method chaining
         */
        public Builder setAdaptiveSampling(boolean adaptiveSampling) {
            blackboard.adaptiveSampling = adaptiveSampling;
            return this;
        }
        /**
         * Sets the maximum adaptive level for adaptive sampling.
         * This value determines the maximum number of rays that can be generated in adaptive sampling.
         * Default is 5, which means that the algorithm can generate up to 5 times the base number of rays.
         *
         * @param maxAdaptiveLevel the maximum adaptive level to be set
         * @return this Builder instance for method chaining
         */
        public Builder setMaxAdaptiveLevel(int maxAdaptiveLevel) {
            blackboard.maxAdaptiveLevel = maxAdaptiveLevel;
            return this;
        }
        /**
         * Sets the adaptive threshold for adaptive sampling.
         * This value determines the sensitivity of the adaptive sampling algorithm.
         * Default is 0.01, which means that if the difference in color intensity is less than this value,
         * the algorithm will adaptively reduce the number of rays.
         *
         * @param adaptiveThereHold the adaptive threshold to be set
         * @return this Builder instance for method chaining
         */
        public Builder setAdaptiveThereHold(double adaptiveThereHold) {
            blackboard.adaptiveThereHold = adaptiveThereHold;
            return this;
        }

        /**
         * Builds the Blackboard instance with the specified configurations.
         * This method checks if the sender point is set and throws an exception if it is not.
         *
         * @return a new instance of Blackboard with the configured properties
         * @throws MissingResourceException if the sender point is not set
         */
        public Blackboard build() {
            return blackboard.clone();
        }
    }

    /**
     * Constructs rays from the generated points based on the specified base ray and center point.
     * Uses a default radius size
     * This method uses local variables to avoid thread safety issues.
     *
     * @param baseRay the base ray used for direction calculation
     * @param distance the center point around which rays are constructed
     * @return a list of rays constructed from the generated points
     */
    public List<Ray> constructRays(Ray baseRay, double distance) {
        return constructRays(baseRay, distance, DEFAULT_RADIUS);
    }

    /**
     * Constructs rays from the generated points based on the specified base ray and center point.
     * This method uses local variables to avoid thread safety issues.
     *
     * @param baseRay the base ray used for direction calculation
     * @param distance the center point around which rays are constructed
     * @param radius the radius of the target zone
     * @return a list of rays constructed from the generated points
     */
    public List<Ray> constructRays(Ray baseRay, double distance, double radius) {
        Point center = baseRay.getPoint(distance);
        List<Point> localPoints = calculatePoints(baseRay, center, radius);
        if (useCircle) {
            localPoints = filterCirclePoints(localPoints, center, radius);
        }

        List<Ray> resultRays = new LinkedList<>();
        for (Point point : localPoints) {
            Vector direction = point.subtract(baseRay.getHead()).normalize();
            resultRays.add(new Ray(baseRay.getHead(), direction));
        }
        return resultRays;
    }

    /**
     * Calculates points based on the specified method and center point.
     * This method generates points in a grid, randomly, or jittered based on the method set in the Blackboard.
     *
     * @param baseRay the base ray used for direction calculation
     * @param center the center point around which points are constructed
     * @param radius the radius for point generation
     * @return a list of generated points
     */
    private List<Point> calculatePoints(Ray baseRay, Point center, double radius) {
        List<Point> localPoints = new LinkedList<>();
        localPoints.add(center);

        if (amountOfRays == 0 || amountOfRays == 1 || radius == 0) {
            return localPoints;
        }

        switch (method) {
            case GRID:
                createGridPoints(baseRay, center, radius, localPoints);
                break;
            case RANDOM:
                createRandomPoints(baseRay, center, radius, localPoints);
                break;
            case JITTERED:
                createJitteredPoints(baseRay, center, radius, localPoints);
                break;
        }
        return localPoints;
    }

    /**
     * Creates evenly spaced grid points based on the base ray and center point.
     * Selects a subset of the grid cells randomly, preserving spatial uniformity with holes.
     */
    private void createGridPoints(Ray baseRay, Point center, double radius, List<Point> pointsList) {
        Vector v = baseRay.getDirection();
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
        Vector u = v.crossProduct(w).normalize();

        int gridCount = gridSize;  // Convert to integer for looping
        double cellSize = (2.0 * radius) / gridSize;

        List<int[]> cellIndices = new ArrayList<>();
        int mid = gridCount / 2;

        // Collect all grid cell indices
        for (int i = 0; i < gridCount; i++) {
            for (int j = 0; j < gridCount; j++) {
                cellIndices.add(new int[]{i, j});
            }
        }

        // Sort cells by distance from the grid center, farthest first
        cellIndices.sort((a, b) -> {
            double da = Math.pow(a[0] - mid + 0.5, 2) + Math.pow(a[1] - mid + 0.5, 2);
            double db = Math.pow(b[0] - mid + 0.5, 2) + Math.pow(b[1] - mid + 0.5, 2);
            return Double.compare(db, da);  // reversed: farthest first
        });

        int count = Math.min(amountOfRays, cellIndices.size());

        for (int k = 0; k < count; k++) {
            int[] cell = cellIndices.get(k);
            int i = cell[0];
            int j = cell[1];

            double x = (i + 0.5) * cellSize - radius;
            double y = (j + 0.5) * cellSize - radius;

            Point point = center;
            if (!Util.isZero(x)) point = point.add(u.scale(x));
            if (!Util.isZero(y)) point = point.add(w.scale(y));

            pointsList.add(point);
        }
    }



    /**
     * Creates jittered points within grid cells, based on the base ray and center point.
     * Randomly skips some cells to generate {@code amountOfRays} total, preserving the grid layout with holes.
     */
    private void createJitteredPoints(Ray baseRay, Point center, double radius, List<Point> pointsList) {
        Vector v = baseRay.getDirection();
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
        Vector u = v.crossProduct(w).normalize();

        double cellSize = (2.0 * radius) / gridSize;

        List<int[]> cellIndices = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cellIndices.add(new int[]{i, j});
            }
        }

        Collections.shuffle(cellIndices);
        int count = Math.min(amountOfRays, cellIndices.size());

        for (int k = 0; k < count; k++) {
            int[] cell = cellIndices.get(k);
            int i = cell[0];
            int j = cell[1];

            double x = (i + Math.random()) * cellSize - radius;
            double y = (j + Math.random()) * cellSize - radius;

            Point point = center;
            if (!Util.isZero(x)) point = point.add(u.scale(x));
            if (!Util.isZero(y)) point = point.add(w.scale(y));

            pointsList.add(point);
        }
    }


    /**
     * Creates random points based on the base ray and center point.
     * Each point is placed randomly within the square area defined by {@code radius}.
     * Generates exactly {@code amountOfRays} points.
     *
     * @param baseRay    the base ray used to determine orientation
     * @param center     the center point around which points are created
     * @param radius     the radius defining the square area size
     * @param pointsList the list to which generated points are added
     */
    private void createRandomPoints(Ray baseRay, Point center, double radius, List<Point> pointsList) {
        Vector v = baseRay.getDirection();
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
        Vector u = v.crossProduct(w).normalize();

        for (int i = 0; i < amountOfRays; i++) {
            double x = (Math.random() * 2 - 1) * radius;
            double y = (Math.random() * 2 - 1) * radius;

            Point point = center;
            if (!Util.isZero(x)) point = point.add(u.scale(x));
            if (!Util.isZero(y)) point = point.add(w.scale(y));

            pointsList.add(point);
        }
    }


    /**
     * Filters points to keep only those within the specified radius from the center.
     * This method removes all points that are outside the circle.
     *
     * @param pointsList the list of points to filter
     * @param center the center point of the circle
     * @param radius the radius of the circle
     * @return a filtered list containing only points within the circle
     */
    private List<Point> filterCirclePoints(List<Point> pointsList, Point center, double radius) {
        return pointsList.stream()
                .filter(point -> Util.alignZero(point.distance(center)) <= radius)
                .collect(Collectors.toList());
    }

    /**
     * Returns whether soft shadows are enabled.
     */
    public Boolean useSoftShadows() {
        return softShadows;
    }

    /**
     * Returns whether anti-aliasing is enabled.
     */
    public Boolean useAntiAliasing() {
        return antiAliasing;
    }

    /**
     * Returns whether depth of field is enabled.
     */
    public Boolean useDepthOfField() {
        return depthOfField;
    }

    /**
     * Returns whether blurry and glossy effects are enabled.
     */
    public Boolean useBlurryAndGlossy() {
        return blurryAndGlossy;
    }
    /**
     * Returns whether adaptive sampling is enabled.
     * @return true if adaptive sampling is enabled, false otherwise
     */
    public Boolean useAdaptive() {
        return adaptiveSampling;
    }

    /**
     * Returns the method used for generating points.
     * @return the method of generating points, such as GRID, RANDOM, or JITTERED
     */
    public int getMaxAdaptiveLevel()
    {
        return maxAdaptiveLevel;
    }
    public double getAdaptiveThreshold()
    {
        return adaptiveThereHold;
    }



    /**
     * Creates a clone of this Blackboard instance.
     */
    @Override
    public Blackboard clone() {
        try {
            return (Blackboard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}