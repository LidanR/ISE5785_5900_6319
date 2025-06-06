package lighting;

import primitives.Color;
import primitives.Util;

/**
 * Abstract class Light is the basic class representing a light source in the 3D space.
 * It contains the intensity of the light and provides a method to get that intensity.
 */
abstract class Light {
    /// The intensity of the light
    protected final Color intensity;

    /**
     * Default constructor for the Light class.
     * Initializes the intensity to black (0, 0, 0).
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of the light.
     *
     * @return the intensity of the light
     */
    public Color getIntensity() {
        return intensity;
    }

}
