package renderer;


import geometries.Geometry;
import geometries.Sphere;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.JsonScene;
import scene.Scene;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public void snowManTest()
    {
        Scene scene = new Scene("SnowMan").setAmbientLight(new AmbientLight(new Color(java.awt.Color.WHITE)));
        Material snow =
                new Material().setKD(0.7).setKS(0.3);
        scene.geometries.add(
                new Sphere(new Point(0,50,0),50).setMaterial(snow)
        );
        scene.geometries.add(
                new Sphere(new Point(0,110,0),25).setMaterial(snow)
        );
        scene.geometries.add(
                new Sphere(new Point(0,145,0),12.5).setMaterial(snow)
        );
        Camera.getBuilder()
                .setRayTracer(scene,RayTracerType.SIMPLE)
                .setLocation(new Point(0,0,100))
                .setDirection(new Vector(0,0,-1),Vector.AXIS_Y)
                .setVpDistance(100)
                .setResolution(600,600)
                .setVpSize(150,150)
                .build()
                .renderImage()
                .writeToImage("SnowMan");
    }


}
