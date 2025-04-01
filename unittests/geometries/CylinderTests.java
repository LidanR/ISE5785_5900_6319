package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class CylinderTests {
    /**
     * Test method for {@link geometries.Cylinder#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // Create a cylinder with:
        // - radius: 1
        // - height: 7
        // - axis ray starting at (1,1,1) with direction (2,3,6)
        Vector axisDirection = new Vector(2, 3, 6);
        Point p0 = new Point(1, 1, 1);
        Ray axisRay = new Ray(p0, axisDirection);
        Cylinder cylinder = new Cylinder(1, axisRay, 7);

        // Get the normalized axis direction
        Vector dir = axisDirection.normalize();

        // ============ Equivalence Partitions Tests ==================
        // TC01: Lateral surface point
        // Choose t = 3.5 (this is within the height of the cylinder)
        double t = 3.5;

        // Calculate point on axis
        Point axisPoint = p0.add(dir.scale(t));

        // Create a vector perpendicular to the axis
        // We'll use cross product to ensure it's perpendicular and non-zero
        Vector perpBase = new Vector(1, 2, 1); // Any vector not parallel to dir
        Vector perpVector = dir.crossProduct(perpBase).normalize();

        // Create point on the lateral surface
        Point p1 = axisPoint.add(perpVector);

        // The normal is the vector from axis point to surface point (normalized)
        Vector expectedNormal1 = perpVector;

        assertEquals(expectedNormal1, cylinder.getNormal(p1), "TC01: Failed - Lateral surface point");

        // TC02: Point on the bottom base
        // The bottom base normal is -dir
        Vector expectedNormal2 = dir.scale(-1);

        // Create a point definitely on the bottom base (t < 0)
        // We'll move in the negative direction of the axis to ensure t < 0
        Vector negAxisDir = dir.scale(-0.5); // Move in negative axis direction
        Point p2 = p0.add(negAxisDir).add(new Vector(0.3, 0.2, 0.1)); // Add offset to avoid zero vector

        assertEquals(expectedNormal2, cylinder.getNormal(p2), "TC02: Failed - Bottom base point");

        // TC03: Point on the top base
        // Top center is at p0 + height * dir
        Point topCenter = p0.add(dir.scale(7));

        // The top base normal is dir
        Vector expectedNormal3 = dir;

        // Create a point definitely on the top base (t > height)
        Vector beyondTop = dir.scale(7.5); // Move beyond the top
        Point p3 = p0.add(beyondTop).add(new Vector(0.4, 0.3, 0.2)); // Add offset to avoid zero vector

        assertEquals(expectedNormal3, cylinder.getNormal(p3), "TC03: Failed - Top base point");

        // =============== Boundary Value Analysis ==================
        // TC04: Point on bottom base - EXPLICITLY ensure t <= 0
        // This is critical: we must ensure the projection onto the axis gives t <= 0

        // Calculate a point that's definitely on the bottom base
        // The key is to ensure the dot product with dir is <= 0
        Vector toBottomBase = dir.scale(-1); // Move in the opposite direction of the axis
        Point p4 = p0.add(toBottomBase);

        // Double-check that p4 is on the bottom base (t <= 0)
        Vector v4 = p4.subtract(p0);
        double t4 = v4.dotProduct(dir);

        // If somehow t4 > 0, try a more extreme negative direction
        if (t4 > 0) {
            toBottomBase = dir.scale(-2);
            p4 = p0.add(toBottomBase);
        }

        assertEquals(expectedNormal2, cylinder.getNormal(p4), "TC04: Failed - Point on bottom base");

        // TC05: Point on top base - EXPLICITLY ensure t >= height
        // Calculate a point that's definitely on the top base
        Vector toTopBase = dir.scale(7.1); // Slightly beyond height
        Point p5 = p0.add(toTopBase);

        // Double-check that p5 is on the top base (t >= height)
        Vector v5 = p5.subtract(p0);
        double t5 = v5.dotProduct(dir);

        // If somehow t5 < height, try a more extreme position
        if (t5 < 7) {
            toTopBase = dir.scale(8);
            p5 = p0.add(toTopBase);
        }

        assertEquals(expectedNormal3, cylinder.getNormal(p5), "TC05: Failed - Point on top base");

        // TC06: Bottom base edge (ensuring non-zero vectors)
        // Create a non-zero radius vector perpendicular to axis
        Vector radiusDir = dir.crossProduct(new Vector(0, 0, 1)).normalize();
        if (radiusDir.length() < 0.1) {
            // If we get too close to zero vector, use a different cross product
            radiusDir = dir.crossProduct(new Vector(0, 1, 0)).normalize();
        }

        // Ensure we're on the bottom base
        Point p6 = p0.add(dir.scale(-0.1)).add(radiusDir);
        assertEquals(expectedNormal2, cylinder.getNormal(p6), "TC06: Failed - Bottom base edge");

        // TC07: Top base edge (ensuring non-zero vectors)
        // Place point on top base at radius distance
        Point p7 = p0.add(dir.scale(7.1)).add(radiusDir);
        assertEquals(expectedNormal3, cylinder.getNormal(p7), "TC07: Failed - Top base edge");
    }
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==================

        //========== Boundary Value Tests  ==================

    }
}