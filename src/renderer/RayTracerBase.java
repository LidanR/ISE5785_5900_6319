package renderer;

import lighting.LightSource;
import primitives.*;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

/**
 * Abstract base class for ray tracers that contains shared logic.
 */
public abstract class RayTracerBase {
    protected static final int MAX_CALC_COLOR_LEVEL = 4;
    protected static final double MIN_CALC_COLOR_K = 0.001;
    protected static final Double3 INITIAL_K = Double3.ONE;
    protected static final double SIZEOFGLOSSYANDBLURRY = 1;

    protected final Scene scene;
    protected Blackboard blackboard = Blackboard.getBuilder().build();

    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    public abstract Color traceRay(Ray ray);
    protected abstract Intersection findClosestIntersection(Ray ray);
    protected abstract Double3 transparency(Intersection intersection);

    protected Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection())) return Color.BLACK;
        Color base = calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);
        return base.add(scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial().Ka));
    }

    protected Color calcColor(Intersection intersection, int level, Double3 k) {
        Color local = calcColorLocalEffects(intersection, k);
        return level == 1 ? local : local.add(calcGlobalEffects(intersection, level, k));
    }

    protected boolean preprocessIntersection(Intersection intersection, Vector dir) {
        intersection.v = dir;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = Util.alignZero(intersection.normal.dotProduct(dir));
        return intersection.vNormal != 0;
    }

    protected boolean setLightSource(Intersection intersection, LightSource light, Vector lDir) {
        intersection.light = light;
        intersection.l = lDir;
        intersection.lNormal = Util.alignZero(intersection.normal.dotProduct(lDir));
        return intersection.lNormal != 0;
    }

    protected Double3 calcDiffuse(Intersection intersection) {
        return intersection.material.Kd.scale(Math.abs(intersection.lNormal));
    }

    protected Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal));
        double vr = Util.alignZero(intersection.v.scale(-1).dotProduct(r));
        return vr <= 0 ? Double3.ZERO : intersection.material.Ks.scale(Math.pow(vr, intersection.material.nSh));
    }

    protected Ray constructReflectedRay(Intersection intersection) {
        Vector r = intersection.v.subtract(intersection.normal.scale(2 * intersection.vNormal)).normalize();
        return new Ray(intersection.point, r, intersection.normal);
    }

    protected Ray constructRefractedRay(Intersection intersection) {
        return new Ray(intersection.point, intersection.v, intersection.normal);
    }

    protected Color calcColorLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();
        if (intersection.vNormal == 0) return color;

        for (LightSource light : scene.lights) {
            Vector l = light.getL(intersection.point);
            if (!setLightSource(intersection, light, l)) continue;

            List<Ray> shadowRays = List.of(new Ray(intersection.point, l.scale(-1)));
            if (blackboard.useSoftShadows()) {
                double dist = light.getDistance(intersection.point);
                shadowRays = blackboard.constructRays(new Ray(intersection.point, l.scale(-1)), dist, light.getRadius());
            }

            for (Ray sRay : shadowRays) {
                if (!setLightSource(intersection, light, sRay.getDirection().scale(-1)) ||
                        intersection.lNormal * intersection.vNormal <= 0) continue;

                Double3 ktr = transparency(intersection).reduce(shadowRays.size());
                if (!ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
                    Color iL = light.getIntensity(intersection.point).scale(ktr);
                    color = color.add(iL.scale(calcDiffuse(intersection).add(calcSpecular(intersection))));
                }
            }
        }
        return color;
    }

    protected Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Material mat = intersection.geometry.getMaterial();
        return calcGlobalEffect(intersection, constructRefractedRay(intersection), mat.Kt, level, k)
                .add(calcGlobalEffect(intersection, constructReflectedRay(intersection), mat.Kr, level, k));
    }

    protected Color calcGlobalEffect(Intersection inter, Ray ray, Double3 kx, int level, Double3 k) {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Color color = Color.BLACK;
        List<Ray> rays = List.of(ray);

        if (blackboard.useBlurryAndGlossy() && inter.material.strength > 0) {
            double alpha = inter.material.strength;
            double radius = Math.tan(Math.toRadians(alpha));
            rays = blackboard.constructRays(ray, SIZEOFGLOSSYANDBLURRY, radius);

            if (kx == inter.material.Kr) {
                rays.removeIf(r -> inter.normal.dotProduct(r.getDirection()) <= 0);
            }
        }

        for (Ray r : rays) {
            Intersection hit = findClosestIntersection(r);
            if (hit == null) {
                color = color.add(scene.background);
            } else if (preprocessIntersection(hit, r.getDirection())) {
                color = color.add(calcColor(hit, level - 1, kkx));
            }
        }
        if(rays.isEmpty())
            return color;
        return color.reduce(rays.size()).scale(kx);
    }
}
