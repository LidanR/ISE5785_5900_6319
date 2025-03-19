package primitives;

public class Ray {
    private final Point head;
    private final Vector direction;

    public Ray(Point p, Vector v) {
        this.head = p;
        this.direction = v.normalize();
    }

    @Override
    public String toString() {
        return "head: " + head + ", direction: " + direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other) && head.equals(other.head) && direction.equals(other.direction);
    }
}
