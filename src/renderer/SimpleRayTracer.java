package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;
import geometries.Intersectable. Intersection;
import java.util.List;
/**
 * SimpleRayTracer is a basic implementation of a ray tracer that calculates the color
 * at a given point in the scene based on the ambient light.
 */
public class SimpleRayTracer extends RayTracerBase {
    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to be rendered
     */
    SimpleRayTracer(Scene scene){
        super(scene);
    }
    /**
     * Calculates the color at a given point in the scene.
     *
     * @param p the point to calculate the color for
     * @return the color at the given point
     */
    private Color calcColor(Intersection p){
        return scene.ambientLight.getIntensity().scale( p.geometry.getMaterial().Ka)
                .add(p.geometry.getEmission());
    }
    /**
     * Traces a ray through the scene and returns the color at the intersection point.
     *
     * @param ray the ray to trace
     * @return the color at the intersection point
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return scene.background;
        } else {
            Intersection closestPoint = ray.findClosestIntersection(intersections);
            return calcColor(closestPoint);
        }
    }
}
