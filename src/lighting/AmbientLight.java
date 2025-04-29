package lighting;

import org.json.simple.JSONObject;
import primitives.Color;
import renderer.RayTracerType;

public class AmbientLight {
    /// The intensity of the ambient light
    private final Color intensity;
    /// A constant representing no ambient light
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK,1d);
    /**
     * A constant representing a default ambient light with intensity of 255
     * for each color channel
     */
    public AmbientLight(Color intensity,double ka) {
        this.intensity = intensity.scale(ka);
    }
    /**
     * A constant representing a default ambient light with intensity of 255
     * for each color channel
     */
    public Color getIntensity() {
        return intensity;
    }



}
