package geometries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import primitives.*;

class PlaneTests {

    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: Test that the normal is orthogonal to at least two different vectors and its length is 1
        Plane plane = new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        Vector normal = plane.getNormal();
        assertEquals(1, normal.length(), "Plane's normal is not a unit vector");
        assertEquals(0, normal.dotProduct(new Vector(1, 0, 0)), "Plane's normal is not orthogonal to the first vector");
        assertEquals(0, normal.dotProduct(new Vector(0, 1, 0)), "Plane's normal is not orthogonal to the second vector");

        // =============== Boundary Values Tests ==================

        // TC10: First and second points are the same
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(0, 0, 0), new Point(0, 0, 0), new Point(0, 1, 0)),
                "Constructed a plane with first and second points being the same");

        // TC11: First and third points are the same
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 0, 0)),
                "Constructed a plane with first and third points being the same");

        // TC12: Second and third points are the same
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(1, 0, 0)),
                "Constructed a plane with second and third points being the same");

        // TC13: All points are the same
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(0, 0, 0), new Point(0, 0, 0), new Point(0, 0, 0)),
                "Constructed a plane with all points being the same");

        // TC14: All points are on the same line but not coincident
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(0, 0, 0), new Point(1, 1, 1), new Point(2, 2, 2)),
                "Constructed a plane with all points on the same line");
    }

}