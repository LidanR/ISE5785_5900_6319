package renderer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.*;

/**
 * Testing Camera Class
 * @author Dan
 */
class CameraTest {
   /** Camera builder for the tests */
   private final Camera.Builder cameraBuilder = Camera.getBuilder()
           .setLocation(Point.ZERO)
           .setVpDistance(10);
   /** Assert failure message for a bad ray */
   private static final String  BAD_RAY       = "Bad ray";

   /**
    * Test method for
    * {@link renderer.Camera#constructRay(int, int, int, int)}.
    */
   @Test
   void testConstructRay() {
      cameraBuilder.setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
              .setResolution(2,2);
      Camera camera1 = cameraBuilder.setVpSize(8, 8).build();
      Camera camera2 = cameraBuilder.setVpSize(6, 6).build();

      // ============ Equivalence Partitions Tests ==============
      // EP01: 4X4 Inside (1,1)
      assertEquals(new Ray(Point.ZERO, new Vector(1, -1, -10)),
              camera1.constructRay(4, 4, 1, 1), BAD_RAY);

      // =============== Boundary Values Tests ==================
      // BV01: 4X4 Corner (0,0)
      assertEquals(new Ray(Point.ZERO, new Vector(3, -3, -10)),
              camera1.constructRay(4, 4, 0, 0), BAD_RAY);

      // BV02: 4X4 Side (0,1)
      assertEquals(new Ray(Point.ZERO, new Vector(1, -3, -10)),
              camera1.constructRay(4, 4, 1, 0), BAD_RAY);

      // BV03: 3X3 Center (1,1)
      assertEquals(new Ray(Point.ZERO, new Vector(0, 0, -10)),
              camera2.constructRay(3, 3, 1, 1), BAD_RAY);

      // BV04: 3X3 Center of Upper Side (0,1)
      assertEquals(new Ray(Point.ZERO, new Vector(0, -2, -10)),
              camera2.constructRay(3, 3, 1, 0), BAD_RAY);

      // BV05: 3X3 Center of Left Side (1,0)
      assertEquals(new Ray(Point.ZERO, new Vector(2, 0, -10)),
              camera2.constructRay(3, 3, 0, 1), BAD_RAY);

      // BV06: 3X3 Corner (0,0)
      assertEquals(new Ray(Point.ZERO, new Vector(2, -2, -10)),
              camera2.constructRay(3, 3, 0, 0), BAD_RAY);

   }

   @Test
   void testBuilder() {
      cameraBuilder.setVpSize(4, 4).setResolution(2, 2);

      // ============ Equivalence Partitions Tests ==============
      // EP01: set to a target point without up vector
      Point  target1    = new Point(10, -10, 0);
      Camera camera1    = cameraBuilder.setDirection(target1).build();
      Point  center1    = target1.subtract(Point.ZERO).normalize().scale(10);
      Vector right1     = new Vector(0,0,1);
      Vector up1        = new Vector(1, 1, 0).normalize();
      Vector direction1 = center1.add(up1.normalize()).subtract(right1).subtract(Point.ZERO);
      assertEquals(new Ray(Point.ZERO, direction1), camera1.constructRay(2, 2, 0, 0));

      // EP02: set to a target point with up vector
      Point  target2    = new Point(0, 5, 0);
      Camera camera2    = cameraBuilder.setDirection(target2, new Vector(0, 1, 1)).build();
      Point  center2    = new Point(0, 10, 0);
      Vector right2     = new Vector(1,0,0);
      Vector up2        = new Vector(0,0,1);
      Vector direction2 = center2.add(up2).subtract(right2).subtract(Point.ZERO);
      assertEquals(new Ray(Point.ZERO, direction2), camera2.constructRay(2, 2, 0, 0));

      // =============== Boundary Values Tests ==================
      // BV01: set to a target on Y-axis without up
      assertThrows(IllegalArgumentException.class, () -> cameraBuilder.setDirection(new Point(0, 10, 0)).build());
   }
}
