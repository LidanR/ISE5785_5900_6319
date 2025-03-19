package primitives;

public class Vector extends Point {
    public Vector(Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector cannot be the zero vector");
        }
    }

    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector cannot be the zero vector");
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Vector add(Vector v) {
        return new Vector(xyz.add(v.xyz));
    }

    public double lengthSquared() {
        return xyz.d1() * xyz.d1() + xyz.d2() * xyz.d2() + xyz.d3() * xyz.d3();
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector scale(double scalar) {
        return new Vector(xyz.scale(scalar));
    }

    public Double dotProduct(Vector v) {
        return xyz.d1() * v.xyz.d1() + xyz.d2() * v.xyz.d2() + xyz.d3() * v.xyz.d3();
    }

    public Vector crossProduct(Vector v) {
        return new Vector(new Double3(
                xyz.d2() * v.xyz.d3() - xyz.d3() * v.xyz.d2(),
                xyz.d3() * v.xyz.d1() - xyz.d1() * v.xyz.d3(),
                xyz.d1() * v.xyz.d2() - xyz.d2() * v.xyz.d1()));
    }

    public Vector normalize() {
        return scale(1 / length());
    }
}
