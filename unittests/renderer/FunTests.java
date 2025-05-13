package renderer;


import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import scene.JsonScene;
import scene.Scene;

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


}
