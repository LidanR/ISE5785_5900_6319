package renderer;

import geometries.*;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.JsonScene;
import scene.Scene;

import java.io.IOException;

import static java.awt.Color.*;

/**
 * Test rendering an improved images
 */
class SuperSamplingTests {

   /** Scene for the tests */
   private final Scene          scene         = new Scene("Test scene");
   /** Camera builder for the tests with triangles */
   private final Camera.Builder cameraBuilder = Camera.getBuilder();
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
              ).setMaterial(new Material().setKR(1.0).setKD(0.6).setStrength(0.3).setKA(0.1).setKS(0.3).setShininess(30))
                      .setEmission(new Color(0, 0, 0))
      );



      // Sphere directly in front of the camera
      scene.geometries.add(
              new Sphere(new Point(-18, 25, -30), 5)
                      .setEmission(new Color(0, 0, 150))
                      .setMaterial(new Material().setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
      );
      scene.geometries.add(
              new Sphere(new Point(0, 15, -30), 5)
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
              .setAmountOfRays(10)
              .setSoftShadows(false)
              .setDepthOfField(false)
              .setBlurryAndGlossy(true)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();
      cameraBuilder
              .setBlackboard(blackboard)
              .setRayTracer(scene, RayTracerType.SIMPLE)
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

              ).setMaterial(new Material().setKT(1).setKD(0.6).setStrength(1.8).setKA(0.1).setKS(0.3).setShininess(30))
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
              .setAmountOfRays(10)
              .setSoftShadows(false)
              .setDepthOfField(false)
              .setBlurryAndGlossy(true)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();
      cameraBuilder
              .setBlackboard(blackboard)
              .setRayTracer(scene, RayTracerType.SIMPLE)
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
      Scene scene = new Scene("advanced depth of field test");

      Material mat = new Material().setKD(0.3).setKR(0.0).setKS(0.7).setShininess(500);

      scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(40, 40, 40))); // Increased light intensity
      scene.geometries.add(
              new Sphere( new Point(10, 15, -80),5).setEmission(new Color(100, 50, 50)).setMaterial(mat),
              new Sphere( new Point(5, 10, -40),5).setEmission(new Color(100, 150, 50)).setMaterial(mat),
              new Sphere( new Point(0, 5, 0),5).setEmission(new Color(50, 50, 100)).setMaterial(mat),
              new Sphere( new Point(-5, 0, 40),5).setEmission(new Color(50, 100, 50)).setMaterial(mat),
              new Sphere( new Point(-10, -5, 80),5).setEmission(new Color(50, 100, 100)).setMaterial(mat)
      );
      Blackboard blackboard = new Blackboard.Builder()
              .setSoftShadows(false)
              .setDepthOfField(true)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();
      Camera.getBuilder().setResolution(1000, 1000)
              .setBlackboard(blackboard)
              .setMultithreading(threadNum) //
              .setRayTracer(scene, RayTracerType.SIMPLE)
              .setVpDistance(100) // Adjusted for a closer view of the scene
              .setVpSize(40, 40)  // Maintain size for consistency
              .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
              .setLocation(new Point(-5, 0, 200)) // Moved closer to the scene for more pronounced depth of field
              .setFocusPointDistance(100) // Adjusted focus point distance for better depth of field effect
              .setAperture(2)  // Decreased aperture to reduce overall blurriness while still showing depth of field

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
              .setDepthOfField(false)
              .setUseCircle(false)
              .setAntiAliasing(false)
              .build();
      Camera.getBuilder()
              .setLocation(new Point(0, 0, 1000)).setVpDistance(1000)
              .setDirection(Point.ZERO, Vector.AXIS_Y)
              .setVpSize(200, 200)
              .setMultithreading(threadNum)
              .setBlackboard(blackboard)
              .setRayTracer(scene, RayTracerType.SIMPLE) //
              .setResolution(600, 600) //
              .build() //
              .renderImage() //
              .writeToImage("superSampling/Soft_Shadows");
   }

   @Test
   void Anti_Aliasing_Test() {
      Scene scene = new Scene("anti-aliasing test")
              .setAmbientLight(new AmbientLight(new Color(WHITE)));
      scene.geometries //
              .add(// center
                      new Sphere(new Point(0, 0, -100), 150).setEmission(new Color(RED)).setMaterial(new Material().setKA(0.25)));
        scene.lights //
                .add(new PointLight(new Color(WHITE), new Point(0, 0, -50)) //
                        .setKc(1).setKl(0.00001).setKq(0.000001));
      Blackboard blackboard = Blackboard.getBuilder().setAntiAliasing(true).build();
      Camera.getBuilder() //
                .setMultithreading(threadNum) //
              .setBlackboard(blackboard)
              .setLocation(new Point(0,0,100))
              .setDirection(Point.ZERO, Vector.AXIS_Y) //
              .setVpDistance(100) //
              .setVpSize(500, 500) //
              .setRayTracer(scene, RayTracerType.SIMPLE) //
              .setResolution(600, 600) //
              .build() //
              .renderImage() //
              .writeToImage("superSampling/Anti_Aliasing");
   }

}
