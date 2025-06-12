package geometries;

import org.junit.jupiter.api.Test;
import primitives.*;

import static org.junit.jupiter.api.Assertions.*;

public class CubeTests {
    @Test
    public void testGetNormal() {
        // Create a unit cube centered at origin
        Cube cube = new Cube(2, 2, 2, new Point(0, 0, 0));

        // Epsilon for floating-point comparison
        double epsilon = 1e-10;

        // Right face (+X)
        assertEquals(new Vector(1, 0, 0),
                cube.getNormal(new Point(1, 0, 0)),
                "Wrong normal for right face");

        // Left face (-X)
        assertEquals(new Vector(-1, 0, 0),
                cube.getNormal(new Point(-1, 0, 0)),
                "Wrong normal for left face");

        // Top face (+Y)
        assertEquals(new Vector(0, 1, 0),
                cube.getNormal(new Point(0, 1, 0)),
                "Wrong normal for top face");

        // Bottom face (-Y)
        assertEquals(new Vector(0, -1, 0),
                cube.getNormal(new Point(0, -1, 0)),
                "Wrong normal for bottom face");

        // Front face (+Z)
        assertEquals(new Vector(0, 0, 1),
                cube.getNormal(new Point(0, 0, 1)),
                "Wrong normal for front face");

        // Back face (-Z)
        assertEquals(new Vector(0, 0, -1),
                cube.getNormal(new Point(0, 0, -1)),
                "Wrong normal for back face");

        // Not on surface
        assertNull(cube.getNormal(new Point(0.3, 0.3, 0.3)),
                "Expected null for a point not on any cube face");
    }
    @Test
    void testCubeIntersections() {
        Cube cube = new Cube(2, 2, 2, new Point(0, 0, 0)); // Cube from (-1,-1,-1) to (1,1,1)
        // ================== BOUNDARY VALUE TESTS ==================
        // TC01 : Ray intersects with a corner of the cube
        Ray ray1 = new Ray(new Point(2, 2, 2), new Vector(-1, -1, -1));
        assertNull( cube.calculateIntersections(ray1),
                "TC01: Ray should intersect at the corner of the cube");
        // TC02 : Ray intersects with an edge of the cube
        Ray ray2 = new Ray(new Point(2, 0, 2), new Vector(-1, 0, -1));
        assertNull(cube.calculateIntersections(ray2),
                "TC02: Ray should intersect at the edge of the cube");
        // TC03 : Ray intersects with a face of the cube
        Ray ray3 = new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0));
        assertEquals(1, cube.calculateIntersections(ray3).size(),
                "TC03: Ray should intersect with the face of the cube");
    }

    @Test
    void findIntersectionsWithDistance()
    {
        Cube cube = new Cube(2, 2, 2, new Point(0, 0, 0)); // Cube from (-1,-1,-1) to (1,1,1)
        // ================== BOUNDARY VALUE TESTS ==================
        // TC01 : Ray intersects with a corner of the cube
        Ray ray1 = new Ray(new Point(2, 2, 2), new Vector(-1, -1, -1));
        assertNull(cube.calculateIntersections(ray1, 10),
                "TC01: Ray should intersect at the corner of the cube");
        // TC02 : Ray intersects with an edge of the cube
        Ray ray2 = new Ray(new Point(2, 0, 2), new Vector(-1, 0, -1));
        assertNull(cube.calculateIntersections(ray2, 10),
                "TC02: Ray should intersect at the edge of the cube");
        // TC03 : Ray intersects with a face of the cube
        Ray ray3 = new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0));
        assertEquals(1, cube.calculateIntersections(ray3, 10).size(),
                "TC03: Ray should intersect with the face of the cube");
        // TC04 : Ray intersects with a face of the cube at a distance
        Ray ray4 = new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0));
        assertEquals(1, cube.calculateIntersections(ray4, 2).size(),
                "TC04: Ray should intersect with the face of the cube at a distance");
        // TC05 : Ray does not intersect with the cube
        Ray ray5 = new Ray(new Point(3, 3, 3), new Vector(-1, -1, -1));
        assertNull(cube.calculateIntersections(ray5, 10),
                "TC05: Ray should not intersect with the cube");
    }
}
