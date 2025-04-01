package geometries;

import org.junit.jupiter.api.Test;
import primitives.Ray;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;
class TubeTests {
    /**
     * Test method for {@link geometries.Tube#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for getNormal
        Tube t = new Tube(1, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))); // שינוי הצינור כך שיהיה בכיוון יציב (ציר Z)

        assertEquals(new Vector(1, 0, 0),
                t.getNormal(new Point(1, 0, 2)),
                "Simple test for getNormal");

        // ============ Boundary Value Tests ==================
        // TC02: BVA test for getNormal (avoid zero vector)
        assertThrows(IllegalArgumentException.class,
                () -> t.getNormal(new Point(0, 0, 2)),
                "Boundary test for getNormal");
    }
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==================

        //========== Boundary Value Tests  ==================

    }

}