package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorTests {
    Vector v1 = new Vector(1, 2, 3);
    Vector v2 = new Vector(2,3,4);

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for constructor
        assertEquals(new Vector(1, 2, 3), v1, "Simple test for constructor");

        // ============ Boundary Value Tests ==================
        // TC02: Boundary test for constructor - zero vector
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), "Simple test for constructor");
    }

    /**
     * Test method for {@link primitives.Vector#subtract(Vector)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for subtract
        assertEquals(new Vector(1, 1, 1), v2.subtract(v1), "Simple test for subtract");

        // ============ Boundary Value Tests ==================
        //TC02: Boundary test for subtract - subtracting the same vector
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1), "Boundary test for subtract");
        }

    /**
     * Test method for {@link primitives.Vector#add(Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for add
        assertEquals(new Point(3, 5, 7), v1.add(v2), "Simple test for add");

        // ============ Boundary Value Tests ==================
        //TC02: Boundary test for add - adding the same vector
        assertEquals(new Vector(2, 4, 6), v1.add(v1), "Boundary test for add");

        //TC03: Boundary test for add - adding the opposite vector
        Vector v4 = new Vector(-1, -2, -3);
        assertThrows(IllegalArgumentException.class, () -> v1.add(v4), "Boundary test for add");
       }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for lengthSquared
        assertEquals(14, v1.lengthSquared(), "Simple test for lengthSquared");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for length
        assertEquals(Math.sqrt(14), v1.length(), "Simple test for length");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for scale
        assertEquals(new Vector(2, 4, 6), v1.scale(2), "Simple test for scale");

        // ============ Boundary Value Tests ==================
        // TC02: BVA test for scale - scaling by 0
        assertThrows(IllegalArgumentException.class, ()-> v1.scale(0), "BVA test for scale");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==================
        // TC03: Simple test for dotProduct
        Vector v1 = new Vector(1, 1, 0);
        Vector v2 = new Vector(2, 0, 0);
        assertEquals(2, v1.dotProduct(v2), "0-90 degree test for dotProduct");

        // ============ Equivalence Partitions Tests ==================
        // TC04: Simple test for dotProduct
        Vector v3 = new Vector(-2, 0, 0);
        assertEquals(-2, v2.dotProduct(v3), "90-180 degree test for dotProduct");

        // ============ BVA Value Tests ==================
        // TC05: BVA test for dotProduct - orthogonal vectors
        Vector v4 = new Vector(2, 0, 0);
        Vector v5 = new Vector(0, 2, 0);
        assertEquals(0, v4.dotProduct(v5), "BVA test for dotProduct - orthogonal vectors");

        // TC06: BVA test for dotProduct - unit vector
        Vector v6 = new Vector(1, 0, 0);
        assertEquals(1, v1.dotProduct(v6), "BVA for dotProduct - unit vector");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for crossProduct
        Vector v1 = new Vector(1, 1, 0);
        Vector v2 = new Vector(1, 0, 0);
        assertEquals(new Vector(0, 0, -1), v1.crossProduct(v2), "Simple test for crossProduct");

        // ============ BVA value Tests ==================
        //TC02: BVA test for crossProduct - same direction
        Vector v3 = new Vector(2, 2, 0);
        assertThrows(IllegalArgumentException.class, ()-> v1.crossProduct(v3), "BVA test for crossProduct - same direction");

        //TC03: BVA test for crossProduct - opposite direction, same length
        Vector v4 = new Vector(-1, -1, 0);
        assertThrows(IllegalArgumentException.class, ()-> v1.crossProduct(v4), "BVA test for crossProduct - opposite direction, same length");

        //TC04: BVA test for crossProduct - opposite direction, different length
        Vector v5 = new Vector(-2, -2, 0);
        assertThrows(IllegalArgumentException.class, ()-> v1.crossProduct(v5), "BVA test for crossProduct - opposite direction, different length");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
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