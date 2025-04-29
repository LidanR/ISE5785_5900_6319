package renderer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import primitives.Color;
import primitives.Ray;
import scene.JsonScene;
import scene.Scene;

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
