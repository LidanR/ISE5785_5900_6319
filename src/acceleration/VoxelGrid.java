package acceleration;

import primitives.*;
import geometries.Intersectable;
import geometries.Intersectable.Intersection;
import primitives.Vector;

import java.util.*;

/**
 * A VoxelGrid is an acceleration structure that partitions 3D space into a regular grid of voxels.
 * Each voxel contains a list of geometries that intersect it.
 * This implementation uses a sparse HashMap to store only occupied voxels, improving memory efficiency.
 */
public class VoxelGrid {
    private final AABB bounds; // The global bounding box of the scene
    private final int nx, ny, nz; // Number of voxels along each axis
    private final double voxelSizeX, voxelSizeY, voxelSizeZ; // Size of each voxel along each axis
    private final Map<VoxelIndex, List<Intersectable>> grid = new HashMap<>(); // Sparse grid to store intersectable objects in voxels

    /**
     * Constructs a voxel grid using the provided bounding box and grid resolution.
     *
     * @param bounds the global bounding box of the scene
     * @param nx     number of voxels along the X-axis
     * @param ny     number of voxels along the Y-axis
     * @param nz     number of voxels along the Z-axis
     */
    public VoxelGrid(AABB bounds, int nx, int ny, int nz) {
        this.bounds = bounds;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        voxelSizeX = (bounds.getMax().getX() - bounds.getMin().getX()) / nx;
        voxelSizeY = (bounds.getMax().getY() - bounds.getMin().getY()) / ny;
        voxelSizeZ = (bounds.getMax().getZ() - bounds.getMin().getZ()) / nz;
    }

