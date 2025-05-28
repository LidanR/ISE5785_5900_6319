package primitives;

import geometries.Plane;
import geometries.Polygon;
import renderer.Camera;

import java.util.ArrayList;
import java.util.List;
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
     * The list of points generated based on the specified method.
     * This list is populated when the Blackboard is built using the Builder class.
     */
    public List<Point> points=null;
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
    private MethodsOfPoints method = MethodsOfPoints.GRID;
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
    public static Blackboard.Builder getBuilder() {
        return new Blackboard.Builder();
    }

    public static class Builder {
        private final Blackboard blackboard;

        public Builder() {
            blackboard = new Blackboard();
        }

        public Builder setUseCircle(boolean useCircle) {
            blackboard.useCircle = useCircle;
            return this;
        }
        public Builder setMethod(MethodsOfPoints method) {
            blackboard.method = method;
            return this;
        }
        public Builder setAmountOfRays(int amountOfRays) {
            blackboard.amountOfRays = amountOfRays;
            return this;
        }
        public Builder setGridSize(int gridSize) {
            blackboard.gridSize = gridSize;
            return this;
        }


        public Blackboard build(Ray base,Point center) {
            blackboard.CalcPoints(base, center);
            if (blackboard.useCircle) {
                blackboard.circleThePoints();
            }
            return blackboard.clone();
        }
    }

    private void CalcPoints(Ray base,Point center)
    {
        points = new ArrayList<>();
        points.add(center);
        switch (method) {
            case GRID:
                createGridPoints(base, center);
                break;
            case RANDOM:
                CreateRandomPoints(base, center);
                break;
            case JITTERED:
                createJitteredPoints(base, center);
                break;
        }
    }

    private void createGridPoints(Ray base, Point center) {
        int gridSize = this.gridSize;
        double cellSize = 1.0 / gridSize;

        Vector v = base.getDir();
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
        Vector u = v.crossProduct(w).normalize();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double x = (i + 0.5) * cellSize - 0.5;
                double y = (j + 0.5) * cellSize - 0.5;

                    Point point = center;
                    if (!Util.isZero(x)) point = point.add(u.scale(x));
                    if (!Util.isZero(y)) point = point.add(w.scale(y));

                    points.add(point);

            }
        }
    }

    private void CreateRandomPoints(Ray base, Point center) {
        double cellSize = 1.0 / gridSize;
        for (int i = 0; i < amountOfRays; i++) {
            double x = (Math.random() - 0.5) * cellSize;
            double y = (Math.random() - 0.5) * cellSize;

                Point point = center;
                Vector v = base.getDir();
                Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
                Vector u = v.crossProduct(w).normalize();

                if (!Util.isZero(x)) point = point.add(u.scale(x));
                if (!Util.isZero(y)) point = point.add(w.scale(y));

                points.add(point);

        }
    }

    private void createJitteredPoints(Ray base, Point center) {
        double cellSize = 1.0 / gridSize;

        Vector v = base.getDir();
        Vector w = Vector.AXIS_Y.equals(v) ? Vector.AXIS_X : Vector.AXIS_Y.crossProduct(v).normalize();
        Vector u = v.crossProduct(w).normalize();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double x = (i + Math.random()) * cellSize - 0.5;
                double y = (j + Math.random()) * cellSize - 0.5;

                    Point point = center;
                    if (!Util.isZero(x)) point = point.add(u.scale(x));
                    if (!Util.isZero(y)) point = point.add(w.scale(y));

                    points.add(point);

            }
        }
    }

    /**
     * Filters the existing points to keep only those that lie within a unit circle.
     * This method removes all points that are outside the circle centered at the origin
     * with a radius of 0.5 units.
     */
    private void circleThePoints() {
        if (points == null || points.isEmpty()) return;

        double radius = 0.5;
        double radiusSquared = radius * radius;

        Point center = points.getFirst();
        points.removeIf(point -> {
            double dx = point.xyz.d1() - center.xyz.d1();
            double dy = point.xyz.d2() - center.xyz.d2();
            return dx * dx + dy * dy > radiusSquared;
        });
    }


    @Override
    public Blackboard clone() {
        try {
            Blackboard cloned = (Blackboard) super.clone();
            cloned.points = new ArrayList<>(points);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
