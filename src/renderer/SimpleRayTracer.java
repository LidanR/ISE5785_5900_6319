package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

public class SimpleRayTracer extends RayTracerBase {
    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to be rendered
     */
    SimpleRayTracer(Scene scene){
        super(scene);
    }

    private Color calcColor(Point p){
        return scene.ambientLight.getIntensity();
    }

    @Override
    public Color traceRay(Ray ray) {
        List<Point> intersections = scene.geometries.findIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return scene.ambientLight.getIntensity();
        } else {
            Point closestPoint = ray.findClosestPoint(intersections);
            return calcColor(closestPoint);
        }
    }
}
