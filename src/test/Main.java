//Lidan Rubinov 215015900
//Dror Yakov Hi 325846319

package test;

import primitives.Double3;
import primitives.Point;
import primitives.Vector;

import static java.lang.System.out;
import static primitives.Util.isZero;

/**
 * Test program for the 1st stage
 * @author Dan Zilberstein
 */
public final class Main {
   /** A point for tests at (1,2,3) */
   private static final Point P1          = new Point(1, 2, 3);
   /** A point for tests at (2,4,6) */
   private static final Point  P2          = new Point(2, 4, 6);
   /** A point for tests at (2,4,5) */
   private static final Point  P3          = new Point(2, 4, 5);

   /** A vector for tests to (1,2,3) */
   private static final Vector V1          = new Vector(1, 2, 3);
   /** A vector for tests to (-1,-2,-3) (opposite to V1) */
   private static final Vector V1_OPPOSITE = new Vector(-1, -2, -3);
   /** A vector for tests to (-2,-4,-6) */
   private static final Vector V2          = new Vector(-2, -4, -6);
   /** A vector for tests to (0,3,-2) */
   private static final Vector V3          = new Vector(0, 3, -2);
   /** A vector for tests to (1,2,2) */
   private static final Vector V4          = new Vector(1, 2, 2);

   /**
    * Main program to tests initial functionality of the 1st stage
    * @param args irrelevant here
    */
   public static void main(String[] args) {
      pointTests();
      vectorDoubleOperationTests();
      vectorSingleOperationTests();
      out.println("If there were no any other outputs - all tests succeeded!");
   }

   /**
    * Method for testing operations on points
    */
   private static void pointTests() {
      // Subtract points
      if (!P2.subtract(P1).equals(V1))
         out.println("ERROR: (point2 - point1) does not work correctly");
      try {
         P1.subtract(P1);
         out.println("ERROR: (point - itself) does not throw an exception");
      } catch (IllegalArgumentException ignore) {} catch (Exception ignore) {
         out.println("ERROR: (point - itself) throws wrong exception");
      }

      // Add vector to point
      if (!(P1.add(V1).equals(P2)))
         out.println("ERROR: (point + vector) = other point does not work correctly");
      if (!(P1.add(V1_OPPOSITE).equals(Point.ZERO)))
         out.println("ERROR: (point + vector) = center of coordinates does not work correctly");

      // distances
      if (!isZero(P1.distanceSquared(P1)))
         out.println("ERROR: point squared distance to itself is not zero");
      if (!isZero(P1.distance(P1)))
         out.println("ERROR: point distance to itself is not zero");
      if (!isZero(P1.distanceSquared(P3) - 9))
         out.println("ERROR: squared distance between points is wrong");
      if (!isZero(P3.distanceSquared(P1) - 9))
         out.println("ERROR: squared distance between points is wrong");
      if (!isZero(P1.distance(P3) - 3))
         out.println("ERROR: distance between points to itself is wrong");
      if (!isZero(P3.distance(P1) - 3))
         out.println("ERROR: distance between points to itself is wrong");

   }

   /**
    * Method for testing operations on single vector
    */
   private static void vectorSingleOperationTests() {
      // test zero vector =====================================================
      try {
         new Vector(0, 0, 0);
         new Vector(Double3.ZERO);
         out.println("ERROR: zero vector does not throw an exception");
      } catch (IllegalArgumentException ignore) {} catch (Exception ignore) {
         out.println("ERROR: zero vector throws wrong exception");
      }

      // test length
      if (!isZero(V4.lengthSquared() - 9))
         out.println("ERROR: lengthSquared() wrong value");
      if (!isZero(V4.length() - 3))
         out.println("ERROR: length() wrong value");

      // test vector normalization vs vector length and cross-product
      Vector v = new Vector(1, 2, 3);
      Vector u = v.normalize();
      if (!isZero(u.length() - 1))
         out.println("ERROR: the normalized vector is not a unit vector");
      try { // test that the vectors are co-lined
         v.crossProduct(u);
         out.println("ERROR: the normalized vector is not parallel to the original one");
      } catch (Exception e) {}
      if (v.dotProduct(u) < 0)
         out.println("ERROR: the normalized vector is opposite to the original one");
   }

   /**
    * Method for testing operations on two vectors
    */
   private static void vectorDoubleOperationTests() {
      // Test add & subtract
      try {
         V1.add(V1_OPPOSITE);
         out.println("ERROR: Vector + -itself does not throw an exception");
      } catch (IllegalArgumentException ignore) {} catch (Exception ignore) {
         out.println("ERROR: Vector + itself throws wrong exception");
      }
      try {
         V1.subtract(V1);
         out.println("ERROR: Vector - itself does not throw an exception");
      } catch (IllegalArgumentException ignore) {} catch (Exception ignore) {
         out.println("ERROR: Vector + itself throws wrong exception");
      }
      if (!V1.add(V2).equals(V1_OPPOSITE))
         out.println("ERROR: Vector + Vector does not work correctly");
      if (!V1.subtract(V2).equals(new Vector(3, 6, 9)))
         out.println("ERROR: Vector + Vector does not work correctly");

      // test Dot-Product
      if (!isZero(V1.dotProduct(V3)))
         out.println("ERROR: dotProduct() for orthogonal vectors is not zero");
      if (!isZero(V1.dotProduct(V2) + 28))
         out.println("ERROR: dotProduct() wrong value");

      // test Cross-Product
      try { // test zero vector
         V1.crossProduct(V2);
         out.println("ERROR: crossProduct() for parallel vectors does not throw an exception");
      } catch (Exception e) {}
      Vector vr = V1.crossProduct(V3);
      if (!isZero(vr.length() - V1.length() * V3.length()))
         out.println("ERROR: crossProduct() wrong result length");
      if (!isZero(vr.dotProduct(V1)) || !isZero(vr.dotProduct(V3)))
         out.println("ERROR: crossProduct() result is not orthogonal to its operands");
   }

}
