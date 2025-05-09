package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * The DirectionalLight class represents a directional light source in a 3D
 * scene. It extends the Light class and implements the LightSource interface.
 * The light has a specific direction and intensity.
 */
public class DirectionalLight extends Light implements LightSource {
    private Vector direction;

    /**
     * Constructor for the DirectionalLight class.
     *
     * @param intensity the intensity of the light
     * @param direction the direction of the light
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    /**
     * @param p
     * @return
     */
    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    /**
     * @param p
     * @return
     */
    @Override
    public Vector getL(Point p) {
        return direction;
    }
}
