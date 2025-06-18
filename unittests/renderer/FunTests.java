package renderer;


import geometries.*;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.JsonScene;
import scene.Scene;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FunTests {
    /**
     * Camera builder of the tests
     */
    private final Camera.Builder camera = Camera.getBuilder();

    @Test
    public void DiamondBox() {
        assertDoesNotThrow(() -> {
            Scene scene1 = JsonScene.CreateScene("jsonScenes/multydiamonds.json");
            camera
                    .setResolution(1000, 1000)
                    .setRayTracer(scene1, RayTracerType.VOXEL)
                    .setDebugPrint(0.1)
                    .setDirection(new Vector(0, 1, -0.1).normalize(), new Vector(0, 1, 10).normalize())
                    .setLocation(new Point(0, -350, 45))//Point(0, 130, 30)
                    .setVpDistance(500)
                    .setVpSize(150, 150)
                    .setMultithreading(-1)
                    .build()
                    .renderImage()
                    .writeToImage("multi diamond");

        }, "Failed to render image");
    }

    @Test
    public void crown() {
        assertDoesNotThrow(() -> {
            Scene scene = JsonScene.CreateScene("jsonScenes/crown.json");
            Blackboard blackboard = new Blackboard.Builder()
                    .setSoftShadows(false)
                    .setDepthOfField(false)
                    .setUseCircle(true)
                    .setAntiAliasing(false)
                    .build();
            camera
                    .setBlackboard(blackboard)
                    .setRayTracer(scene, RayTracerType.VOXEL)
                    .setResolution(3000, 3000) //
                     .setMultithreading(-1)
                    .setDebugPrint(0.1)
                    .setDirection(new Vector(0, 1, -0.1).normalize(), new Vector(0, 1, 10).normalize())
                    .setLocation(new Point(0, -320, 40))
                    .setVpDistance(500)
                    .setVpSize(150, 150)
                    .orbitAroundTargetVertical(-45, 315)
                    .build()
                    .renderImage()
                    .writeToImage("crown");

        }, "Failed to render image");
    }
    @Test
    public void DiamondRing() {
        assertDoesNotThrow(() -> {
                    Scene scene = JsonScene.CreateScene("jsonScenes/diamondRing.json");
                    Blackboard blackboard = new Blackboard.Builder()
                            .setSoftShadows(false)
                            .setDepthOfField(false)
                            .setUseCircle(true)
                            .setAntiAliasing(false)
                            .build();
                    final Camera.Builder camera = Camera.getBuilder()
                            .setBlackboard(blackboard)
                            .setMultithreading(-1)
                            .setDebugPrint(1)
                            .setDirection(new Vector(0, 1, -0.1).normalize(), new Vector(0, 0.1, 1).normalize())
                            .setLocation(new Point(0, -350, 60))//Point(0, 130, 30)
                            .setVpDistance(500)
                            .setResolution(1000,1000)
                            .setRayTracer(scene, RayTracerType.SIMPLE)
                            .setVpSize(150, 150);

                    camera
                            //.orbitAroundTargetHorizontal(180, 350)
                            .orbitAroundTargetVertical(-120, 315)
                            .build()
                            .renderImage()
                            .writeToImage("DiamondRing");
                }, "Failed to render image"
        );
    }
    @Test
    public void Final_Minip_Test() {
        Scene scene = new Scene("Final Minip Test")
                .setBackground(new Color(0, 0, 0))
                .setAmbientLight(new AmbientLight(new Color(5, 5, 5))); // Very low ambient light

        // <editor-fold desc="Stone Circle">
        Material stoneMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(100);
        double radius = 15;
        double cornersPivot = 0.65 * radius;
        double height = 4;
        double width = 4;
        double depth = 8;

        scene.geometries.add(
                new Cube(height, width, depth, new Point(radius, 0, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(-radius, 0, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(0, 0, radius), new Double3(0,90,0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(0, 0, -radius), new Double3(0, 90, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(-cornersPivot, 0, cornersPivot), new Double3(0, 45, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(cornersPivot, 0, -cornersPivot), new Double3(0, 45, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(cornersPivot, 0, cornersPivot), new Double3(0, 135, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200)))),
                new Cube(height, width, depth, new Point(-cornersPivot, 0, -cornersPivot), new Double3(0, 135, 0)).setMaterial(stoneMaterial).setEmission(new Color(new Double3(new Random().nextInt(50,200))))
        );
        // </editor-fold>

        // <editor-fold desc="Fire - Layered Flame Core">
        Material flameMaterial = new Material().setKD(0.1).setKS(0.8).setShininess(400).setKT(0.3);
        Color[] flameColors = {
                new Color(255, 60, 0),
                new Color(255, 120, 0),
                new Color(255, 180, 20),
                new Color(255, 255, 100),
                new Color(255, 255, 180)
        };

        int flameLayers = 8;
        int flameInstancesPerLayer = 14;
        for (int layer = 0; layer < flameLayers; layer++) {
            double baseY = layer * 2.2;
            double radiusLayer = 6.0 - layer * 0.6;
            Color color = flameColors[Math.min(layer, flameColors.length - 1)];

            for (int i = 0; i < flameInstancesPerLayer; i++) {
                double offsetX = Math.random() * radiusLayer * 0.5 - radiusLayer * 0.25;
                double offsetZ = Math.random() * radiusLayer * 0.5 - radiusLayer * 0.25;
                double jitterY = Math.random() * 0.7;

                scene.geometries.add(
                        new Sphere(new Point(offsetX, baseY + jitterY, offsetZ), radiusLayer)
                                .setEmission(color)
                                .setMaterial(flameMaterial)
                );
            }
        }

        // Add glowing core
        scene.geometries.add(
                new Sphere(new Point(0, 2.5, 0), 5.5)
                        .setEmission(new Color(255, 100, 10))
                        .setMaterial(new Material().setKD(0.05).setKS(0.9).setShininess(500)),
                new Sphere(new Point(0, 5, 0), 3.5)
                        .setEmission(new Color(255, 180, 30))
                        .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(300))
        );
        scene.geometries.add(
                new Sphere(new Point(0, 0, 0), 40)
                        .setEmission(new Color(10, 10, 10))
                        .setMaterial(new Material().setKT(1.0))
        );
        // </editor-fold>

        // <editor-fold desc="Fireflies - Mini glowing floating lights">
        int fireflyCount = 20;
        Material fireflyMaterial = new Material().setKD(0.0).setKS(1.0).setShininess(300);
        for (int i = 0; i < fireflyCount; i++) {
            double x = Math.random() * 60 - 30;
            double z = Math.random() * 60 - 30;
            double y = 4 + Math.random() * 20;
            Color glowColor = Math.random() > 0.5 ? new Color(255, 255, 120) : new Color(150, 255, 180);

            scene.geometries.add(
                    new Sphere(new Point(x, y, z), 0.4)
                            .setEmission(glowColor)
                            .setMaterial(fireflyMaterial)
            );


        }
        // </editor-fold>

        // <editor-fold desc="Smoke Column">
        Material smokeMaterial = new Material().setKD(0.1).setKS(0.1).setShininess(50).setKT(0.7);
        for (int i = 0; i < 50; i++) {
            double y = 6 + i * 1.8;
            double radiusSmoke = 1.5 + Math.random() * 1.5;
            double x = Math.random() * 1.8 - 0.9;
            double z = Math.random() * 1.8 - 0.9;
            scene.geometries.add(
                    new Sphere(new Point(x, y, z), radiusSmoke)
                            .setEmission(new Color(80, 80, 80))
                            .setMaterial(smokeMaterial)
            );
        }
        // </editor-fold>

        // <editor-fold desc="Ground and Decoration">
        scene.geometries.add(
                new Plane(new Point(0, -height / 2, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(15, 45, 15))
                        .setMaterial(new Material().setKD(0.5).setKS(0.2).setStrength(20).setShininess(100))
        );

        Material woodMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(200);
        scene.geometries.add(
                new Cylinder(5, new Ray(new Point(50, 0, -30), new Vector(-0.7, 0, -1)), 40)
                        .setEmission(new Color(88, 57, 39))
                        .setMaterial(woodMaterial)
        );
        scene.geometries.add(
                new Cylinder(5, new Ray(new Point(-50, 0, -30), new Vector(0.7, 0, -1)), 40)
                        .setEmission(new Color(88, 57, 39))
                        .setMaterial(woodMaterial)
        );
        // </editor-fold>

        // <editor-fold desc="Light from Fire Only">
        scene.lights.add(
           new PointLight(new Color(255,140,0), new Point(0, 30, -30),10)
        );
        // </editor-fold>

        // <editor-fold desc="Trees">

        // Place trees in a circular arc behind the campfire
        Random rand = new Random();

        double minRadius = 90;   // closest to the campfire
        double maxRadius = 700;  // farthest from the campfire
        int treeCount = 300;

        double startAngle = Math.toRadians(150);
        double endAngle = Math.toRadians(250);

        for (int i = 0; i < treeCount; i++) {
            // Random angle within arc
            double angle = startAngle + rand.nextDouble() * (endAngle - startAngle);

            // Random radius within the range
            double treeRadius = minRadius + rand.nextDouble() * (maxRadius - minRadius);

            // Compute base position
            double baseX = treeRadius * Math.cos(angle);
            double baseZ = treeRadius * Math.sin(angle);

            // Add small jitter
            double jitterX = rand.nextDouble() * 10 - 5;
            double jitterZ = rand.nextDouble() * 10 - 5;

            double x = baseX + jitterX;
            double z = baseZ + jitterZ;

            Point treePosition = new Point(z, 0, x); // (z,x) per your convention

            // Optional: add size variation
            double trunkHeight = 20 + rand.nextDouble() * 2;
            double trunkRadius = 4 + rand.nextDouble() ;
            double leavesHeight = 70 + rand.nextDouble() * 3;

            for (Geometry g : createTree(treePosition, trunkHeight, trunkRadius, trunkRadius+20, leavesHeight)) {
                scene.geometries.add(g);
            }
        }
        // </editor-fold>
        // <editor-fold desc="stars">

        int starCount = 100;
        for (int i = 0; i < starCount; i++) {
            double x = rand.nextDouble(-150,150);
            double y = Math.random() * 100 + 60;
            double z = -100-Math.abs(Math.random() * 100 - 50);
            Color starColor = new Color(255, 255, 255);

            scene.geometries.add(
                    new Sphere(new Point(x, y, z), 0.5)
                            .setEmission(starColor)
                            .setMaterial(new Material().setKD(0.0).setKS(1.0).setShininess(300))
            );
        }

        // </editor-fold>


        // <editor-fold desc="Tent">

        for (int i = 5; i < 100; i += 10) {
            // Generate realistic brown RGB components
            int r = 80 + rand.nextInt(25);   // 80–160
            int g = 40 + rand.nextInt(20);   // 40–110
            int b = 20 + rand.nextInt(25);   // 20–60

            scene.geometries.add(
                    new Cylinder(5, new Ray(new Point(40, i, 100), new Vector(-0.7, 0, -1)), 40)
                            .setEmission(new Color(r, g, b))
                            .setMaterial(woodMaterial)
            );
        }
        scene.geometries.add(
                new Cylinder(5, new Ray(new Point(15, 0, 62), new Vector(0, 1, 0)), 80)
                        .setEmission(new Color(88, 40, 39))
                        .setMaterial(woodMaterial)
        );
        // </editor-fold>


        // <editor-fold desc="Moon">
        double moonRadius = 10;
        Point moonCenter = new Point(-30, 100, -100);
        scene.geometries.add(
                new Sphere(moonCenter, moonRadius)
                        .setEmission(new Color(255, 255, 224)) // Light yellow color
                        .setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(300).setKR(0.5))
        );
        //</editor-fold>

        // <editor-fold desc="Shovel">
        Material shovelMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(200);
        Color shovelColor = new Color(100, 100, 100); // Metallic gray color
        scene.geometries.add(
                new Cylinder(1, new Ray(new Point(-25, 5, 60), new Vector(0, 0.9, 0)), 30)
                        .setEmission(new Color(80,40,20))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(200)));
        scene.geometries.add(
                new Cube(
                        2, 3, 4, new Point(-25, 35, 60), new Double3(0, 110, 0))
                        .setEmission(shovelColor)
                        .setMaterial(shovelMaterial)
        );
        scene.geometries.add(
                new Cube(
                        20, 10, 2, new Point(-25, -5, 60), new Double3(0, 20, 0))
                        .setEmission(shovelColor)
                        .setMaterial(shovelMaterial)
        );
// Create a pile of dirt around the shovel
Material dirtMaterial = new Material().setKD(0.8).setKR(0.1).setKS(0.2).setShininess(100);
Color dirtColor = new Color(80, 40, 20); // Earthy brown color

// Base larger spheres for the main dirt pile
Point dirtCenter = new Point(-25, -2, 60); // Center point of the dirt pile
for (int i = 0; i < 15; i++) {
    double angle = rand.nextDouble() * 2 * Math.PI;
    double distance = rand.nextDouble() * 4; // Radius of the dirt pile
    double x = dirtCenter.getX() + Math.cos(angle) * distance;
    double z = dirtCenter.getZ() + Math.sin(angle) * distance;
    double y = dirtCenter.getY() + rand.nextDouble() * 1.5; // Slight height variation

    scene.geometries.add(
            new Sphere(new Point(x, y, z), 1.0 + rand.nextDouble() * 0.5)
                    .setEmission(dirtColor)
                    .setMaterial(dirtMaterial)
    );
}

// Smaller particles and details for more natural appearance
for (int i = 0; i < 20; i++) {
    double angle = rand.nextDouble() * 2 * Math.PI;
    double distance = rand.nextDouble() * 6; // Wider spread for smaller particles
    double x = dirtCenter.getX() + Math.cos(angle) * distance;
    double z = dirtCenter.getZ() + Math.sin(angle) * distance;
    double y = dirtCenter.getY() + rand.nextDouble() * 2.0;

    // Slight color variations for realism
    scene.geometries.add(
            new Sphere(new Point(x, y, z), 0.3 + rand.nextDouble() * 0.3)
                    .setEmission(new Color(
                            dirtColor.getColor().getRed() + rand.nextInt(-10, 10),
                            dirtColor.getColor().getGreen() + rand.nextInt(-5, 5),
                            dirtColor.getColor().getBlue() + rand.nextInt(-5, 5))
                    )
                    .setMaterial(dirtMaterial)
    );
}
        // </editor-fold>



        // <editor-fold desc="Camera & Render">
        Blackboard blackboard = new Blackboard.Builder()
                .setSoftShadows(false)
                .setDepthOfField(false)
                .setUseCircle(true)
                .setAntiAliasing(false)
                .setBlurryAndGlossy(false)
                .build();

        final Camera.Builder camera = Camera.getBuilder()
                .setBlackboard(blackboard)
                .setMultithreading(-1)
                .setDebugPrint(0.1)
                .setLocation(new Point(0,30,100))
                .setFocusPointDistance(110)
                .setDirection(new Vector(0.02, -0.1, 0))
                .setVpDistance(100)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.VOXEL)
                .setAperture(0.2)
                .setVpSize(150, 150);

        camera.build()
                .renderImage()
                .writeToImage("Final_Minip_Test_DOF_2");
        // </editor-fold>
    }

    private List<Geometry> createTree(Point position, double height, double radius,double leafRadius,double leafHeight) {
        Material trunkMaterial = new Material().setKD(0.6).setKS(0.4).setShininess(200);
        Material leafMaterial = new Material().setKD(0.2).setKS(0.8).setShininess(300);
        Random rand = new Random();
        int r = 80 + rand.nextInt(20);   // 80–160
        int g = 40 + rand.nextInt(20);   // 40–110
        int b = 20 + rand.nextInt(20);   // 20–60
        // Create trunk
       Geometry trunk = new Cylinder(radius, new Ray(position, new Vector(0, 1, 0)), height)
                       .setEmission(new Color(r, g, b))
                       .setMaterial(trunkMaterial);
       // Create leaves (as Triangle)
        Geometry Leave1 = new Triangle(
                position.add(Vector.AXIS_Y.scale(leafHeight)),
                position.add(Vector.AXIS_Y.scale(height).add(Vector.AXIS_X.scale(leafRadius))),
                position.add(Vector.AXIS_Y.scale(height).add(Vector.AXIS_X.scale(-leafRadius))));
         Geometry Leave2 = new Triangle(
                position.add(Vector.AXIS_Y.scale(leafHeight)),
                position.add(Vector.AXIS_Y.scale(height).add(Vector.AXIS_Z.scale(leafRadius))),
                position.add(Vector.AXIS_Y.scale(height).add(Vector.AXIS_Z.scale(-leafRadius))));
         Geometry Leave3 = new Triangle(
                position.add(Vector.AXIS_Y.scale(height)),
                position.add(Vector.AXIS_Y.scale(height).add(Vector.AXIS_X.scale(leafRadius)).add(Vector.AXIS_Z.scale(leafRadius))),
                position.add(Vector.AXIS_Y.scale(height+0.01).add(Vector.AXIS_X.scale(-leafRadius)).add(Vector.AXIS_Z.scale(-leafRadius))));
         Geometry Leave4 = new Triangle(
                position.add(Vector.AXIS_Y.scale(height)),
                position.add(Vector.AXIS_Y.scale(height).add(Vector.AXIS_X.scale(leafRadius)).add(Vector.AXIS_Z.scale(-leafRadius))),
                position.add(Vector.AXIS_Y.scale(height+0.01).add(Vector.AXIS_X.scale(-leafRadius)).add(Vector.AXIS_Z.scale(leafRadius))));

         // Random Green Color for leaves
            Color randomGreen = new Color(rand.nextInt(100, 150), rand.nextInt(100, 200), rand.nextInt(50, 100));
            // Set emission and material for leaves
         Leave1.setEmission(randomGreen).setMaterial(leafMaterial);
            Leave2.setEmission(randomGreen).setMaterial(leafMaterial);
            Leave3.setEmission(randomGreen).setMaterial(leafMaterial);
            Leave4.setEmission(randomGreen).setMaterial(leafMaterial);
         // Add leaves to the trunk
        return List.of(trunk, Leave1, Leave2, Leave3,Leave4);

    }


    @Test
    public void blackHoleInSpace_video() {
        int numFrames = 60;
        int numRings = 3;
        int spheresPerRing = 50;
        double blackHoleRadius = 8;
        double innerDiskRadius = blackHoleRadius + 2;
        double outerDiskRadius = innerDiskRadius + 10;
        double frameAngleShift = Math.PI * 2 / numFrames;

        for (int frame = 0; frame < numFrames; frame++) {
            Scene scene = new Scene("BlackHole_Frame_" + frame)
                    .setBackground(new Color(5, 5, 15))
                    .setAmbientLight(new AmbientLight(new Color(25, 25, 35)));

            // Central black hole (perfect reflector to simulate light bending)
            scene.geometries.add(
                    new Sphere(new Point(0, 0, 0), blackHoleRadius)
                            .setEmission(Color.BLACK)
                            .setMaterial(new Material()
                                    .setKD(0.0)
                                    .setKR(1.0)
                            )
            );

            Random rand = new Random(42);

            // Accretion disk (animated glowing particles)
            for (int ring = 0; ring < numRings; ring++) {
                double radius = innerDiskRadius + ring * ((outerDiskRadius - innerDiskRadius) / (numRings - 1));
                for (int i = 0; i < spheresPerRing; i++) {
                    double angle = i * 2 * Math.PI / spheresPerRing + frame * frameAngleShift * 0.5;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    // Random rich glowing color
                    Color glow = new Color(
                            180 + rand.nextInt(60),
                            80 + rand.nextInt(100),
                            30 + rand.nextInt(80)
                    );

                    // Random reflective/transmissive disk particles
                    Material mat = new Material()
                            .setKD(0.4)
                            .setKS(0.8)
                            .setShininess(120)
                            .setKR(rand.nextDouble() < 0.2 ? 0.5 : 0)
                            .setKT(rand.nextDouble() < 0.2 ? 0.5 : 0);

                    scene.geometries.add(
                            new Sphere(new Point(x, 0, z), 0.8)
                                    .setEmission(glow)
                                    .setMaterial(mat)
                    );
                }
            }

            // Stars and cosmic depth
            for (int i = 0; i < 150; i++) {
                double x = -120 + rand.nextDouble() * 240;
                double y = -100 + rand.nextDouble() * 200;
                double z = -120 + rand.nextDouble() * 240;

                Color starColor = new Color(
                        100 + rand.nextInt(155),
                        100 + rand.nextInt(155),
                        200 + rand.nextInt(55)
                );

                scene.geometries.add(
                        new Sphere(new Point(x, y, z), 0.2)
                                .setEmission(starColor)
                                .setMaterial(new Material().setKA(1))
                );
            }

            // Multi-directional lighting to simulate lensing and galactic glow
            scene.lights.addAll(List.of(
                    new PointLight(new Color(255, 200, 255), new Point(50, 0, -50)).setKl(0.01).setKq(0.001),
                    new PointLight(new Color(255, 150, 150), new Point(-50, 30, -50)).setKl(0.01).setKq(0.002),
                    new PointLight(new Color(180, 220, 255), new Point(70, 40, 40)).setKl(0.01).setKq(0.0015),
                    new PointLight(new Color(120, 120, 255), new Point(-70, -50, 70)).setKl(0.01).setKq(0.002)
            ));

            // Camera rotation
            double cameraAngle = frame * frameAngleShift;
            double camX = 40 * Math.cos(cameraAngle);
            double camZ = 40 * Math.sin(cameraAngle);
            Point camLocation = new Point(camX, 15, camZ);
            Vector camDirection = new Point(0, 0, 0).subtract(camLocation).normalize();

            Camera.getBuilder()
                    .setRayTracer(scene, RayTracerType.VOXEL)
                    .setMultithreading(-1)
                    .setDebugPrint(0)
                    .setLocation(camLocation)
                    .setDirection(camDirection)
                    .setVpDistance(50)
                    .setVpSize(100, 100)
                    .setResolution(600, 600)
                    .build()
                    .renderImage()
                    .writeToImage("Video_BlackHole/BlackHole_Frame_" + frame);
        }

        try {
            ImagesToVideo.createVideoFromImages("Video_BlackHole", "Video_BlackHole/TheVideo/blackhole", 1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void solarSystem_video() {
        int numFrames = 120;
        double angleStep = 2 * Math.PI / numFrames;

        record Planet(String name, Color color, double orbitRadius, double radius, double speedFactor) {}
        List<Planet> planets = List.of(
                new Planet("Mercury", new Color(169, 169, 169), 10, 0.5, 4.7),
                new Planet("Venus", new Color(255, 215, 0), 15, 0.9, 3.5),
                new Planet("Earth", new Color(0, 100, 255), 20, 1.0, 2.9),
                new Planet("Mars", new Color(255, 60, 60), 25, 0.8, 2.4),
                new Planet("Jupiter", new Color(205, 133, 63), 32, 2.2, 1.3),
                new Planet("Saturn", new Color(218, 165, 32), 40, 2.0, 1.0),
                new Planet("Uranus", new Color(72, 209, 204), 47, 1.7, 0.7),
                new Planet("Neptune", new Color(70, 130, 180), 54, 1.7, 0.5)
        );

        Random rand = new Random(42);

        for (int frame = 0; frame < numFrames; frame++) {
            Scene scene = new Scene("SolarSystem_Frame_" + frame)
                    .setBackground(new Color(3, 3, 10))
                    .setAmbientLight(new AmbientLight(new Color(30, 30, 45)));

            // Glowing sun
            scene.geometries.add(
                    new Sphere(new Point(0, 0, 0), 5)
                            .setEmission(new Color(255, 255, 180))
                            .setMaterial(new Material().setKA(0.5).setKD(0.2).setKS(1.0).setShininess(300).setKR(0.5))
            );

            // Multiple lights around the solar system
            scene.lights.addAll(List.of(
                    new PointLight(new Color(1000, 900, 600), new Point(0, 0, 0)).setKl(0.0004).setKq(0.0001),
                    new PointLight(new Color(400, 200, 700), new Point(60, 50, -60)).setKl(0.001).setKq(0.002),
                    new PointLight(new Color(200, 255, 255), new Point(-70, 30, 80)).setKl(0.001).setKq(0.002),
                    new PointLight(new Color(255, 150, 255), new Point(100, 100, 100)).setKl(0.0015).setKq(0.003)
            ));

            // Planets
            for (Planet p : planets) {
                double angle = angleStep * frame * p.speedFactor;
                double x = p.orbitRadius * Math.cos(angle);
                double z = p.orbitRadius * Math.sin(angle);

                Material mat = new Material()
                        .setKA(0.2)
                        .setKD(0.5)
                        .setKS(0.7)
                        .setKT(rand.nextDouble() < 0.25 ? 0.6 : 0)  // ~25% transparent
                        .setKR(rand.nextDouble() < 0.25 ? 0.4 : 0)  // ~25% reflective
                        .setShininess(70 + rand.nextInt(100));

                scene.geometries.add(
                        new Sphere(new Point(x, 0, z), p.radius)
                                .setEmission(p.color)
                                .setMaterial(mat)
                );

                // Earth's moon
                if (p.name.equals("Earth")) {
                    double moonAngle = angle * 12;
                    double moonX = x + 1.5 * Math.cos(moonAngle);
                    double moonZ = z + 1.5 * Math.sin(moonAngle);
                    scene.geometries.add(
                            new Sphere(new Point(moonX, 0, moonZ), 0.3)
                                    .setEmission(new Color(200, 200, 200))
                                    .setMaterial(new Material().setKA(0.2).setKD(0.5).setKS(0.5).setShininess(30))
                    );
                }

                // Saturn’s rings
                if (p.name.equals("Saturn")) {
                    for (int i = 0; i < 3; i++) {
                        double ringRadius = p.radius + 0.3 + i * 0.2;
                        for (int j = 0; j < 36; j++) {
                            double theta = Math.toRadians(j * 10);
                            double rx = x + ringRadius * Math.cos(theta);
                            double rz = z + ringRadius * Math.sin(theta);
                            scene.geometries.add(
                                    new Sphere(new Point(rx, 0, rz), 0.1)
                                            .setEmission(new Color(200, 180, 140))
                                            .setMaterial(new Material().setKA(0.1))
                            );
                        }
                    }
                }
            }

            // Asteroid belt
            for (int i = 0; i < 300; i++) {
                double r = 28 + rand.nextDouble() * 4;
                double angle = rand.nextDouble() * 2 * Math.PI;
                double x = r * Math.cos(angle);
                double z = r * Math.sin(angle);
                scene.geometries.add(
                        new Sphere(new Point(x, 0, z), 0.15)
                                .setEmission(new Color(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200)))
                                .setMaterial(new Material().setKA(0.2))
                );
            }

            // Colorful animated stars
            for (int i = 0; i < 100; i++) {
                double x = -100 + rand.nextDouble() * 200;
                double y = -100 + rand.nextDouble() * 200;
                double z = -100 + rand.nextDouble() * 200;
                scene.geometries.add(
                        new Sphere(new Point(x, y, z), 0.2)
                                .setEmission(new Color(100 + rand.nextInt(155), 100 + rand.nextInt(155), 255))
                                .setMaterial(new Material().setKA(1))
                );
            }

            // Orbiting camera
            double camAngle = frame * angleStep;
            double camX = 100 * Math.cos(camAngle);
            double camZ = 100 * Math.sin(camAngle);
            Point camPos = new Point(camX, 40, camZ);
            Vector camDir = new Point(0, 0, 0).subtract(camPos).normalize();

            Camera.getBuilder()
                    .setRayTracer(scene, RayTracerType.VOXEL)
                    .setMultithreading(-1)
                    .setDebugPrint(0)
                    .setLocation(camPos)
                    .setDirection(camDir)
                    .setVpDistance(50)
                    .setVpSize(150, 150)
                    .setResolution(800, 800)
                    .build()
                    .renderImage()
                    .writeToImage("SolarSystem_Video/Frame_" + frame);
        }

        try {
            ImagesToVideo.createVideoFromImages("SolarSystem_Video", "SolarSystem_Video/TheVideo/solar_system", 3, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
