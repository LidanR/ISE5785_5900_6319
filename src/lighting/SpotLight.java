package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
/**
 * The SpotLight class represents a spotlight in a 3D scene.
 * It extends the PointLight class and adds functionality for directionality.
 * The spotlight emits light in a specific direction, creating a cone of light.
 */
public class SpotLight extends PointLight {
    // Direction of the spotlight beam
    private final Vector direction;
    // The angle of the spotlight beam
    private Double narrowBeam = 1d;


    /**
     * Constructor for the SpotLight class.
     *
     * @param color     The color of the light
     * @param direction The direction of the spotlight beam
     * @param position  The position of the light source
     */
    public SpotLight(Color color, Point position, Vector direction) {
        super(color, position, 5.0);
        this.direction = direction.normalize();
    }

    /**
     * setter for the KC value
     * @param kC the attenuation factor
     * @return this the light source
     */
    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }
    /**
     * setter for the KL value
     * @param kL the attenuation factor
     * @return this
     */
    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }
    /**
     * setter for the KQ value
     * @param kQ the attenuation factor
     * @return this
     */
    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    /**
     * get intensity of the light at a specific point
     * @param point point in the scene
     * @return color of the light at the point
     */
    @Override
    public Color getIntensity(Point point) {
        Color superColor = super.getIntensity(point);
        if(narrowBeam != 1d)
            return superColor.scale(Math.pow(Math.max(0d, direction.dotProduct(getL(point))),narrowBeam));
        return superColor.scale(Math.max(0d, direction.dotProduct(getL(point))));
    }

    /**
     * set the narrow beam of the light
     * @param narrowBeam the narrow beam of the light
     * @return the light source
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

}
