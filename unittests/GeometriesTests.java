import geometries.Geometries;
import geometries.Sphere;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeometriesTests {


    /**
     * Test method for {@link geometries.Geometries#findIntersections(Ray)}.
     */
@Test
void testFindIntersections() {
    Geometries geometries = new Geometries();
    Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
    //========== Boundary Value Tests  ==================
    // TC01: Empty collection
    assertNull(geometries.findIntersections(ray), "Empty collection should return null");

    // TC02: No shape intersects
    geometries.add(new Sphere(new Point(5, 5, 5), 1));
    assertNull(geometries.findIntersections(ray), "No shape intersects should return null");

    // TC03: One shape intersects
    geometries.add(new Sphere(new Point(2, 0, 0), 1));
    List<Point> result = geometries.findIntersections(ray);
    assertEquals(2, result.size(), "One shape intersects should return one intersection point");

    Geometries geometries2 = new Geometries();
    // TC04: All shapes intersect
    geometries2.add(new Sphere(new Point(5, 0, 0), 1));
    geometries2.add(new Sphere(new Point(10, 0, 0), 1));
    geometries2.add(new Sphere(new Point(15, 0, 0), 1));
    result = geometries2.findIntersections(ray);
    assertEquals(6, result.size(), "All shapes intersect should return three intersection points");

    // ============ Equivalence Partitions Tests ==================
    // TC05: Some shapes intersect
    geometries.add(new Sphere(new Point(20, 5, 0), 1));
    result = geometries.findIntersections(ray);
    assertEquals(2, result.size(), "Some shapes intersect should return two intersection points");
}
}