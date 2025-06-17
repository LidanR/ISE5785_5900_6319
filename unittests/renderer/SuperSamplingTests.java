package renderer;

import geometries.*;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Test rendering an improved images
 */
class SuperSamplingTests {

   /** Scene for the tests */
   private final Scene          scene         = new Scene("Test scene");
   /** Camera builder for the tests with triangles */
   private final Camera.Builder cameraBuilder = Camera.getBuilder().setDebugPrint(1);
   /**
    *  The thread number for multithreading
    */
   private int threadNum = -1;
   @Test
   void Glossy_Surface_Test() {
      // Set visible ambient light
      scene.setBackground(new Color(0, 0, 0)).setAmbientLight(new AmbientLight(new Color(255, 255, 255)));

      // Ground plane (Mirror)
      scene.geometries.add(
              new Plane(
                      new Point(1, 0, 1),
                      new Vector(0, 1, 0)
              ).setMaterial(new Material().setKR(1.0).setKD(0.6).setStrength(6).setKA(0.1).setKS(0.3).setShininess(30))
                      .setEmission(new Color(0, 0, 0))
      );



      // Sphere directly in front of the camera
      scene.geometries.add(
              new Sphere(new Point(-18, 40, -30), 5)
                      .setEmission(new Color(0, 0, 150))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );
      scene.geometries.add(
              new Sphere(new Point(0, 25, -30), 5)
                      .setEmission(new Color(0, 150, 0))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );
      scene.geometries.add(
              new Sphere(new Point(18, 5, -30), 5)
                      .setEmission(new Color(150, 0, 0))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );


      scene.lights.add(
              new DirectionalLight(new Color(255,255,255) ,new Vector(0,-1,0))
      );

      Blackboard blackboard = new Blackboard.Builder()
              .setSoftShadows(false)
              .setDepthOfField(false)
              .setBlurryAndGlossy(true)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();
      cameraBuilder
              .setBlackboard(blackboard)
              .setRayTracer(scene, RayTracerType.VOXEL)
              .setMultithreading(threadNum)
              .setLocation(new Point(0, 20, -100))          // In front of sphere
              .setDirection(new Vector(0, -0.1, 1))         // Looking at origin
              .setVpDistance(100)
              .setVpSize(150, 150)
              .setResolution(600, 600)
              .build()
              .renderImage()
              .writeToImage("superSampling/Glossy_Surface");


   }
   /**
    * Test for rendering a blurry glass ball behind a plane
    * This test creates a scene with a ground plane and a glass ball behind it,
    * applying blurry reflections to the glass ball.
    */
   @Test
   void Blurry_Glass_Test() {
      // Set visible ambient light
      scene.setBackground(new Color(0, 0, 0)).setAmbientLight(new AmbientLight(new Color(255, 255, 255)));

      // Ground plane (Mirror)
      scene.geometries.add(
              new Polygon(
                      new Point(30, 350, -35),
                      new Point(-30, 350, -35),
                      new Point(-30, -350, -35),
                      new Point(30, -350, -35)

              ).setMaterial(new Material().setKT(1).setKD(0.6).setStrength(20).setKA(0.1).setKS(0.3).setShininess(30))
                      .setEmission(new Color(100, 100, 150))
      );
      scene.geometries.add(
              new Plane(
                      new Point(1, 0, 1),
                      new Vector(0, 1, 0)
              ).setMaterial(new Material().setKD(0.1).setKA(0.1).setKS(0.3).setShininess(0))
                      .setEmission(new Color(10, 20, 0))
      );


      // Sphere directly in front of the camera
      scene.geometries.add(
              new Sphere(new Point(-18, 5, -20), 5)
                      .setEmission(new Color(0, 0, 150))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );
      scene.geometries.add(
              new Sphere(new Point(0, 5, -25), 5)
                      .setEmission(new Color(0, 150, 0))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );
      scene.geometries.add(
              new Sphere(new Point(18, 5, -30), 5)
                      .setEmission(new Color(150, 0, 0))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );


      scene.lights.add(
              new DirectionalLight(new Color(255,255,255) ,new Vector(0,-1,0))
      );

      Blackboard blackboard = new Blackboard.Builder()
              .setSoftShadows(false)
              .setDepthOfField(false)
              .setBlurryAndGlossy(true)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();
      cameraBuilder
              .setBlackboard(blackboard)
              .setRayTracer(scene, RayTracerType.VOXEL)
              .setMultithreading(threadNum)
              .setLocation(new Point(0, 20, -100))          // In front of sphere
              .setDirection(new Vector(0, -0.1, 1))         // Looking at origin
              .setVpDistance(100)
              .setVpSize(150, 150)
              .setResolution(600, 600)
              .build()
              .renderImage()
              .writeToImage("superSampling/Blurry_Glass");
   }
   /**
    * Test for advanced depth of field rendering
    * This test creates a scene with multiple spheres at different distances
    * and applies depth of field rendering to create a realistic focus effect.
    */
   @Test
   public void Depth_Of_Field_Test() {
      Scene scene = new Scene("ultimate depth of field grid test");

      Material mat = new Material().setKD(0.3).setKR(0.0).setKS(0.7).setShininess(500);

      scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(40, 40, 40)));

      // Generate 4 rows of spheres across 7 depth layers (Z axis)
      int rows = 4;
      int columns = 7;
      double spacing = 10;
      int radius = 5;
      double startX = -columns / 2.0 * spacing;
      double startY = -rows / 2.0 * spacing;

      for (int row = 0; row < rows; row++) {
         for (int col = 0; col < columns; col++) {
            double x = startX + col * spacing;
            double y = startY + row * spacing;

            // Z varies across columns: behind and in front of the focal plane (100)
            double z = -120 + col * 40;  // From -120 to +120

            int r = (50 + (row * 80)) % 256;
            int g = (100 + (col * 60)) % 256;
            int b = (150 + (row * 30 + col * 20)) % 256;

            scene.geometries.add(
                    new Sphere(new Point(x, y, z), radius)
                            .setEmission(new Color(r, g, b))
                            .setMaterial(mat)
            );
         }
      }
      scene.geometries.add(
                new Plane(new Point(0, -25, 0), new Vector(0, 1, 0))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
                        .setEmission(new Color(100, 100, 100))
      );

      Blackboard blackboard = new Blackboard.Builder()
              .setSoftShadows(false)
              .setDepthOfField(true)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();

      Camera.getBuilder()
              .setResolution(1000, 1000)
              .setBlackboard(blackboard)
              .setMultithreading(threadNum)
              .setRayTracer(scene, RayTracerType.VOXEL)
              .setVpDistance(100)
              .setVpSize(40, 40)
              .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
              .setLocation(new Point(0, 0, 200))          // Looking along -Z
              .setFocusPointDistance(100)
              .setDebugPrint(1)// Focused near center
              .setAperture(2)
              .build()
              .renderImage()
              .writeToImage("superSampling/Depth_Of_Field");
   }

   /**
    * Test for rendering a scene with soft shadows and a sphere casting shadows on triangles
    * This test creates a scene with two triangles and a sphere, applying soft shadows to the light source.
    */
   @Test
   void Soft_Shadows_Test() {
      scene.geometries //
              .add( //
                      new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135), new Point(75, 75, -150)) //
                              .setMaterial(new Material().setKS(0.8).setShininess(60)), //
                      new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150)) //
                              .setMaterial(new Material().setKS(0.8).setShininess(60)), //
                      new Sphere(new Point(0, 0, -11), 30d) //
                              .setEmission(new Color(BLUE)) //
                              .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30)) //
              );
      scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
      scene.lights //
              .add(new SpotLight(new Color(700, 400, 400), new Point(40, 40, 115), new Vector(-1, -1, -4),20) //
                      .setKl(4E-4).setKq(2E-5));
      Blackboard blackboard = new Blackboard.Builder()
              .setSoftShadows(true)
              .build();
      Camera.getBuilder()
              .setLocation(new Point(0, 0, 1000)).setVpDistance(1000)
              .setDirection(Point.ZERO, Vector.AXIS_Y)
              .setVpSize(200, 200)
              .setMultithreading(threadNum)
              .setBlackboard(blackboard)
              .setRayTracer(scene, RayTracerType.VOXEL) //
              .setResolution(600, 600) //
              .build() //
              .renderImage() //
              .writeToImage("superSampling/Soft_Shadows");
   }
   /**
    *  Test for rendering a scene with anti aliasing
    */
   @Test
   void Anti_Aliasing_Test() {
      Scene scene = new Scene("anti-aliasing test")
              .setBackground(new Color(WHITE))
              .setAmbientLight(new AmbientLight(new Color(WHITE)));
      scene.geometries //
              .add(// center
                      new Sphere(new Point(0, 0, -100), 150).setEmission(new Color(RED)).setMaterial(new Material().setKA(0.25)));
        scene.lights //
                .add(new PointLight(new Color(WHITE), new Point(0, 0, -50)) //
                        .setKc(1).setKl(0.00001).setKq(0.000001));
      Blackboard blackboard = Blackboard.getBuilder().setUseCircle(true).setAntiAliasing(true).build();
      Camera.getBuilder() //
                .setMultithreading(threadNum) //
              .setBlackboard(blackboard)
              .setLocation(new Point(0,0,100))
              .setDirection(Point.ZERO, Vector.AXIS_Y) //
              .setVpDistance(100) //
              .setVpSize(500, 500) //
              .setRayTracer(scene, RayTracerType.VOXEL) //
              .setResolution(600, 600) //
              .build() //
              .renderImage() //
              .writeToImage("superSampling/Anti_Aliasing");
   }
   /**
    * Test for rendering a video with glossy surfaces
    * And soft shadows
    */
