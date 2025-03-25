package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTests {
    Vector v1 = new Vector(1, 2, 3);
    Vector v2 = new Vector(2,3,4);


    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for subtract
        assertEquals(new Vector(1, 1, 1), v2.subtract(v1), "Simple test for subtract");

        // ============ Boundary Value Tests ==================
        //TC02: Boundary test for subtract
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1), "Boundary test for subtract");
        }

    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for add
        assertEquals(new Point(3, 4, 5), v1.add(v2), "Simple test for add");

        // ============ Boundary Value Tests ==================
        //TC02: Boundary test for add
        assertEquals(new Vector(2, 3, 6), v1.add(v1), "Boundary test for add");

        //TC03: Boundary test for add
        Vector v4 = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v4), "Boundary test for add");
       }

    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for lengthSquared
        assertEquals(3, v1.lengthSquared(), "Simple test for lengthSquared");
    }

    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for length
        assertEquals(Math.sqrt(3), v1.length(), "Simple test for length");
    }

    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for scale
        assertEquals(new Vector(2, 4, 6), v1.scale(2), "Simple test for scale");

        // ============ Boundary Value Tests ==================
        // TC02: BVA test for scale
        assertThrows(IllegalArgumentException.class, () -> v1.scale(0), "BVA test for scale");
    }

    @Test
    void testDotProduct() {
        Vector v3 = new Vector(1, 0, 0);
        Vector v4 = new Vector(1, 1, 0);
        // ============ Equivalence Partitions Tests ==================
        // TC03: Simple test for dotProduct
        assertEquals(1, v3.dotProduct(v4), "0-90 degree test for dotProduct");

        Vector v5 = new Vector(1, 0, 0);
        Vector v6 = new Vector(-1, 1, 0);
        // ============ Equivalence Partitions Tests ==================
        // TC04: Simple test for dotProduct
        assertEquals(-1, v5.dotProduct(v6), "90-180 degree test for dotProduct");

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

        // ============ Equivalence Partitions Tests ==================
        //TC02: BVA test for crossProduct
        Vector v3 = new Vector(2, 4, 6);
        assertEquals(new Vector(0,0,0), v1.crossProduct(v3), "BVA test for crossProduct");
    }

    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for normalize// TC01: Simple test for normalize
        assertEquals(1, v1.normalize().length(), "Simple test for normalize");

        // TC02: Check that the normalized vector has the same direction
        Vector normalizedV1 = v1.normalize();
        assertTrue(normalizedV1.dotProduct(v1) > 0, "Normalized vector should have the same direction");
    }
}