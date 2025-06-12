package renderer;

import geometries.Plane;
import geometries.Sphere;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import java.util.List;

class BlackboardTests {
    /**
     * The builder for the Blackboard used in the tests
     */
    Blackboard.Builder blackboardBuilder = Blackboard.getBuilder();
    /**
     * The material of the balls in the tests
     */
    Material ballsMetirial = new Material().setKS(0.5).setKD(0.3).setKA(new Double3(1.0,0.0,0.0));
    /**
     * The distance from the camera to the blackboard
     */
    double distance = 100;

    /**
     * Test for the grid method of points in the Blackboard
     */
    @Test
    public void gridMethodTest()
    {
        Blackboard blackboard = blackboardBuilder.setMethod(Blackboard.MethodsOfPoints.GRID).setAmountOfRays(16).build();
        List<Ray> rays = blackboard.constructRays(new Ray(Point.ZERO,Vector.AXIS_Z),distance);
        makeScene(rays,"Blackboard_Grid");
    }
    /**
     * Test for the random method of points in the Blackboard
     */
    @Test
    public void randomMethodTest()
    {
        Blackboard blackboard = blackboardBuilder.setMethod(Blackboard.MethodsOfPoints.RANDOM).build();
        List<Ray> rays = blackboard.constructRays(new Ray(Point.ZERO,Vector.AXIS_Z),distance);
        makeScene(rays,"Blackboard_Random");
    }
    /**
     * Test for the jittered method of points in the Blackboard
     */
    @Test
    public void JitterMethodTest() {
        Blackboard blackboard = blackboardBuilder.setMethod(Blackboard.MethodsOfPoints.JITTERED).setAmountOfRays(8).build();
        List<Ray> rays = blackboard.constructRays(new Ray(Point.ZERO,Vector.AXIS_Z),distance);
        makeScene(rays,"Blackboard_Jitter");
    }
    /**
     * Test for the circle method of points in the Blackboard
     */
    @Test
    public void CircleMethodTest()
    {
        Blackboard blackboard = blackboardBuilder.setMethod(Blackboard.MethodsOfPoints.GRID).setUseCircle(true).build();
        List<Ray> rays = blackboard.constructRays(new Ray(Point.ZERO,Vector.AXIS_Z),distance);
        makeScene(rays,"Blackboard_Circle");
    }

    /**
     * Helper function to create a scene with the given rays and name
     * @param rays the rays to use in the scene
     * @param name the name of the scene, used for the image file name
     */
    private void makeScene(List<Ray> rays,String name)
    {
        Scene scene = new Scene(name);
        scene.setAmbientLight(new AmbientLight(new Color(java.awt.Color.WHITE)));
        Point planeLocation = new Point(0,0,100);
        scene.geometries.add(
                new Plane(planeLocation,new Vector(0,0,-1))
        );
        for(Ray ray : rays)
        {
            Point sphereLocation =Vector.AXIS_Z.scale(distance);
            if(!ray.getDirection().equals(Vector.AXIS_Z))
                    sphereLocation = sphereLocation.subtract(ray.getPoint(distance)).scale(200);
            scene.geometries.add(
                    new Sphere(sphereLocation,5).setMaterial(ballsMetirial)
            );
        }
        Camera.getBuilder()
                .setLocation(new Point(0,0,-200))
                .setDirection(new Vector(0,0,1),Vector.AXIS_Y)
                .setVpDistance(100)
                .setResolution(600,600)
                .setVpSize(150,150)
                .setMultithreading(-1)
                .setRayTracer(scene,RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("blackBoard/"+name);
    }
}