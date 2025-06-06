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
    private final Vector direction; // The direction of the light (final but may be changed in the future)

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
     * @param p the point at which to get the intensity
     * @return the intensity of the light at the given point
     */
    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    /**
     * @param p the point at which to get the direction
     * @return the direction of the light at the given point
     */
    @Override
    public Vector getL(Point p) {
        return direction;
    }

    /**
     * @param point the point to which the distance is calculated
     * @return the distance from the light source to the point
     */
    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }
    /**
     * @return the radius of the light source, which is not applicable for directional light
     */
    @Override
    public double getRadius() {
        return 1.0d; // Directional light does not have a radius
    }
}
