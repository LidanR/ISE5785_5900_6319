package renderer;

import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

/**
 * A basic ray tracer that uses no spatial acceleration.
 */
public class SimpleRayTracer extends RayTracerBase {

    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    public SimpleRayTracer(Scene scene, Blackboard blackboard) {
        super(scene);
        this.blackboard = blackboard;
    }

    @Override
    public Color traceRay(Ray ray) {
        Intersection intersection = findClosestIntersection(ray);
        return intersection == null ? scene.background : calcColor(intersection, ray);
    }

    @Override
    protected Intersection findClosestIntersection(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null) return null;
        return ray.findClosestIntersection(intersections);
    }

    @Override
    protected Double3 transparency(Intersection intersection) {
        Vector lightDirection = intersection.l.scale(-1);
        Ray lightRay = new Ray(intersection.point, lightDirection, intersection.normal);
        double distance = intersection.light.getDistance(lightRay.getHead());
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay, distance);
        if (intersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;
        for (Intersection i : intersections) {
            ktr = ktr.product(i.material.Kt);
            if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
        }
        return ktr;
    }
}
