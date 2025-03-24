package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTests {
    Point p1 = new Point(1, 2, 3);
    Vector v1 = new Vector(1, 1, 1);
    Vector v2 = new Vector(0,0,0);

    @Test
    void testSubtract() {
        }

    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for add
        Point p2 = p1.add(v1);
        assertEquals(new Point(2, 3, 4), p2, "Simple test for add");
       }

    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for lengthSquared
        assertEquals(3, v1.lengthSquared(), "Simple test for lengthSquared");

        // ============ Equivalence Partitions Tests ==================
        // TC02: BVA test for length
        assertEquals(0, v2.length(), "BVA test for length");
    }

    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for length
        assertEquals(Math.sqrt(3), v1.length(), "Simple test for length");

        // ============ Equivalence Partitions Tests ==================
        // TC02: BVA test for length
        assertEquals(0, v2.length(), "BVA test for length");
    }

    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for scale
        assertEquals(new Vector(2, 2, 2), v1.scale(2), "Simple test for scale");

        // ============ Equivalence Partitions Tests ==================
        // TC02: BVA test for scale
        assertEquals(new Vector(0,0,0), v1.scale(0), "BVA test for scale");

        // ============ Equivalence Partitions Tests ==================
        // TC03: BVA test for scale
        assertEquals(new Vector(0,0,0), v2.scale(2), "BVA test for scale");
    }

    @Test
    void testDotProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(2, 3, 4);
        // ============ Equivalence Partitions Tests ==================
        // TC02: Simple test for dotProduct
        assertEquals(20, v1.dotProduct(v2), "Simple test for dotProduct");

        Vector v3 = new Vector(1, 0, 0);
        Vector v4 = new Vector(1, 1, 0);
        // ============ Equivalence Partitions Tests ==================
        // TC03: Simple test for dotProduct
        assertEquals(1, v3.dotProduct(v4), "Simple test for dotProduct");

        Vector v5 = new Vector(1, 0, 0);
        Vector v6 = new Vector(-1, 1, 0);
        // ============ Equivalence Partitions Tests ==================
        // TC04: Simple test for dotProduct
        assertEquals(-1, v5.dotProduct(v6), "Simple test for dotProduct");

        Vector v7 = new Vector(1, 0, 0);
        Vector v8 = new Vector(0, 1, 0);
        // ============ Equivalence Partitions Tests ==================
        // TC05: BVA test for dotProduct
        assertEquals(0, v7.dotProduct(v8), "BVA test for dotProduct");
    }

    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for crossProduct
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(2, 3, 4);
        assertEquals(new Vector(-1, 2, -1), v1.crossProduct(v2), "Simple test for crossProduct");


    }

    @Test
    void testNormalize() {
    }
}