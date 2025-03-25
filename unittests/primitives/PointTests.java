package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTests {
    Point p1 = new Point(1, 2, 3);
    Point p2 = new Point(2, 3, 4);
    Vector v1 = new Vector(1, 1, 1);
    Vector v2 = new Vector(-1, -2, -3);

    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for add
        assertEquals(new Point(2, 3, 4), p1.add(v1), "Simple test for add");

        // ============ Boundary Value Tests ==================
        // TC02: Adding the opposite vector to a point should return ZeroPoint
        assertEquals(Point.ZERO, p1.add(v2), "Simple test for add");
    }

    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for subtract
        assertEquals(new Vector(1, 1, 1), p2.subtract(p1), "Simple test for subtract");

        // ============ Boundary Value Tests ==================
        // TC02: Subtracting a point from itself should return the zero vector
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1), "Boundary test for subtract");
    }

    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for distanceSquared
        double d = p1.distanceSquared(p2);
        assertEquals(3, d, "Simple test for distanceSquared");

        // ============ Boundary Value Tests ==================
        // TC02: BVA test for distanceSquared
        assertEquals(0, p1.distanceSquared(p1), "Squared distance between the same point should be 0");
    }

    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for distance
        assertEquals(Math.sqrt(3), p1.distance(p2), "Simple test for distance");
    }
}