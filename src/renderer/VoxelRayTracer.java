package renderer;

import acceleration.AABB;
import acceleration.VoxelGrid;
import geometries.Intersectable;
import geometries.Intersectable.Intersection;
import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;

/**
 * A ray tracer that uses a voxel grid for spatial acceleration.
 * This class optimizes ray tracing by dividing the scene into a grid of voxels,
 * allowing efficient intersection calculations for bounded and unbounded geometries.
 */
public class VoxelRayTracer extends RayTracerBase {

    private static final int VOXELS_PER_OBJECT = 4; // Number of voxels per object for optimal grid size calculation
    private final VoxelGrid voxelGrid; // The voxel grid used for spatial acceleration
    private final List<Intersectable> unboundedGeometries; // List of geometries without bounding boxes

    /**
     * Constructs a VoxelRayTracer with the given scene and default blackboard settings.
     *
     * @param scene The scene to be rendered.
     */
    public VoxelRayTracer(Scene scene) {
        this(scene, Blackboard.getBuilder().build());
    }

    /**
     * Constructs a VoxelRayTracer with the given scene and blackboard settings.
     *
     * @param scene     The scene to be rendered.
     * @param blackboard The blackboard settings for rendering.
     */
    public VoxelRayTracer(Scene scene, Blackboard blackboard) {
        super(scene);
        this.blackboard = blackboard;

        // Calculate optimal grid size based on the number of objects in the scene
        int objectCount = scene.geometries.getGeomitriesSize();
        int optimalGridSize = (int) Math.cbrt(objectCount * VOXELS_PER_OBJECT);

        // Create the voxel grid based on the scene's bounding box
        AABB sceneBounds = scene.geometries.getAABB();
        this.voxelGrid = new VoxelGrid(sceneBounds, optimalGridSize, optimalGridSize, optimalGridSize);

        // Separate bounded and unbounded geometries
        this.unboundedGeometries = new LinkedList<>();
        for (Intersectable geometry : scene.geometries.getGeometries()) {
            AABB aabb = geometry.getAABB();
            if (aabb == null) {
                unboundedGeometries.add(geometry);
            } else {
                voxelGrid.addObject(geometry, aabb);
            }
        }
    }

    /**
     * Traces a ray and calculates its color based on intersections with the scene.
     *
     * @param ray The ray to be traced.
     * @return The color of the ray based on intersections or the background color if no intersection is found.
     */
    @Override
    public Color traceRay(Ray ray) {
        Intersection intersection = findClosestIntersection(ray);
        return intersection == null ? scene.background : calcColor(intersection, ray);
    }

    /**
     * Finds the closest intersection of a ray with the scene's geometries.
     *
     * @param ray The ray to find intersections for.
     * @return The closest intersection or null if no intersection is found.
     */
    @Override
    protected Intersection findClosestIntersection(Ray ray) {
        Intersection voxelHit = voxelGrid.findClosestIntersection(ray);
        Intersection unboundedHit = findClosestUnbounded(ray);

        if (voxelHit == null) return unboundedHit;
        if (unboundedHit == null) return voxelHit;

        // Compare distances to determine the closest intersection
        double distVoxel = ray.getHead().distanceSquared(voxelHit.point);
        double distUnbounded = ray.getHead().distanceSquared(unboundedHit.point);
        return distVoxel < distUnbounded ? voxelHit : unboundedHit;
    }

    /**
     * Finds the closest intersection of a ray with unbounded geometries.
     *
     * @param ray The ray to find intersections for.
     * @return The closest intersection or null if no intersection is found.
     */
    private Intersection findClosestUnbounded(Ray ray) {
        Intersection closest = null;
        double minDist = Double.POSITIVE_INFINITY;

        for (Intersectable g : unboundedGeometries) {
            List<Intersection> hits = g.calculateIntersections(ray);
            if (hits != null) {
                for (Intersection i : hits) {
                    double dist = ray.getHead().distance(i.point);
                    if (dist < minDist) {
                        minDist = dist;
                        closest = i;
                    }
                }
            }
        }
        return closest;
    }

    /**
     * Calculates the transparency factor for a given intersection.
     *
     * @param intersection The intersection to calculate transparency for.
     * @return The transparency factor as a Double3 object.
     */
    @Override
    protected Double3 transparency(Intersection intersection) {
        Vector ld = intersection.l.scale(-1); // Light direction
        Ray tRay = new Ray(intersection.point, ld, intersection.normal); // Transparency ray
        double maxDist = intersection.light.getDistance(tRay.getHead()); // Maximum distance to the light source

        // Find all intersections along the transparency ray
        List<Intersection> intersections = voxelGrid.findAllIntersections(tRay, maxDist);
        if (intersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE; // Transparency factor
        for (Intersection inter : intersections) {
            ktr = ktr.product(inter.material.Kt); // Accumulate transparency factors
            if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO; // Stop if transparency is negligible
        }
        return ktr;
    }
}