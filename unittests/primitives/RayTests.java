package primitives;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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

    @Test
    void testFindClosestPoint() {
        Ray r = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));
        Point p1 = new Point(-1, 2, 3);
        Point p2 = new Point(2, 2, 3);
        Point p3 = new Point(0, 2, 6);

        // ============ Equivalence Partitions Tests ==================
        // TC01: The closest point is in the middle
        assertEquals(p2, r.findClosestPoint(Arrays.asList(p1, p2, p3)), "Simple test for findClosestPoint - in the middle");
        // ============ Boundary Value Tests ==================
        // TC02: Empty list
        assertEquals(null, r.findClosestPoint(null), "Boundary test for findClosestPoint with same points - empty list");
        // TC03: First point in the list is the closest one
        assertEquals(p2, r.findClosestPoint(Arrays.asList(p2, p1, p3)), "Boundary test for findClosestPoint with same points - first point");
        // TC04: Last point in the list is the closest one
        assertEquals(p2, r.findClosestPoint(Arrays.asList(p1, p3, p2)), "Boundary test for findClosestPoint with same points - last point");
    }
}