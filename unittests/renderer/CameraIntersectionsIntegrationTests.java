package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.*;
import geometries.*;

import java.util.List;

public class CameraIntersectionsIntegrationTests {

    @Test
    void testCameraIntersectionsWithSphere() {

      // Test case 1: Sphere is in front of the camera
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1),new Vector(0, 1, 0))
                .setVpSize(3,3)
                .setResolution(3,3)
                .setVpDistance(1)
                .build();
        Sphere sphere1 = new Sphere(new Point(0,0,-3), 1);
        assertEquals(2, MakeIntersectionPixels(camera1,sphere1), "Sphere in front of camera should have 2 intersection points");

        // Test case 2: view plane is inside the sphere
        Camera camera_2_3_4_5 = Camera.getBuilder()
                .setLocation(new Point(0,0,0.5))
                .setDirection(new Vector(0, 0, -1),new Vector(0, 1, 0))
                .setVpSize(3,3)
                .setResolution(3,3)
                .setVpDistance(1)
                .build();
        Sphere sphere2 = new Sphere(new Point(0,0,-2.5), 2.5);
        assertEquals(18, MakeIntersectionPixels(camera_2_3_4_5,sphere2), "Camera inside sphere should have 18 intersection points");

        // Test case 3: View plane is inside the sphere
        Sphere sphere3 = new Sphere(new Point(0,0,-2), 2);
        assertEquals(10, MakeIntersectionPixels(camera_2_3_4_5,sphere3), "View plane inside sphere should have 10 intersection points");

        // Test case 4: Camera is inside the sphere
        Sphere sphere4 = new Sphere(new Point(0,0,-2), 4);
        assertEquals(9, MakeIntersectionPixels(camera_2_3_4_5,sphere4), "Camera inside sphere should have 9 intersection points");

        // Test case 5: Sphere is behind the camera
        Sphere sphere5 = new Sphere(new Point(0,0,1), 0.5);
        assertEquals(0, MakeIntersectionPixels(camera1,sphere5), "Sphere behind camera should have 0 intersection points");
    }

    @Test
    void testCameraIntersectionsWithTriangle() {
        // Test case 1: Triangle is in front of the camera and is the size of a pixel
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1),new Vector(0, 1, 0))
                .setVpSize(3,3)
                .setResolution(3,3)
                .setVpDistance(1)
                .build();
        Triangle triangle1 = new Triangle(new Point(-1, -1, -2), new Point(1, -1, -2), new Point(0, 1, -2));
        assertEquals(1, MakeIntersectionPixels(camera1,triangle1), "Triangle in front of camera should have 1 intersection point");
        // Test case 2: Triangle is in front of the camera and is the size of the view plane
        Triangle triangle2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertEquals(2, MakeIntersectionPixels(camera1,triangle2), "Triangle in front of camera should have 3 intersection points");

    }


    @Test
    void testCameraIntersectionsWithPlane() {
        // Test case 1: Plane is in front of the camera and orthogonal to the view plane
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1),new Vector(0, 1, 0))
                .setVpSize(3,3)
                .setResolution(3,3)
                .setVpDistance(1)
                .build();
        Plane plane1 = new Plane(new Point(0, 0, -2), new Vector(0, 0, -1));
        assertEquals(9, MakeIntersectionPixels(camera,plane1), "Plane in front of camera should have 9 intersection points");

        // Test case 2: Plane is in front of the camera and has a different angle
        Plane plane2 = new Plane(new Point(0, 0, -2), new Point(0, 5, -1.5),new Point(1,-20, -4));
        assertEquals(9, MakeIntersectionPixels(camera,plane2), "Plane in front of camera with different angle should have 9 intersection points");

        // Test case 3: Plane is in front of the camera and has a different angle
        Plane plane3 = new Plane(new Point(0, 0, -2), new Point(0, 10, -1.5),new Point(1,5, -4));
        assertEquals(6, MakeIntersectionPixels(camera,plane3), "Plane in front of camera with different angle should have 6 intersection points");

    }

    private double MakeIntersectionPixels(Camera camera,Geometry geometry) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Ray ray = camera.constructRay(3, 3, i, j);
                List<Point> intersections = geometry.findIntersections(ray);
                if (intersections != null) {
                    count+= intersections.size();
                }
            }
        }
        return count;
    }
}
