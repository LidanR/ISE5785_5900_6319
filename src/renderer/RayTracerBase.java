package renderer;


import primitives.Color;
import primitives.Ray;
import scene.Scene;
/**
 * Abstract class for ray tracing.
 * <p>
 * This class serves as a base for implementing different ray tracing algorithms.
 * It contains the scene to be rendered and provides an abstract method for tracing rays.
 * </p>
 */
public abstract class RayTracerBase {
    /// The scene to be rendered
    protected final Scene scene;

    /**
     * Constructor for RayTracerBase.
     *
     * @param scene the scene to be rendered
     */
    RayTracerBase(Scene scene) {
        this.scene = scene;
    }



    /**
     * Traces a ray through the scene and returns the color at the intersection point.
     *
     * @param ray the ray to trace
     * @return the color at the intersection point
     */
    public abstract Color traceRay(Ray ray);
}
