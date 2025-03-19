package geometries;

public abstract class RadialGeometry extends Geometry {
    private final double radius;

    public RadialGeometry(double radius) {
        this.radius = radius;
    }
}