    /**
     * Adds a geometry to all voxels overlapping its bounding box.
     *
     * @param obj       the geometry to add
     * @param objBounds the axis-aligned bounding box of the geometry
     */
    public void addObject(Intersectable obj, AABB objBounds) {
        int minX = clamp(toGridX(objBounds.getMin().getX()), 0, nx - 1);
        int maxX = clamp(toGridX(objBounds.getMax().getX()), 0, nx - 1);
        int minY = clamp(toGridY(objBounds.getMin().getY()), 0, ny - 1);
        int maxY = clamp(toGridY(objBounds.getMax().getY()), 0, ny - 1);
        int minZ = clamp(toGridZ(objBounds.getMin().getZ()), 0, nz - 1);
        int maxZ = clamp(toGridZ(objBounds.getMax().getZ()), 0, nz - 1);

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                for (int k = minZ; k <= maxZ; k++) {
                    VoxelIndex index = new VoxelIndex(i, j, k);
                    // Add the object to the voxel's list, creating the list if it doesn't exist
                    grid.computeIfAbsent(index, key -> new ArrayList<>()).add(obj);
                }
            }
        }
    }

    /**
     * Finds the closest intersection between the ray and the geometries in the grid.
     * AKA 3D DDA algorithm for ray traversal through the voxel grid.
     *
     * @param ray the ray to test
     * @return the closest intersection, or null if no intersection was found
     */
    public Intersection findClosestIntersection(Ray ray) {
        if (!bounds.hasIntersection(ray)) return null; // If the ray does not intersect the scene bounding box, return null

        Point origin = ray.getHead();
        Vector dir = ray.getDirection();

        int ix = clamp(toGridX(origin.getX()), 0, nx - 1);
        int iy = clamp(toGridY(origin.getY()), 0, ny - 1);
        int iz = clamp(toGridZ(origin.getZ()), 0, nz - 1);

        int stepX = dir.getX() >= 0 ? 1 : -1;
        int stepY = dir.getY() >= 0 ? 1 : -1;
        int stepZ = dir.getZ() >= 0 ? 1 : -1;

        double nextX = voxelBoundary(bounds.getMin().getX(), ix, stepX, voxelSizeX);
        double nextY = voxelBoundary(bounds.getMin().getY(), iy, stepY, voxelSizeY);
        double nextZ = voxelBoundary(bounds.getMin().getZ(), iz, stepZ, voxelSizeZ);

        double tMaxX = safeDivide(nextX - origin.getX(), dir.getX());
        double tMaxY = safeDivide(nextY - origin.getY(), dir.getY());
        double tMaxZ = safeDivide(nextZ - origin.getZ(), dir.getZ());

        double tDeltaX = voxelSizeX / Math.abs(dir.getX());
        double tDeltaY = voxelSizeY / Math.abs(dir.getY());
        double tDeltaZ = voxelSizeZ / Math.abs(dir.getZ());

        Intersection closest = null;
        double closestDist = Double.POSITIVE_INFINITY;
        Set<Intersectable> tested = new HashSet<>();

        while (ix >= 0 && ix < nx && iy >= 0 && iy < ny && iz >= 0 && iz < nz) {
            VoxelIndex index = new VoxelIndex(ix, iy, iz);
            List<Intersectable> cell = grid.get(index);

            if (cell != null) {
                for (Intersectable obj : cell) {
                    if (tested.add(obj)) { // Only test each object once
                        List<Intersection> hits = obj.calculateIntersections(ray);
                        if (hits != null) {
                            for (Intersection inter : hits) {
                                double dist = origin.distance(inter.point);
                                if (dist < closestDist) {
                                    closest = inter;
                                    closestDist = dist;
                                }
                            }
                        }
                    }
                }
            }

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    ix += stepX;
                    tMaxX += tDeltaX;
                } else {
                    iz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    iy += stepY;
                    tMaxY += tDeltaY;
                } else {
                    iz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }

        return closest;
    }

    /**
     * Finds all intersections between the ray and geometries within the given maximum distance.
     *
     * @param ray         the ray to test
     * @param maxDistance maximum distance from ray origin to consider
     * @return a list of intersections, or an empty list if none found
     */
    public List<Intersection> findAllIntersections(Ray ray, double maxDistance) {
        List<Intersection> allHits = new LinkedList<>();

        if (!bounds.hasIntersection(ray)) return allHits;

        Point origin = ray.getHead();
        Vector dir = ray.getDirection();

        int ix = clamp(toGridX(origin.getX()), 0, nx - 1);
        int iy = clamp(toGridY(origin.getY()), 0, ny - 1);
        int iz = clamp(toGridZ(origin.getZ()), 0, nz - 1);

        int stepX = dir.getX() >= 0 ? 1 : -1;
        int stepY = dir.getY() >= 0 ? 1 : -1;
        int stepZ = dir.getZ() >= 0 ? 1 : -1;

        double nextX = voxelBoundary(bounds.getMin().getX(), ix, stepX, voxelSizeX);
        double nextY = voxelBoundary(bounds.getMin().getY(), iy, stepY, voxelSizeY);
        double nextZ = voxelBoundary(bounds.getMin().getZ(), iz, stepZ, voxelSizeZ);

        double tMaxX = safeDivide(nextX - origin.getX(), dir.getX());
        double tMaxY = safeDivide(nextY - origin.getY(), dir.getY());
        double tMaxZ = safeDivide(nextZ - origin.getZ(), dir.getZ());

        double tDeltaX = voxelSizeX / Math.abs(dir.getX());
        double tDeltaY = voxelSizeY / Math.abs(dir.getY());
        double tDeltaZ = voxelSizeZ / Math.abs(dir.getZ());

        Set<Intersectable> tested = new HashSet<>();

        while (ix >= 0 && ix < nx && iy >= 0 && iy < ny && iz >= 0 && iz < nz) {
            VoxelIndex index = new VoxelIndex(ix, iy, iz);
            List<Intersectable> cell = grid.get(index);

            if (cell != null) {
                for (Intersectable obj : cell) {
                    if (tested.add(obj)) {
                        List<Intersection> hits = obj.calculateIntersections(ray, maxDistance);
                        if (hits != null) {
                            for (Intersection inter : hits) {
                                if (origin.distance(inter.point) <= maxDistance) {
                                    allHits.add(inter);
                                }
                            }
                        }
                    }
                }
            }

            double nextT = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));
            if (nextT > maxDistance) break;

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    ix += stepX;
                    tMaxX += tDeltaX;
                } else {
                    iz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    iy += stepY;
                    tMaxY += tDeltaY;
                } else {
                    iz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }

        return allHits;
    }

    /**
     * Converts a world coordinate to a grid index along the X-axis.
     * @param x the world coordinate along the X-axis
     * @return the grid index corresponding to the X coordinate
     */
    private int toGridX(double x) {
        return (int) ((x - bounds.getMin().getX()) / voxelSizeX);
    }
    /**
     * Converts a world coordinate to a grid index along the Y-axis.
     * @param y the world coordinate along the Y-axis
     * @return the grid index corresponding to the Y coordinate
     */
    private int toGridY(double y) {
        return (int) ((y - bounds.getMin().getY()) / voxelSizeY);
    }
    /**
     * Converts a world coordinate to a grid index along the Z-axis.
     * @param z the world coordinate along the Z-axis
     * @return the grid index corresponding to the Z coordinate
     */
    private int toGridZ(double z) {
        return (int) ((z - bounds.getMin().getZ()) / voxelSizeZ);
    }
    /**
     * Clamps a value to be within the specified range.
     *
     * @param value the value to clamp
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @return the clamped value
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    /**
     * Calculates the boundary of a voxel based on its index and size.
     *
     * @param start the starting coordinate of the bounding box
     * @param index the index of the voxel along the axis
     * @param step  the step direction (1 for positive, -1 for negative)
     * @param size  the size of each voxel along the axis
     * @return the boundary coordinate of the voxel
     */
    private double voxelBoundary(double start, int index, int step, double size) {
        return start + (step > 0 ? (index + 1) : index) * size;
    }
    /**
     * Safely divides two numbers, returning positive infinity if the denominator is too small.
     *
     * @param numerator   the numerator of the division
     * @param denominator the denominator of the division
     * @return the result of the division or positive infinity if the denominator is too small
     */
    private double safeDivide(double numerator, double denominator) {
        return Math.abs(denominator) < 1e-10 ? Double.POSITIVE_INFINITY : numerator / denominator;
    }
}
