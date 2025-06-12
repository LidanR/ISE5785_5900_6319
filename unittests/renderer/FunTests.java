package renderer;


import geometries.*;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.JsonScene;
import scene.Scene;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FunTests {
    /**
     * Camera builder of the tests
     */
    private final Camera.Builder camera = Camera.getBuilder();

    @Test
    public void crown() {
//        assertDoesNotThrow(() -> {
//            Scene scene = JsonScene.CreateScene("jsonScenes/crown.json");
//
//            camera
//
//                    .setRayTracer(scene, RayTracerType.SIMPLE)
//                    .setResolution(1000, 1000) //
//                    .setDirection(new Vector(0, 1, -0.1).normalize(), new Vector(0, 1, 10).normalize())
//                    .setLocation(new Point(0, -320, 40))
//                    .setVpDistance(500)
//                    .setVpSize(150, 150)
//                    .build()
//                    .renderImage()
//                    .writeToImage("crown");
//
//        }, "Failed to render image");
    }
    @Test
    public void diamondRing() {
        assertDoesNotThrow(() -> {
                    Scene scene = JsonScene.CreateScene("jsonScenes/diamondRing.json");
                    Blackboard blackboard = new Blackboard.Builder()
                            .setSoftShadows(false)
                            .setDepthOfField(false)
                            .setUseCircle(true)
                            .setAntiAliasing(true)
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
        Color stoneColor = new Color(192, 192, 192);
        Material stoneMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(100);
        double radius = 15;
        double cornersPivot = 0.65 * radius;
        double height = 4;
        double width = 4;
        double depth = 8;

        scene.geometries.add(
                new Cube(height, width, depth, new Point(radius, 0, 0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(-radius, 0, 0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(0, 0, radius), new Double3(0,90,0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(0, 0, -radius), new Double3(0, 90, 0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(-cornersPivot, 0, cornersPivot), new Double3(0, 45, 0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(cornersPivot, 0, -cornersPivot), new Double3(0, 45, 0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(cornersPivot, 0, cornersPivot), new Double3(0, 135, 0)).setMaterial(stoneMaterial).setEmission(stoneColor),
                new Cube(height, width, depth, new Point(-cornersPivot, 0, -cornersPivot), new Double3(0, 135, 0)).setMaterial(stoneMaterial).setEmission(stoneColor)
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
           new PointLight(new Color(255,140,0), new Point(0, 30, -30),12)
        );
        // </editor-fold>
        scene.geometries.add(
                new Sphere(new Point(0, 0, 0), 40)
                        .setEmission(new Color(10, 10, 10))
                        .setMaterial(new Material().setKT(1.0))
        );
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

// Add fixed cylinder (you can keep or randomize this too)
        scene.geometries.add(
                new Cylinder(5, new Ray(new Point(15, 0, 62), new Vector(0, 1, 0)), 80)
                        .setEmission(new Color(88, 40, 39))
                        .setMaterial(woodMaterial)
        );

        // <editor-fold desc="Lake">
        Double3 lakeCenter = new Double3(-50, 0, 75);
        double lakeRadius = 45;

        scene.geometries.add(
                new Cylinder(
                        lakeRadius,
                        new Ray(new Point(lakeCenter.d1(), 0, lakeCenter.d3()), Vector.AXIS_Y),
                        0.1
                ).setEmission(new Color(0, 0, 255)) // Blue color for water
                        .setMaterial(new Material().setKD(0.1).setKR(0.7).setKS(0.9).setShininess(300).setKT(0.8)
                )
        );
        // </editor-fold>

        // <editor-fold desc="Moon">
        double moonRadius = 10;
        Point moonCenter = new Point(-30, 100, -100);
        scene.geometries.add(
                new Sphere(moonCenter, moonRadius)
                        .setEmission(new Color(255, 255, 224)) // Light yellow color
                        .setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(300).setKT(0.5))
        );


        // <editor-fold desc="Camera & Render">
        Blackboard blackboard = new Blackboard.Builder()
                .setSoftShadows(false)
                .setDepthOfField(false)
                .setUseCircle(false)
                .setAntiAliasing(false)
                .setBlurryAndGlossy(false)
                .setAmountOfRays(5)
                .build();

        final Camera.Builder camera = Camera.getBuilder()
                .setBlackboard(blackboard)
                .setMultithreading(-1)
                .setDebugPrint(0.1)
                .setLocation(new Point(0,30,100))
                .setDirection(new Vector(0, -0.1, 0))
                .setVpDistance(100)
                .setResolution(1000, 1000)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setVpSize(150, 150);

        camera.build()
                .renderImage()
                .writeToImage("Final_Minip_Test");
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
}
