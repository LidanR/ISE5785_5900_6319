package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTests {

    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for getNormal
        Triangle t = new Triangle(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        assertEquals(new Vector(0, 0, 1), t.getNormal(new Point(0, 0, 0)), "Simple test for getNormal");
    }
}