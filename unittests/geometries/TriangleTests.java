package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class TriangleTests {
    /**
     * Test method for {@link geometries.Triangle#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for getNormal
        Triangle t = new Triangle(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        assertEquals(new Vector(0, 0, 1), t.getNormal(new Point(0, 0, 0)), "Simple test for getNormal");
    }
    /**
     * Test method for {@link geometries.Triangle#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==================
        Triangle t = new Triangle(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        // TC01: Ray intersects inside the triangle
        assertEquals(t.findIntersections(new Ray(new Point(0.3, 0.3, -1), new Vector(0, 0, 1))).size(), 1, "Ray intersects inside the triangle");
        // TC02: Ray intersects outside the triangle against edge
        assertNull(t.findIntersections(new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, -1))), "Ray intersects outside the triangle against edge");
        // TC03: Ray intersects outside the triangle against vertex
        assertNull(t.findIntersections(new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, -1))), "Ray intersects outside the triangle against vertex");
        //============ Boundary Value Tests  ==================
        // TC04: Ray intersects on edge
        assertNull(t.findIntersections(new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, 1))), "Ray intersects on edge");
        // TC05: Ray intersects on vertex
        assertNull(t.findIntersections(new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, 1))), "Ray intersects on vertex");
        // TC06: Ray intersects on the continuation of the edge
        assertNull(t.findIntersections(new Ray(new Point(0.5, 0.5, -1), new Vector(0, 0, 1))), "Ray intersects on the continuation of the edge");


    }
}