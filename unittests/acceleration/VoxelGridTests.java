package acceleration;

import geometries.*;
import lighting.AmbientLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.*;
import renderer.RayTracerType;
import scene.Scene;

import java.util.List;

class VoxelGridTests {
    @Test
    public void test3DDDA()
    {
        Scene scene = new Scene("Test Scene");
        scene.setAmbientLight(new AmbientLight(new Color(255, 255, 255)));
        scene.setBackground(new Color(255,255,255));

        int nx = 20, ny = 20, nz = 20;
        double cubeSize = 5;
        Geometries cubes = new Geometries();
        for (int i = 0; i < nx; i++) {
                for (int k = 0; k < nz; k++) {
                    Point position = new Point(i * cubeSize,  cubeSize, k * cubeSize + 60);
                    cubes.add(new Cube(cubeSize, cubeSize, cubeSize, position).setMaterial(
                            new Material().setKA(new Double3(0,0,0.3)).setKD(0.5).setKT(0.1).setKS(0.5).setShininess(100)
                    ));
                }

        }

        AABB sceneBounds = cubes.getAABB();
        VoxelGrid voxelGrid = new VoxelGrid(sceneBounds, nx, ny, nz);

        for (Intersectable geometry : cubes.getGeometries()) {
            voxelGrid.addObject(geometry, geometry.getAABB());
        }
        double xOffset = -0.3;
        double zOffset = 1;
        // Shoot a test ray to color intersected cubes
        Ray ray = new Ray(new Point(nx * cubeSize / 2.0, cubeSize, 0), new Vector(xOffset, 0, zOffset));
        List<Intersectable.Intersection> intersects = voxelGrid.findAllIntersections(ray, Double.POSITIVE_INFINITY);
        for (Intersectable.Intersection i : intersects) {
            i.material.setKA(new Double3(0, 0, 1)).setKD(0); // highlight intersections
        }
        scene.geometries = cubes;
        // Represent the ray as a tube
        scene.geometries.add(
                new Tube(
                        0.5,new Ray(new Point(nx * cubeSize / 2.0, cubeSize*2, 0), new Vector(xOffset, 0, zOffset))
                ).setMaterial(new Material()
                .setKA(new Double3(1,0,0))
                        .setKD(0.5)
                        .setKS(0.5)
                        .setShininess(100))
        );


        // Camera from a diagonal angle
        Point cameraLocation = new Point(nx * cubeSize / 2.0, ny * cubeSize *1.3, 60 + nz * cubeSize / 2.0);
        Vector to = new Vector(0,-1,0);
        Vector up = new Vector(0, 0, 1);

        Camera cam = Camera.getBuilder().setDebugPrint(1)
                .setMultithreading(-1)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.VOXEL)
                .setVpSize(100, 100)
                .setLocation(cameraLocation)
                .setVpDistance(100)
                .setDirection(to,up)
                .build();

        cam.renderImage().writeToImage("acceleration/3DDDATest");
    }



}