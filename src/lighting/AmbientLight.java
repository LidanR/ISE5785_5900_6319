package lighting;

import primitives.Color;
/**
 * class for ambient light
 * <p>
 * Ambient light is a type of light that is scattered in all directions and does not have a specific source.
 * It is used to simulate the effect of indirect lighting in a scene.
 * </p>
 */
public class AmbientLight extends Light {
    /// The intensity of the ambient light
    /// A constant representing no ambient light
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);
    /**
     * A constant representing a default ambient light with intensity of 255
     * for each color channel
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }



}
