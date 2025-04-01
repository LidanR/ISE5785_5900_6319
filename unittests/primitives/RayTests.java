package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RayTests {
    /**
     * Test method for {@link primitives.Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        Ray r = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for getPoint positive distance
        assertEquals(new Point(2, 2, 3), r.getPoint(1), "Simple test for getPoint positive distance");
        // TC02: Simple test for getPoint negative distance
        assertEquals(new Point(0, 2, 3), r.getPoint(-1), "Simple test for getPoint negative distance");
        // ============ Boundary Value Tests ==================
        // TC03: Boundary test for getPoint with zero distance
        assertEquals(new Point(1, 2, 3), r.getPoint(0), "Boundary test for getPoint with zero distance");
    }
}