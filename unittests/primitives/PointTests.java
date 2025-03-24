package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTests {


    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for add
        Point p1 = new Point(1, 2, 3);
        Vector v1 = new Vector(1, 1, 1);
        Point p2 = p1.add(v1);
        assertEquals(new Point(2, 3, 4), p2, "Simple test for add");

    }

    @Test
    void testSubtract() {
    }

    @Test
    void testDistanceSquared() {
    }

    @Test
    void testDistance() {
    }
}