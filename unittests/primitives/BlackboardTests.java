package primitives;

import geometries.Polygon;
import geometries.Sphere;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

class BlackboardTests {
    private final Camera.Builder cameraBuilder = Camera.getBuilder();
    @Test
    void testBlackboard() {
        // ============ Equivalence Partitions Tests ==================
        // TC01: Simple test for Blackboard creation
        Blackboard b= Blackboard.getBuilder().setMethod(Blackboard.MethodsOfPoints.RANDOM).build(new Ray(Point.ZERO, Vector.AXIS_X), new Point(5,0,0));
        for(int i=0; i<b.points.size(); i++){
            System.out.println(b.points.get(i));
        }


    }

}