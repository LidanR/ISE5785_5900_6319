package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class SphereTests {

    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for getNormal
        Sphere s = new Sphere(new Point(0,0,0), 1);
        assertEquals(new Vector(1, 0, 0), s.getNormal(new Point(1, 0, 0)), "Simple test for getNormal");
    }
}