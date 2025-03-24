package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTests {
    Point p1 = new Point(1, 2, 3);
    Point p2 = new Point(2, 3, 4);
    Vector v1 = new Vector(1, 1, 1);

    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for add
        Point p2 = p1.add(v1);
        assertEquals(new Point(2, 3, 4), p2, "Simple test for add");

    }

    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for subtract
        Vector v1 = p2.subtract(p1);
        assertEquals(new Vector(1, 1, 1), v1, "Simple test for subtract");

        // TC02: Subtracting a point from itself should return the zero vector
        Vector v2 = p1.subtract(p1);
        assertEquals(new Vector(0, 0, 0), v2, "Subtracting a point from itself should return the zero vector");
    }

    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for distanceSquared
        double d = p1.distanceSquared(p2);
        assertEquals(3, d, "Simple test for distanceSquared");

        // TC02: BVA test for distanceSquared
        Point p3 = new Point(1, 2, 3);
        double d2 = p3.distanceSquared(p1);
        assertEquals(0, d2, "Squared distance between the same point should be 0");
    }

    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for distance
        double d = p1.distance(p2);
        assertEquals(Math.sqrt(3), d, "Simple test for distance");
    }
}