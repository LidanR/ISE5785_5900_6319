package primitives;

public class Point {
    public static final Point ZERO = new Point(0, 0, 0);
    final protected Double3 xyz;

    public Point(double x, double y, double z) {
        xyz = new Double3(x, y, z);
    }

    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other) && xyz.equals(other.xyz);
    }

    @Override
    public String toString() { return xyz.toString(); }

    public Point add(Vector v) {
        return new Point(xyz.add(v.xyz));
    }

    public Vector subtract(Point p) {
        return new Vector(xyz.subtract(p.xyz));
    }

    public double distanceSquared(Point p) {
        double dx = xyz.d1() - p.xyz.d1();
        double dy = xyz.d2() - p.xyz.d2();
        double dz = xyz.d3() - p.xyz.d3();
        return dx * dx + dy * dy + dz * dz;
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }
}
