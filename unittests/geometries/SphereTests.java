package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class SphereTests {
    /**
     * Test method for {@link geometries.Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for getNormal
        Sphere s = new Sphere(new Point(0,0,0), 1);
        assertEquals(new Vector(1, 0, 0), s.getNormal(new Point(1, 0, 0)), "Simple test for getNormal");
    }
    @Test
    void testFindIntersections() {
        Sphere s = new Sphere(new Point(0,0,0), 2);
        // ============ Equivalence Partitions Tests ==================
        // TC01: Ray start inside the sphere and intersects the sphere
        assertEquals(1, s.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))).size(), "Ray start inside the sphere and intersects the sphere");
        // TC02: Ray start outside the sphere and intersects the sphere
        assertEquals(2, s.findIntersections(new Ray(new Point(1, 3, 0), new Vector(0, -1, 0))).size(), "Ray start outside the sphere and intersects the sphere");
        // TC03: Ray start outside the sphere and doesn't intersect the sphere
        assertNull(s.findIntersections(new Ray(new Point(3, 3, 0), new Vector(0, 1, 0))), "Ray start outside the sphere and doesn't intersect the sphere");
        // TC04: Ray start outside the sphere and intersects the sphere in the other direction
        assertNull(s.findIntersections(new Ray(new Point(1, 3, 0), new Vector(0, 1, 0))),  "Ray start outside the sphere and intersects the sphere in the other direction");
        //========== Boundary Value Tests  ==================
        // TC05: Ray start on the sphere center and intersects the sphere once
        assertEquals(1, s.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0))).size(), "Ray start on the sphere center and intersects the sphere once");
        // TC06: Ray start on the sphere and passes through the sphere's center
        assertEquals(1, s.findIntersections(new Ray(new Point(0, 2, 0), new Vector(0, -1, 0))).size(), "Ray start on the sphere and passes through the sphere's center");
        // TC07: Ray start before the sphere and passes through the sphere's center
        assertEquals(2, s.findIntersections(new Ray(new Point(0, 4, 0), new Vector(0, -1, 0))).size(), "Ray start before the sphere and passes through the sphere's center");
        // TC08: Ray start on the sphere and doesn't pass through the sphere's center but the other direction does
        assertNull(s.findIntersections(new Ray(new Point(0, 2, 0), new Vector(0, 1, 0))), "Ray start on the sphere and doesn't pass through the sphere's center but the other direction does");
        // TC09: Ray start outside the sphere and doesn't intersect the sphere but the other direction does and passes through the sphere's center
        assertNull(s.findIntersections(new Ray(new Point(0, 4, 0), new Vector(0, 1, 0))), "Ray start outside the sphere and doesn't intersect the sphere but the other direction does and passes through the sphere's center");
        // TC10: Ray start inside the sphere and the other direction intersects the sphere's center
        assertEquals(1, s.findIntersections(new Ray(new Point(0, 1, 0), new Vector(0, 1, 0))).size(), "Ray start inside the sphere and the other direction intersects the sphere's center");


        // TC11: Ray start on the sphere and doesn't pass through the sphere's center but intersects the sphere once
        assertEquals(1, s.findIntersections(new Ray(new Point(0, 2, 0), new Vector(1, -0.5, 0))).size(), "Ray start on the sphere and doesn't pass through the sphere's center but intersects the sphere once");
        // TC12: Ray start on the sphere and doesn't intersect the sphere and the other direction doesn't intersect the sphere either
        assertNull(s.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, 1, 0))), "Ray start on the sphere and doesn't intersect the sphere and the other direction doesn't intersect the sphere either");


        // TC13: Ray start on the sphere and doesn't intersect with the sphere at all
        assertNull(s.findIntersections(new Ray(new Point(0, 2, 0), new Vector(0, 1, 0))), "Ray start on the sphere and doesn't intersect the sphere at all");
        // TC14: Ray tangent to the sphere and not intersecting
        assertNull(s.findIntersections(new Ray(new Point(2, 2, 0), new Vector(0, 1, 0))), "Ray tangent to the sphere and not intersecting");
        // TC15: Ray tangent to the sphere in the other direction and not intersecting
        assertNull(s.findIntersections(new Ray(new Point(2, 3, 0), new Vector(0, 1, 0))), "Ray tangent to the sphere in the other direction and not intersecting");


        // TC16: Ray start inside the sphere and orthogonal to the sphere's center if we draw a line from the sphere's center to the ray's start point
        assertEquals(1, s.findIntersections(new Ray(new Point(0, 1, 0), new Vector(0, 0, 1))).size(), "Ray start inside the sphere and orthogonal to the sphere's center if we draw a line from the sphere's center to the ray's start point");
        // TC17: Ray start outside the sphere and orthogonal to the sphere's center if we draw a line from the sphere's center to the ray's start point
        assertNull(s.findIntersections(new Ray(new Point(0, 3, 0), new Vector(0, 0, -1))), "Ray start outside the sphere and orthogonal to the sphere's center if we draw a line from the sphere's center to the ray's start point");
        }
}