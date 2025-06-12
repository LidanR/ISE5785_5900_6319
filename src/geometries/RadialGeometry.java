package geometries;

/**
 * The `RadialGeometry` class is an abstract base class for all radial geometric objects.
 * It extends the `Geometry` class and includes a radius attribute.
 */
public abstract class RadialGeometry extends Geometry {
    protected final double radius;

    /**
     * Constructs a new `RadialGeometry` with the specified radius.
     *
     * @param radius the radius of the radial geometry
     */
    public RadialGeometry(double radius) {
        if(radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        this.radius = radius;
    }
    /**
     * Returns the radius of the radial geometry.
     *
     * @return the radius of the radial geometry
     */
    public double getRadius() {
        return radius;
    }
}
