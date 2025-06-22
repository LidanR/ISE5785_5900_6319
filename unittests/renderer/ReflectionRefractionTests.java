package renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

import java.util.Random;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 * @author Dan Zilberstein
 */
class ReflectionRefractionTests {
    /** Default constructor to satisfy JavaDoc generator */
    ReflectionRefractionTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene scene = new Scene("Test scene");
    /** Camera builder for the tests with triangles */
    private final Camera.Builder cameraBuilder = Camera.getBuilder()     //
            .setRayTracer(scene, RayTracerType.SIMPLE);

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    void twoSpheres() {
        scene.geometries.add( //
                new Sphere(new Point(0, 0, -50), 50d).setEmission(new Color(BLUE)) //
                        .setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(100).setKT(0.3)), //
                new Sphere(new Point(0, 0, -50), 25d).setEmission(new Color(RED)) //
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))); //
        scene.lights.add( //
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
                        .setKl(0.0004).setKq(0.0000006));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(150, 150) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("Reflections/refractionTwoSpheres");
    }

    /** Produce a picture of a sphere lighted by a spot light */
    @Test
    void twoSpheresOnMirrors() {
        scene.geometries.add( //
                new Sphere(new Point(-950, -900, -1000), 400d).setEmission(new Color(0, 50, 100)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20) //
                                .setKT(new Double3(0.5, 0, 0))), //
                new Sphere(new Point(-950, -900, -1000), 200d).setEmission(new Color(100, 50, 20)) //
                        .setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(670, 670, 3000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKR(1)), //
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
                        new Point(-1500, -1500, -2000)) //
                        .setEmission(new Color(20, 20, 20)) //
                        .setMaterial(new Material().setKR(new Double3(0.5, 0, 0.4))));
        scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26)));
        scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
                .setKl(0.00001).setKq(0.000005));

        cameraBuilder
                .setMultithreading(-1)
                .setLocation(new Point(0, 0, 10000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(10000).setVpSize(2500, 2500) //
                .setResolution(500, 500) //
                .build() //
                .renderImage() //
                .writeToImage("Reflections/reflectionTwoSpheresMirrored");
    }

    /**
     * Produce a picture of a two triangles lighted by a spot light with a
     * partially
     * transparent Sphere producing partial shadow
     */
    @Test
    void trianglesTransparentSphere() {
        scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                new Sphere(new Point(60, 50, -50), 30d).setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKT(0.6)));
        scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
        scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                        .setKl(4E-5).setKq(2E-7));

        cameraBuilder
                .setLocation(new Point(0, 0, 1000)) //
                .setDirection(Point.ZERO, Vector.AXIS_Y) //
                .setVpDistance(1000).setVpSize(200, 200) //
                .setResolution(600, 600) //
                .build() //
                .renderImage() //
                .writeToImage("Reflections/refractionShadow");
    }

    /**
     * “Floating Lantern Courtyard”:
     * Warm, meditative scene of glowing lanterns over a reflective floor.
     */
    @Test
    void floatingLanternCourtyard() {
        // 1. Ambient: very low to highlight lantern glow
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        // 2. Floor: dark reflective polygon (four tiles)
        scene.geometries.add(
                new Polygon(
                        new Point(-100, -100, -80),
                        new Point( 100, -100, -80),
                        new Point( 100,  100, -80),
                        new Point(-100,  100, -80)
                )
                        .setEmission(new Color(30, 30, 30))       // deep charcoal
                        .setMaterial(new Material()
                                .setKD(0.2)                             // some diffuse
                                .setKS(0.7)                             // strong reflection
                                .setKR(0.5)                             // mirror-like floor
                                .setShininess(200))
        );

        // 3. Lanterns: spheres with internal point lights
        double[][] lanternPositions = {
                {-50,  20, -40},
                {  0,  30, -45},
                { 50,  25, -40},
                {-25, -10, -42},
                { 25, -15, -42}
        };
        for (double[] pos : lanternPositions) {
            Point center = new Point(pos[0], pos[1], pos[2]);
            scene.geometries.add(
                    new Sphere(center, 8)
                            .setEmission(new Color(200, 120,  40))         // burnt orange
                            .setMaterial(new Material()
                                    .setKD(0.1)
                                    .setKS(0.9).setShininess(150)
                                    .setKT(0.4))                                  // slight translucency
            );
            // internal glow
            scene.lights.add(
                    new PointLight(new Color(800, 500, 200), center)
                            .setKl(0.0008)
            );
        }

        // 4. Pillars & Beams: ivory triangles framing scene
        scene.geometries.add(
                // left beam
                new Triangle(
                        new Point(-120, -100, -80),
                        new Point(-120,  100, -80),
                        new Point(-115,  100, -40)
                ).setEmission(new Color(220, 210, 200))
                        .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30)),
                // right beam
                new Triangle(
                        new Point(120, -100, -80),
                        new Point(120,  100, -80),
                        new Point(115,  100, -40)
                ).setEmission(new Color(220, 210, 200))
                        .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30))
        );

        // 5. Backdrop wall: subtle warm glow
        scene.geometries.add(
                new Plane(new Point(0, 0, -100), new Vector(0, 0, 1))
                        .setEmission(new Color(50, 40, 40))
                        .setMaterial(new Material()
                                .setKD(0.3)
                                .setKS(0.3).setShininess(50))
        );

        // 6. Fill light from below for gentle upward glow
        scene.lights.add(
                new PointLight(new Color(200, 80, 20), new Point(0, 0, -100))
                        .setKl(0.0005)
        );

        Blackboard blackboard = new Blackboard.Builder()
                .setSoftShadows(false)
                .setDepthOfField(false)
                .setBlurryAndGlossy(false)
                .setUseCircle(false)
                .setAntiAliasing(true)
                .build();
        // 7. Camera: 30° oblique top‑down view
        cameraBuilder
                .setBlackboard(blackboard)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 120, 100))                             // pulled back and up
                .setDirection(
                        new Vector(0, -0.5, -1)                                   // world-up Y
                )
                .setVpDistance(200).setVpSize(200, 200)
                .setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("Reflections/floatingLanternCourtyard");
    }
    @Test
    void artisticPyramidScene() {
        // Set visible ambient light
        scene.setBackground(new Color(0, 0, 0)).setAmbientLight(new AmbientLight(new Color(50, 50, 50)));

        // Ground plane (optional, mostly for background)
        scene.geometries.add(
                new Polygon(new Point(-500, -60, -300),
                        new Point(500, -60, -300),
                        new Point(500, -60, 300),
                        new Point(-500, -60, 300))

                        .setEmission(new Color(61,65,255))
                        .setMaterial(new Material().setKR(0.1).setKD(0.6).setKA(0.1).setKS(0.3).setShininess(30))
        );

        // Sphere directly in front of the camera
        scene.geometries.add(
                new Sphere(new Point(0, -60, -300), 50)
                        .setEmission(new Color(255, 255, 0))
                        .setMaterial(new Material().setKT(0.5).setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
        );
        scene.geometries.add(
                new Sphere(new Point(125, 200, -250), 50)
                        .setEmission(new Color(200, 200, 200))
                        .setMaterial(new Material().setKD(1).setKA(0.1).setKS(0.3).setShininess(50))
        );
        scene.geometries.add(
                new Sphere(new Point(0, -60, -125), 500)
                        .setEmission(new Color(0, 0, 100))
                        .setMaterial(new Material().setKT(0.2).setKD(1).setKA(0.1).setKS(0.3).setShininess(100))
        );
        // Add 10 stars (yellow spheres) at random positions
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < 30; i++) {
            double x = -300 + 600 * rand.nextDouble(); // x in [-100, 100]
            double y = -10 + 250 * rand.nextDouble();  // y in [-60, 100]
            scene.geometries.add(
                    new Sphere(new Point(x, y, -300), 3)
                            .setEmission(new Color(255, 255, 0))
                            .setMaterial(new Material().setKT(0.5).setKD(0.6).setKA(0.1).setKS(0.3).setShininess(50))
            );
        }
        scene.lights.add(
                new SpotLight(new Color(800, 600, 500), new Point(0, -35, -250),new Vector(0,-0.2,0.8))
                        .setKl(0.0001).setKq(0.0002).setNarrowBeam(0.1)
        );
        scene.lights.add(
                new SpotLight(new Color(800, 600, 500), new Point(0, -35, -250),new Vector(0,-0.2,-0.8))
                        .setKl(0.0001).setKq(0.0002).setNarrowBeam(0.1)
        );
        scene.lights.add(
                new PointLight(new Color(800, 600, 500), new Point(0, -35, -250))
                        .setKl(0.0001).setKq(0.0002)
        );

        // Camera pointing straight toward Z-
        cameraBuilder
                .setLocation(new Point(0, 0, 100))          // In front of sphere
                .setDirection(new Vector(0, 0, -1))         // Looking at origin
                .setVpDistance(100)
                .setVpSize(150, 150)
                .setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("Reflections/AvisibleScene");
    }


    @Test
    void Stage7_Test_AnimatedVideo() {
        int numFrames = 60;
        int numSpheres = 50;
        double spiralRadius = 30;
        double heightStep = 2;
        double angleStep = Math.PI / 6;
        double frameAngleShift = Math.PI / 30;

        for (int frame = 0; frame < numFrames; frame++) {
            Scene scene = new Scene("Stage7_Frame_" + frame);

            scene.setBackground(new Color(10, 10, 20))
                    .setAmbientLight(new AmbientLight(new Color(80, 80, 100)));

            Random rand = new Random(42); // Consistent colors
            double globalRotation = frame * frameAngleShift;

            // Lighting
            scene.lights.add(new PointLight(new Color(38, 255, 55), new Point(10, 0, 10)));
            scene.lights.add(new PointLight(new Color(22, 64, 255), new Point(-10, 0, 10)));
            scene.lights.add(new PointLight(new Color(255, 255, 55), new Point(10, 5, -20)));

            // Spiral spheres (rotating)
            for (int i = 0; i < numSpheres; i++) {
                double angle = i * angleStep + globalRotation;
                double x = spiralRadius * Math.cos(angle);
                double z = spiralRadius * Math.sin(angle);
                double y = i * heightStep;

                scene.geometries.add(
                        new Sphere(new Point(x, y, z), 3)
                                .setEmission(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)))
                                .setMaterial(new Material()
                                        .setKA(0.7)
                                        .setKR(0.6)
                                        .setKD(0.1)
                                        .setKS(0.3)
                                        .setShininess(30))
                );
            }

            // Room construction
            double maxRadius = 50;
            double roomYTop = 2 * maxRadius + 5;
            double roomYBottom = -5;
            double roomZFront = 50;
            double roomZBack = -100;
            double roomXLeft = -100;
            double roomXRight = 100;

            Material wallMaterial = new Material().setKA(0.15).setKD(0.25).setKS(0.3).setShininess(20);
            Color wallColor = new Color(25, 25, 30);

            scene.geometries.add(
                    // Floor (mirror)
                    new Polygon(new Point(roomXRight, roomYBottom, roomZFront), new Point(roomXLeft, roomYBottom, roomZFront),
                            new Point(roomXLeft, roomYBottom, roomZBack), new Point(roomXRight, roomYBottom, roomZBack))
                            .setEmission(new Color(30, 30, 50))
                            .setMaterial(new Material().setKA(0.1).setKR(0.9).setKD(0.05).setKS(0.5).setShininess(100)),

                    // Ceiling (mirror)
                    new Polygon(new Point(roomXRight, roomYTop, roomZFront), new Point(roomXLeft, roomYTop, roomZFront),
                            new Point(roomXLeft, roomYTop, roomZBack), new Point(roomXRight, roomYTop, roomZBack))
                            .setEmission(new Color(30, 30, 50))
                            .setMaterial(new Material().setKA(0.1).setKR(0.9).setKD(0.05).setKS(0.5).setShininess(100)),

                    // Walls
                    new Polygon(new Point(roomXRight, roomYBottom, roomZBack), new Point(roomXLeft, roomYBottom, roomZBack),
                            new Point(roomXLeft, roomYTop, roomZBack), new Point(roomXRight, roomYTop, roomZBack))
                            .setEmission(wallColor).setMaterial(wallMaterial),

                    new Polygon(new Point(roomXRight, roomYBottom, roomZFront), new Point(roomXLeft, roomYBottom, roomZFront),
                            new Point(roomXLeft, roomYTop, roomZFront), new Point(roomXRight, roomYTop, roomZFront))
                            .setEmission(wallColor).setMaterial(wallMaterial),

                    new Polygon(new Point(roomXLeft, roomYBottom, roomZFront), new Point(roomXLeft, roomYBottom, roomZBack),
                            new Point(roomXLeft, roomYTop, roomZBack), new Point(roomXLeft, roomYTop, roomZFront))
                            .setEmission(wallColor).setMaterial(wallMaterial),

                    new Polygon(new Point(roomXRight, roomYBottom, roomZBack), new Point(roomXRight, roomYBottom, roomZFront),
                            new Point(roomXRight, roomYTop, roomZFront), new Point(roomXRight, roomYTop, roomZBack))
                            .setEmission(wallColor).setMaterial(wallMaterial)
            );

            // Hourglass pyramids (fixed)
            double pyramidBaseSize = 20;
            double pyramidHeight = 60;
            double midY = (numSpheres * heightStep) / 2.0;

            Point p1 = new Point(-pyramidBaseSize, midY, -pyramidBaseSize);
            Point p2 = new Point(pyramidBaseSize, midY, -pyramidBaseSize);
            Point p3 = new Point(pyramidBaseSize, midY, pyramidBaseSize);
            Point p4 = new Point(-pyramidBaseSize, midY, pyramidBaseSize);

            Point tipBottom = new Point(0, midY + pyramidHeight, 0);
            Point tipTop = new Point(0, midY - pyramidHeight, 0);

            Material glassMaterial = new Material()
                    .setKA(0.1).setKD(0.1).setKS(0.6).setKT(0.9).setShininess(20);

            scene.geometries.add(
                    new Triangle(p1, p2, tipBottom).setMaterial(glassMaterial).setEmission(new Color(RED)),
                    new Triangle(p2, p3, tipBottom).setMaterial(glassMaterial).setEmission(new Color(YELLOW)),
                    new Triangle(p3, p4, tipBottom).setMaterial(glassMaterial).setEmission(new Color(BLUE)),
                    new Triangle(p4, p1, tipBottom).setMaterial(glassMaterial).setEmission(new Color(GREEN)),

                    new Triangle(p1, p2, tipTop).setMaterial(glassMaterial).setEmission(new Color(GREEN)),
                    new Triangle(p2, p3, tipTop).setMaterial(glassMaterial).setEmission(new Color(YELLOW)),
                    new Triangle(p3, p4, tipTop).setMaterial(glassMaterial).setEmission(new Color(BLUE)),
                    new Triangle(p4, p1, tipTop).setMaterial(glassMaterial).setEmission(new Color(RED))
            );

            // Render current frame
            cameraBuilder
                    .setRayTracer(scene, RayTracerType.VOXEL)
                    .setMultithreading(-1)
                    .setDebugPrint(0)
                    .setLocation(new Point(0, maxRadius, -100))
                    .setDirection(new Vector(0, -0.1, 1))
                    .setVpDistance(50)
                    .setVpSize(150, 150)
                    .setResolution(600, 600)
                    .build()
                    .renderImage()
                    .writeToImage("Video_Loop/Stage7_Video_" + frame);
        }

        // Final step: create the video
        try {
            ImagesToVideo.createVideoFromImages("Video_Loop", "Video_Loop/TheVideo/video", 2, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
