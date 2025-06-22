package acceleration;

import geometries.*;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.*;
import org.junit.jupiter.api.Test;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

import static org.junit.jupiter.api.Assertions.*;

class AABBTests {
    @Test
    public void testAABB()
    {
        Scene scene = new Scene("Test Scene");
         Geometries geometries= new Geometries();
         Material material = new Material().setKA(new Double3(1,1,1)).setKD(0.5).setKS(0.5).setShininess(100);
         geometries.add(
                new Sphere( new Point(0, 0.1, 0),20).setMaterial(
                        material
                ),
               new Cylinder(
                        10, new Ray(new Point(40, -20, 0), new Vector(0, 1, 0)),60
               ).setMaterial(material),
                 new Triangle(
                        new Point(-30, -20, -20),
                        new Point(-30, 40, -5),
                        new Point(-50, 40, -5)
                 ).setMaterial(material)
        );
        AABB bigAABB = geometries.getAABB();
        scene.geometries.add(
                new Cube(
                        bigAABB.getMax().getY() - bigAABB.getMin().getY(),
                        bigAABB.getMax().getX() - bigAABB.getMin().getX(),
                        bigAABB.getMax().getZ() - bigAABB.getMin().getZ(),
                        bigAABB.getCenter()).setMaterial(
                        new Material().setKA(new Double3(0, 0, 0.3)).setKT(0.8).setKD(0.2).setKS(0.5).setShininess(100)
                )
        );
        for(Intersectable geometry : geometries.getGeometries()) {
            AABB aabb = geometry.getAABB();
            scene.geometries.add(
                    new Cube(
                            aabb.getMax().getY() - aabb.getMin().getY(),
                           aabb.getMax().getX() - aabb.getMin().getX(),
                           aabb.getMax().getZ() - aabb.getMin().getZ(),
                            aabb.getCenter()).setMaterial(
                            new Material().setKA(new Double3(0, 0, 0.3)).setKT(0.5).setKD(0.2).setKS(0.5).setShininess(100)
                    )
            );
        }


        scene.geometries.add(geometries);
        scene.setAmbientLight(new AmbientLight(new Color(255, 255, 255)));
        scene.setBackground(new Color(255,255,255));
     scene.lights.add(
              new PointLight(
                        new Color(255, 255, 200),
                        new Point(10, 150, -50)
              )
        );
        Camera.getBuilder().
                setMultithreading(-1)
                .setResolution(600,600)
                .setVpDistance(100)
                .setLocation(new Point(0, 80, -150))
                .setVpSize(100,100)
                .setRayTracer(scene, RayTracerType.VOXEL)
                .setDebugPrint(0.1)
                .setDirection(new Vector(0,-0.25,1))
                .build()
                .renderImage()
                .writeToImage("acceleration/AABBTest");

    }
}