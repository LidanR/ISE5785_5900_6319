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
    protected double radius = 1.0d;
    /// The attenuation coefficients
    private double kc=1.0;
    private double kl=0.0;
    private double kq=0.0;

    /**
     * Default constructor for the PointLight class.
     * @param color the color of the light
     * @param position the position of the light source
     */
     public PointLight(Color color, Point position) {
        this(color, position, 1.0);
     }
    public PointLight(Color color, Point position,double radius) {
        super(color);
        this.radius = radius < 0 ? 0.0d : radius;
        this.position = position;
    }

    /**
     * setter for the kc
     * @param kc the constant attenuation coefficient
     * @return the light source
     */
    public PointLight setKc(double kc) {
        this.kc = kc;
        return this;
    }
    /**
     * setter for the kl
     * @param kl the linear attenuation coefficient
     * @return the light source
     */
    public PointLight setKl(double kl) {
        this.kl = kl;
        return this;
    }
    /**
     * setter for the kq
     * @param kq the quadratic attenuation coefficient
     * @return the light source
     */
    public PointLight setKq(double kq) {
        this.kq = kq;
        return this;
    }

    /**
     * calculates the intensity of the light at a given point.
     * @param point the point at which the intensity is calculated
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
     * @param point the point at which the vector is calculated
     * @return the vector from the light source to the given point
     */
    @Override
    public Vector getL(Point point) {
        return point.subtract(position).normalize();
    }

    /**
     * @param point the point to which the distance is calculated
     * @return the distance from the light source to the point
     */
    @Override
    public double getDistance(Point point) {
        return position.distance(point);
    }
    /**
     * @return the radius of the light source
     */
    @Override
    public double getRadius() {
        return radius;
    }

}
