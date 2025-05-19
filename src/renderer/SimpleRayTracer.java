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
     * The maximum number of color levels to calculate.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * The minimum k value for color calculations.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * the k value for the initial color.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

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
                .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Calculates the color at a given intersection point based on the level and k value.
     *
     * @param intersection the intersection point
     * @param level the recursion level
     * @param k the k value for color calculations
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection, int level, Double3 k)
    {
        Color color = scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().Ka)
                .add(calcColorLocalEffects(intersection));
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
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
        if(intersections == null) return true; // no intersection with any geometry
        // Check if the light source is blocked by any other geometry
        double distance = intersection.light.getDistance(point);
        for (Intersection i : intersections) {
            if (i.geometry.getMaterial().Kt.lowerThan(MIN_CALC_COLOR_K)) continue;
            if (i.point.distance(point)<distance) return false;
        }
        return true;
    }
    /**
     * Constructs a reflected ray at a given intersection point.
     *
     * @param intersection the intersection point
     * @return the reflected ray
     */
    public Ray constructReflectedRay(Intersection intersection)
    {
        Point p0 = intersection.point.add(intersection.normal.scale(DELTA));
        Vector r=intersection.v.subtract(intersection.normal.scale(intersection.lNormal).scale(2d));
        return new Ray(p0,r);
    }
    /**
     * constructs a refracted ray at a given intersection point.
     *
     * @param intersection the intersection point
     */
    public Ray constructRefractedRay(Intersection intersection)
    {
        Point p0= intersection.point.add(intersection.normal.scale(-DELTA));
        return new Ray(p0,intersection.v);
    }

    private Color calcGlobalEffect(Ray ray,int level,Double3 k,Double3 kx)
    {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;
        Intersection intersection = findClosestIntersection(ray);
        return (intersection == null) ? scene.background : calcColor(intersection, level - 1, kkx);
    }

    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k)
    {
       Material material = intersection.geometry.getMaterial();
       return calcGlobalEffect(constructReflectedRay(intersection),level,material.Kt, k)
               .add(calcGlobalEffect(constructRefractedRay(intersection),level,material.Kt , k));
    }


    private Intersection findClosestIntersection(Ray ray)
    {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null) return null;
        return ray.findClosestIntersection(intersections);
    }

    /**
     * Traces a ray through the scene and returns the color at the intersection point.
     *
     * @param ray the ray to trace
     * @return the color at the intersection point
     */
    @Override
    public Color traceRay(Ray ray) {
        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) {
            return scene.background;
        } else {
            return calcColor(intersection, ray);
        }
    }
}
