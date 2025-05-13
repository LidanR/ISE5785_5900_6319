package renderer;

import lighting.LightSource;
import primitives.*;
import scene.Scene;
import geometries.Intersectable. Intersection;
import java.util.List;
/**
 * SimpleRayTracer is a basic implementation of a ray tracer that calculates the color
 * at a given point in the scene based on the ambient light.
 */
public class SimpleRayTracer extends RayTracerBase {
    /**
     * A small value used to avoid shadow acne.
     */
    private static final double DELTA = 0.1;
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
     * @param intersection the point to calculate the color for
     * @param ray the ray that intersects the point
     * @return the color at the given point
     */
    private Color calcColor(Intersection intersection,Ray ray){
        if (!preprocessIntersection(intersection, ray.getDir())) {
            return Color.BLACK;
        }
        return scene.ambientLight.getIntensity().scale( intersection.geometry.getMaterial().Ka)
                .add(calcColorLocalEffects(intersection));
    }
    /**
     * Preprocesses the intersection point to calculate the normal and ray direction.
     *
     * @param intersection the intersection point
     * @param direction the direction of the ray
     * @return true if the intersection is valid, false otherwise
     */
    private boolean preprocessIntersection(Intersection intersection, Vector direction){
        intersection.v = direction;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = Util.alignZero(intersection.normal.dotProduct(direction));
        return intersection.vNormal != 0;
    }
    /**
     * Sets the light source for the intersection point.
     *
     * @param intersection the intersection point
     * @param lightSource the light source to set
     * @return true if the light source is valid, false otherwise
     */
    private boolean setLightSource(Intersection intersection, LightSource lightSource){
        intersection.light = lightSource;
        intersection.l = lightSource.getL(intersection.point);
        intersection.lNormal = Util.alignZero(intersection.normal.dotProduct(intersection.l));
        return intersection.lNormal != 0;

    }
    /**
     * Calculates the color at a given intersection point based on local effects.
     *
     * @param intersection the intersection point
     * @return the color at the intersection point
     */
    private Color calcColorLocalEffects(Intersection intersection)
    {
        Color color = intersection.geometry.getEmission();

        if (intersection.vNormal == 0) return color;
        for(LightSource lightSource : scene.lights)
        {
            if (!setLightSource(intersection, lightSource)) continue;

            if ((intersection.lNormal * intersection.vNormal > 0) //sign(nl)== sign(vl)
             &unshaded(intersection))  // check if the point is in shadow
            {
                Color iL = lightSource.getIntensity(intersection.point);
                color = color.add(iL.scale(calcSpecular(intersection)))
                        .add(iL.scale(calcDiffuse(intersection)));
            }
        }
        return color;
    }
    /**
     * Calculates the specular reflection at a given intersection point.
     *
     * @param intersection the intersection point
     * @return the specular reflection color
     */
    private Double3 calcSpecular(Intersection intersection)
    {
        Vector r = intersection.l.subtract(intersection.normal.scale(intersection.lNormal).scale(2d));
        double vr = Util.alignZero(intersection.v.scale(-1).dotProduct(r));
        if (vr <= 0) return Double3.ZERO;

        return intersection.material.Ks.scale(Math.pow(vr, intersection.material.nSh));
    }
    /**
     * Calculates the diffuse reflection at a given intersection point.
     *
     * @param intersection the intersection point
     * @return the diffuse reflection color
     */
    private Double3 calcDiffuse(Intersection intersection)
    {
        return intersection.material.Kd.scale(Math.abs(intersection.lNormal));
    }
    /**
     * Calculates the shadow intensity at a given intersection point.
     *
     * @param intersection the intersection point
     * @return the shadow intensity
     */
    private boolean unshaded(Intersection intersection)
    {
        Vector lightDirection = intersection.l.scale(-1);
        Vector deltaVector = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Point point = intersection.point.add(deltaVector);
        Ray lightRay = new Ray(point, lightDirection);
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay);
        if(intersections == null) return true;
        double distance = intersection.light.getDistance(point);
        for (Intersection i : intersections) {
            if (i.point.distance(point)<distance) return false;
        }
        return true;
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
        if (intersections == null) {
            return scene.background;
        } else {
            Intersection closestPoint = ray.findClosestIntersection(intersections);
            return calcColor(closestPoint, ray);
        }
    }
}
