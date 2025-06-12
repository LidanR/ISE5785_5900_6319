package renderer;

import lighting.DirectionalLight;
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
     * The size of the glossy and blurry effect.
     */
    private static final double SIZEOFGLOSSYANDBLURRY = 1;
    /**
     * The blackboard used for additional parameters in ray tracing.
     */
    private Blackboard blackboard = Blackboard.getBuilder().build();

    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene to be rendered
     */
    SimpleRayTracer(Scene scene){
        super(scene);
    }
    /**
     * Constructor for SimpleRayTracer with a blackboard.
     *
     * @param scene the scene to be rendered
     * @param blackboard the blackboard to be used for ray tracing
     */
    SimpleRayTracer(Scene scene,Blackboard blackboard){
        super(scene);
        this.blackboard = blackboard;
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
        Color color = Color.BLACK;
        color = color.add(intersection==null?scene.background: calcColor(intersection, ray));
        return color;
    }
    /**
     * Calculates the color at a given point in the scene.
     *
     * @param intersection the point to calculate the color for
     * @param ray the ray that intersects the point
     * @return the color at the given point
     */
    private Color calcColor(Intersection intersection,Ray ray){
        if (!preprocessIntersection(intersection, ray.getDirection())) {
            return Color.BLACK;
        }
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(scene.ambientLight.getIntensity()
                        .scale(intersection.geometry.getMaterial().Ka));
    }

    /**
     * Calculates the color at a given intersection point based on the level and k value.
     *
     * @param intersection the intersection point
     * @param level the recursion level
     * @param k the k value for color calculations
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color localColor = calcColorLocalEffects(intersection,k);
        return 1 == level ? localColor : localColor.add(calcGlobalEffects(intersection, level, k));
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
    private boolean setLightSource(Intersection intersection, LightSource lightSource,Vector direction){
        intersection.light = lightSource;
        intersection.l = direction;
        intersection.lNormal = Util.alignZero(intersection.normal.dotProduct(intersection.l));
        return intersection.lNormal != 0;

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
     * Calculates the shadow intensity at a given intersection point.
     * note: this function is not used in the current implementation,
     *       it is replaced by the transparency function.
     *
     * @param intersection the intersection point
     * @return the shadow intensity
     */
    @SuppressWarnings("unused")
    private boolean unshaded(Intersection intersection)
    {
        Vector lightDirection = intersection.l.scale(-1);
        Ray lightRay = new Ray(intersection.point, lightDirection,intersection.normal);
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay);
        if(intersections == null) return true; // no intersection with any geometry
        // Check if the light source is blocked by any other geometry
        double distance = intersection.light.getDistance(lightRay.getHead());
        for (Intersection i : intersections) {
            if (i.point.distance(lightRay.getHead())<distance&&
                    i.material.Kt.lowerThan(MIN_CALC_COLOR_K)) return false;
        }
        return true;
    }
    /**
     * Calculates the color at a given intersection point based on local effects.
     *
     * @param intersection the intersection point
     * @return the color at the intersection point
     */
    private Color calcColorLocalEffects(Intersection intersection,Double3 k)
    {
        Color color = intersection.geometry.getEmission();
        if (intersection.vNormal == 0) return color;

        for(LightSource lightSource : scene.lights)
        {
            if(!setLightSource(intersection,lightSource,lightSource.getL(intersection.point))) continue;
            Ray centerRay = new Ray(intersection.point,lightSource.getL(intersection.point).scale(-1));
            Double3 Ktr;
            List<Ray> rays=List.of(centerRay);
            if(blackboard.useSoftShadows())
            {
                    double lightDistance = lightSource.getDistance(intersection.point);
                    rays = blackboard.constructRays(centerRay, lightDistance,lightSource.getRadius());
            }
            for(Ray ray : rays)
            {
                if(!setLightSource(intersection,lightSource,ray.getDirection().scale(-1))||
                        intersection.lNormal*intersection.vNormal <= 0) continue;
                Ktr = transparency(intersection).reduce(rays.size()); // Average of rays colors
                if (!Ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
                    Color iL = lightSource.getIntensity(intersection.point).scale(Ktr);
                    color = color.add(
                            iL.scale(calcDiffuse(intersection)
                                    .add(calcSpecular(intersection))));
                }
            }
        }
        return color;
    }
    /**
     * constructs a refracted ray at a given intersection point.
     *
     * @param intersection the intersection point
     */
    public Ray constructRefractedRay(Intersection intersection)
    {
        return new Ray(intersection.point,intersection.v,intersection.normal);
    }
    /**
     * Constructs a reflected ray at a given intersection point.
     *
     * @param intersection the intersection point
     * @return the reflected ray
     */
    public Ray constructReflectedRay(Intersection intersection)
    {
        Vector r=intersection.v.subtract(intersection.normal.scale(intersection.vNormal).scale(2d)).normalize();
        return new Ray(intersection.point,r,intersection.normal);
    }

    /**
     * Calculates the global effect of light on a given ray.
     * @param ray the ray to trace
     * @param kx the k value for color calculations
     * @param level the recursion level
     * @param k the k value for color calculations
     * @return the color at the intersection point
     */
   private Color calcGlobalEffect(Intersection intersectionOfRay, Ray ray, Double3 kx, int level, Double3 k)
    {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Color color = Color.BLACK;
        List<Ray> rays = List.of(ray);
        double alpha = intersectionOfRay.material.strength;
        double glossinessRadius =Util.alignZero(Math.tan(Math.toRadians(alpha)));
        // If global effects beam is enabled, generate multiple rays with the calculated radius
        if (blackboard.useBlurryAndGlossy()) {
            // Generate beam of rays for reflection/refraction
            rays = blackboard.constructRays(ray, SIZEOFGLOSSYANDBLURRY,glossinessRadius);
            // Filter out rays that are not valid for the intersection
            rays.removeIf(r -> intersectionOfRay.normal.dotProduct(r.getDirection()) <= 0);
            if( rays.isEmpty()) {
                return color; // No valid rays in the beam
            }
        }

        // Process each ray in the beam
        for (Ray r : rays) {
            // Find intersection for this specific ray
            Intersection intersection = findClosestIntersection(r);
            if (intersection == null) {
                color = color.add(scene.background);
                continue;
            }
            if (preprocessIntersection(intersection, r.getDirection())) {
                color = color.add(calcColor(intersection, level - 1, kkx));
            }
        }

        return color.reduce(rays.size()).scale(kx);
    }
    /**
     * Calculates the global effects of light on a given intersection point.
     *
     * @param intersection the intersection point
     * @param level the recursion level
     * @param k the k value for color calculations
     * @return the color at the intersection point
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k)
    {
        Material material = intersection.geometry.getMaterial();
        return calcGlobalEffect(intersection, constructRefractedRay(intersection),material.Kt,level , k)
                .add(calcGlobalEffect(intersection, constructReflectedRay(intersection),material.Kr,level, k));
    }

    /**
     * Finds the closest intersection point of a ray with the scene.
     *
     * @param ray the ray to trace
     * @return the closest intersection point, or null if there are no intersections
     */
    private Intersection findClosestIntersection(Ray ray)
    {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null) return null;
        return ray.findClosestIntersection(intersections);
    }
    /**
     * Calculates the transparency of a given intersection point.
     * replaces the unshaded function
     *
     * @param intersection the intersection point
     * @return the transparency value
     */
    private Double3 transparency(Intersection intersection)
    {
        Vector lightDirection = intersection.l.scale(-1);
        Ray lightRay = new Ray(intersection.point, lightDirection,intersection.normal);
        double distance = intersection.light.getDistance(lightRay.getHead());
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay,distance);
        if(intersections == null) return  Double3.ONE; // no intersection with any geometry
        // Check if the light source is blocked by any other geometry
        Double3 ktr = Double3.ONE;
        for (Intersection i : intersections) {
            ktr = ktr.product(i.material.Kt);
            if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
        }
        return ktr;
    }
}
