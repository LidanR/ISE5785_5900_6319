package geometries;

import acceleration.AABB;
import primitives.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Cube extends Geometry {
    /**
     * Dimensions of the cube.
     * These represent the height, width, and depth of the cube.
     */
    private final double height;
    private final double width;
    private final double depth;
    /**
     * Center point of the cube.
     * This point is used as the reference for positioning and rotation.
     */
    private final Point center;
    /**
     * Rotation angles around the X, Y, and Z axes in degrees.
     * These angles are used to rotate the cube's vertices.
     */
    private final Double3 rotation;
    /**
     * List of polygons representing the faces of the cube.
     * Each polygon is defined by its vertices.
     */
    private List<Polygon> polygons;
    /**
     * Constructs a cube with specified dimensions and a default center at the origin.
     * @param height the height of the cube
     * @param width the width of the cube
     * @param depth the depth of the cube
     * @throws IllegalArgumentException if any dimension is not positive
     */
    public Cube(double height, double width, double depth, Point center) {
        this(height, width, depth, center, Double3.ZERO);
    }
    /**
     * Constructs a cube with equal dimensions and a specified center.
     * @param size the size of the cube (height, width, depth)
     * @param center the center point of the cube
     * @throws IllegalArgumentException if size is not positive
     */
    public Cube(double size, Point center) {
        this(size, size, size, center);
    }
    /**
     * Constructs a cube with equal dimensions and a default center at the origin.
     * @param size the size of the cube (height, width, depth)
     * @param center the center point of the cube
     * @param rotation the rotation angles around X, Y, and Z axes
     * @throws IllegalArgumentException if size is not positive
     */
    public Cube(double size, Point center, Double3 rotation)
    {
        this(size, size, size, center,rotation);
    }
    /**
     * Constructs a cube with specified dimensions, center, and rotation angles.
     * @param height the height of the cube
     * @param width the width of the cube
     * @param depth the depth of the cube
     * @param center the center point of the cube
     * @param rotation the rotation angles around X, Y, and Z axes
     * @throws IllegalArgumentException if any dimension is not positive
     */
    public Cube(double height, double width, double depth, Point center,
                Double3 rotation) {
        if (height <= 0 || width <= 0 || depth <= 0)
            throw new IllegalArgumentException("Dimensions must be positive");

        this.height = height;
        this.width = width;
        this.depth = depth;
        this.center = center;
        this.rotation = rotation;
        polygons = initPolygons();
    }
    /**
     * Initializes the polygons that make up the cube.
     * @return a list of polygons representing the cube's faces
     */
    private List<Polygon> initPolygons() {
        List<Point> vertices = new ArrayList<>();

        double hw = width / 2;
        double hh = height / 2;
        double hd = depth / 2;

        // All 8 vertices before rotation
        int[][] signs = {
                {-1, -1, -1},
                {1, -1, -1},
                {1, -1, 1},
                {-1, -1, 1},
                {-1, 1, -1},
                {1, 1, -1},
                {1, 1, 1},
                {-1, 1, 1}
        };

        for (int[] sign : signs) {
            Vector local = new Vector(sign[0] * hw, sign[1] * hh, sign[2] * hd);
            Vector rotated = rotateVector(local);
            vertices.add(center.add(rotated));
        }

        List<Polygon> faces = new ArrayList<>();

        // Bottom (0,1,2,3)
        faces.add(new Polygon(vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3)));

        // Top (7,6,5,4)
        faces.add(new Polygon(vertices.get(7), vertices.get(6), vertices.get(5), vertices.get(4)));

        // Left (0,3,7,4)
        faces.add(new Polygon(vertices.get(0), vertices.get(3), vertices.get(7), vertices.get(4)));

        // Right (1,5,6,2)
        faces.add(new Polygon(vertices.get(1), vertices.get(5), vertices.get(6), vertices.get(2)));

        // Front (3,2,6,7)
        faces.add(new Polygon(vertices.get(3), vertices.get(2), vertices.get(6), vertices.get(7)));

        // Back (0,4,5,1)
        faces.add(new Polygon(vertices.get(0), vertices.get(4), vertices.get(5), vertices.get(1)));

        return faces;
    }
    /**
     * Rotates a vector based on the cube's rotation angles.
     * @param v the vector to rotate
     * @return the rotated vector
     */
    private Vector rotateVector(Vector v) {
        double x = v.dotProduct(new Vector(1, 0, 0));
        double y = v.dotProduct(new Vector(0, 1, 0));
        double z = v.dotProduct(new Vector(0, 0, 1));

        double cosX = Math.cos(Math.toRadians(rotation.d1()));
        double sinX = Math.sin(Math.toRadians(rotation.d1()));
        double cosY = Math.cos(Math.toRadians(rotation.d2()));
        double sinY = Math.sin(Math.toRadians(rotation.d2()));
        double cosZ = Math.cos(Math.toRadians(rotation.d3()));
        double sinZ = Math.sin(Math.toRadians(rotation.d3()));

        // Rotate around X
        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;

        y = y1;
        z = z1;

        // Rotate around Y
        double x2 = x * cosY + z * sinY;
        double z2 = -x * sinY + z * cosY;

        x = x2;
        z = z2;

        // Rotate around Z
        double x3 = x * cosZ - y * sinZ;
        double y3 = x * sinZ + y * cosZ;

        return new Vector(x3, y3, z);
    }
    /**
     * Calculates the intersections of a ray with the cube.
     * @param ray the ray to check for intersections
     * @param maxDistance the maximum distance to check for intersections
     * @return a list of intersections, or null if no intersections are found
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> intersections = new LinkedList<>();
        for (Polygon polygon : polygons) {
            List<Intersection> hits = polygon.calculateIntersectionsHelper(ray, maxDistance);
            if (hits != null) {
                intersections.addAll(hits);
            }
        }
        // If no intersections found, return null
        if (intersections.isEmpty()) {
            return null;
        }
        Intersection closest = ray.findClosestIntersection(intersections);
        return List.of(new Intersection(this,closest.point));
    }
    /**
     * Calculates the normal vector at a given point on the cube.
     * @param point the point on the geometry
     * @return the normal vector at the point, or null if the point is not on any face
     */
    @Override
    public Vector getNormal(Point point) {
        double epsilon = 1e-10;

        // Vector from cube center to the point
        Vector fromCenter = point.subtract(center);

        double dx = fromCenter.getX();
        double dy = fromCenter.getY();
        double dz = fromCenter.getZ();

        double halfWidth = width / 2;
        double halfHeight = height / 2;
        double halfDepth = depth / 2;

        // Determine which face the point is on
        if (Math.abs(dx - halfWidth) < epsilon) return new Vector(1, 0, 0);    // Right
        if (Math.abs(dx + halfWidth) < epsilon) return new Vector(-1, 0, 0);   // Left
        if (Math.abs(dy - halfHeight) < epsilon) return new Vector(0, 1, 0);   // Top
        if (Math.abs(dy + halfHeight) < epsilon) return new Vector(0, -1, 0);  // Bottom
        if (Math.abs(dz - halfDepth) < epsilon) return new Vector(0, 0, 1);    // Front
        if (Math.abs(dz + halfDepth) < epsilon) return new Vector(0, 0, -1);   // Back

        // Fallback: find the dominant axis direction (most aligned component)
        double absX = Math.abs(dx - halfWidth);
        double absNX = Math.abs(dx + halfWidth);
        double absY = Math.abs(dy - halfHeight);
        double absNY = Math.abs(dy + halfHeight);
        double absZ = Math.abs(dz - halfDepth);
        double absNZ = Math.abs(dz + halfDepth);

        double min = Math.min(Math.min(Math.min(Math.min(Math.min(absX, absNX), absY), absNY), absZ), absNZ);

        if (min == absX) return new Vector(1, 0, 0);
        if (min == absNX) return new Vector(-1, 0, 0);
        if (min == absY) return new Vector(0, 1, 0);
        if (min == absNY) return new Vector(0, -1, 0);
        if (min == absZ) return new Vector(0, 0, 1);
        return new Vector(0, 0, -1);
    }


    @Override
    public AABB getAABB() {
        if(box == null) {
            // Calculate the AABB based on the cube's dimensions and center
            double hw = width / 2;
            double hh = height / 2;
            double hd = depth / 2;

            Point min = center.subtract(new Vector(hw, hh, hd));
            Point max = center.add(new Vector(hw, hh, hd));

            box = new AABB(min, max);
        }
        return box;
    }
}
