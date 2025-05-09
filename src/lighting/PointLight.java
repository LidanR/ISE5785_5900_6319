package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;
/**
 * The PointLight class represents a point light source in 3D space.
 * It extends the Light class and implements the LightSource interface.
 * The light source has a position and attenuation coefficients (kc, kl, kq).
 */
public class PointLight extends Light implements LightSource {
    /// The position of the point light source
    protected Point position;
    /// The attenuation coefficients
    private double kc=1.0;
    private double kl=0.0;
    private double kq=0.0;

    /**
     * Default constructor for the PointLight class.
     * @param color
     * @param position
     */
     public PointLight(Color color, Point position) {
     super(color);
     this.position = position;
     }
    // Setters to kc, kl, kq
    public PointLight setKc(double kc) {
        this.kc = kc;
        return this;
    }
    public PointLight setKl(double kl) {
        this.kl = kl;
        return this;
    }
    public PointLight setKq(double kq) {
        this.kq = kq;
        return this;
    }

    /**
     * calculates the intensity of the light at a given point.
     * @param point
     * @return the intensity of the light at the given point
     */
    @Override
    public Color getIntensity(Point point) {
        double d = position.distance(point);
        double factor = kc + kl * d + kq * d * d;
        if(Util.isZero(factor))
            return intensity.scale(Double.POSITIVE_INFINITY);

        return intensity.scale(1d/factor);
    }

    /**
     * calculates the vector from the light source to a given point.
     * @param point
     * @return the vector from the light source to the given point
     */
    @Override
    public Vector getL(Point point) {
        return point.subtract(position).normalize();
    }

}
