package lighting;

import primitives.Color;

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