//   @Test
//   void Glossy_Surface_Video_Test() {
//      int maxhight = 20;
//      Blackboard blackboard = new Blackboard.Builder()
//              .setAmountOfRays(10)
//              .setSoftShadows(true)
//              .setDepthOfField(false)
//              .setBlurryAndGlossy(true)
//              .setUseCircle(true)
//              .setAntiAliasing(false)
//              .build();
//      cameraBuilder
//              .setBlackboard(blackboard)
//              .setMultithreading(threadNum)
//              .setLocation(new Point(0, 20, -100))          // In front of sphere
//              .setDirection(new Vector(0, -0.1, 1))         // Looking at origin
//              .setVpDistance(100)
//              .setVpSize(150, 150)
//              .setResolution(600, 600);
//
//      for(int i = maxhight ;i>=5;i--)
//      {
//         Scene scene1 = new Scene("Glossy_Surface_Video_Test");
//         // Set visible ambient light
//         scene1.setBackground(new Color(0, 0, 0)).setAmbientLight(new AmbientLight(new Color(255, 255, 255)));
//
//         // Ground plane (Mirror)
//         scene1.geometries.add(
//                 new Plane(
//                         new Point(1, 0, 1),
//                         new Vector(0, 1, 0)
//                 ).setMaterial(new Material().setKR(1.0).setKD(0.6).setStrength(0.8).setKA(0.1).setKS(0.3).setShininess(30))
//                         .setEmission(new Color(0, 0, 0))
//         );
//
//
//
//         scene1.geometries.add(
//                 new Sphere(new Point(0, i, -30), 5)
//                         .setEmission(new Color((i*11)%255, (i*43)%255, (i*67)%255))
//                         .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
//         );
//         scene1.lights.add(
//                 new PointLight(new Color(255,255,255) ,new Point(0,60,0),10)
//         );
//         cameraBuilder.
//                 setRayTracer(scene1, RayTracerType.GRID)
//                 .build()
//                 .renderImage()
//                 .writeToImage("superSampling/video/Glossy_Surface_"+i);
//      }
//      try {
//         ImagesToVideo.createVideoFromImages("superSampling/video",
//                 "superSampling/video/TheVideo/first",
//                 20,true);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
//
//
//
//
//
//
//   }
//   /**
//    * Test for rendering a video with depth of field
//    * This test creates a scene with multiple spheres at different heights
//    * and applies depth of field rendering to create a realistic focus effect.
//    */
//   @Test
//   void Depth_Of_Field_Video_Test() {
//      int maxhight = 50;
//      Blackboard blackboard = new Blackboard.Builder()
//              .setAmountOfRays(10)
//              .setSoftShadows(false)
//              .setDepthOfField(true)
//              .setBlurryAndGlossy(false)
//              .setUseCircle(true)
//              .setAntiAliasing(false)
//              .build();
//      cameraBuilder
//              .setBlackboard(blackboard)
//              .setMultithreading(threadNum)
//              .setFocusPointDistance(5)
//              .setAperture(4)
//              .setLocation(new Point(0, 20, -100))          // In front of sphere
//              .setDirection(new Vector(0, -0.1, 1))         // Looking at origin
//              .setVpDistance(100)
//              .setVpSize(150, 150)
//              .setResolution(600, 600);
//
//      for (int i = 0; i <= maxhight; i += 1) {
//         Scene scene1 = new Scene("DOF_Video_Test");
//         // Set visible ambient light
//         scene1.setBackground(new Color(0, 0, 0)).setAmbientLight(new AmbientLight(new Color(255, 255, 255)));
//
//         scene1.geometries.add(
//                 new Sphere(new Point(-5 , 20, 0 + i), 5)
//                         .setEmission(new Color(255, 0, 0))
//                         .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
//         );
//         scene1.geometries.add(
//                 new Sphere(new Point(0 , 20, 0 + i), 5)
//                         .setEmission(new Color(0, 255, 0))
//                         .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
//         );
//         scene1.geometries.add(
//                 new Sphere(new Point(5 , 20, 0 + i), 5)
//                         .setEmission(new Color(0, 0, 255))
//                         .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
//         );
//         scene1.lights.add(
//                 new PointLight(new Color(255, 255, 255), new Point(0, 60, 0))
//         );
//         cameraBuilder.
//                 setRayTracer(scene1, RayTracerType.GRID)
//                 .build()
//                 .renderImage()
//                 .writeToImage("superSampling/video2/dof_" + i);
//      }
//      try {
//         ImagesToVideo.createVideoFromImages("superSampling/video2",
//                 "superSampling/video2/TheVideo/second",
//                 2, true);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
//   }

}
