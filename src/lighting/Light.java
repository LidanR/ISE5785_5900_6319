package lighting;

import primitives.Color;

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
