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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PhongShadingTests {
    @Test
    void donutSmoothShadingTestTest() {
        // 1) set up scene
        Scene scene = new Scene("donutSmoothShadingTest")
                .setBackground(new Color(0, 0, 0))
                .setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        // 2) camera configuration: closer (y = –120) and lower (z = 20), looking at donut center at z = –100
        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(0, -120, 20))
                .setDirection(new Point(0, 0, -100), Vector.AXIS_Y)
                .setVpDistance(150)
                .setVpSize(200, 200)
                .setMultithreading(-1)
                .setDebugPrint(0.1)
                .setRayTracer(scene, RayTracerType.VOXEL);

        // 3) build a torus (donut) mesh out of quads with per-vertex normals
        int majorSegs = 32, minorSegs = 16;
        double R = 50, r = 15;
        List<Geometry> torus = new ArrayList<>();

        for (int i = 0; i < majorSegs; i++) {
            double phi0 = 2 * Math.PI * i / majorSegs;
            double phi1 = 2 * Math.PI * (i + 1) / majorSegs;
            for (int j = 0; j < minorSegs; j++) {
                double th0 = 2 * Math.PI * j / minorSegs;
                double th1 = 2 * Math.PI * (j + 1) / minorSegs;

                // four points of the quad
                Point p00 = new Point(
                        (R + r * Math.cos(th0)) * Math.cos(phi0),
                        (R + r * Math.cos(th0)) * Math.sin(phi0),
                        r * Math.sin(th0) - 100
                );
                Point p10 = new Point(
                        (R + r * Math.cos(th0)) * Math.cos(phi1),
                        (R + r * Math.cos(th0)) * Math.sin(phi1),
                        r * Math.sin(th0) - 100
                );
                Point p11 = new Point(
                        (R + r * Math.cos(th1)) * Math.cos(phi1),
                        (R + r * Math.cos(th1)) * Math.sin(phi1),
                        r * Math.sin(th1) - 100
                );
                Point p01 = new Point(
                        (R + r * Math.cos(th1)) * Math.cos(phi0),
                        (R + r * Math.cos(th1)) * Math.sin(phi0),
                        r * Math.sin(th1) - 100
                );

                // matching normals
                Vector n00 = new Vector(
                        Math.cos(th0) * Math.cos(phi0),
                        Math.cos(th0) * Math.sin(phi0),
                        Math.sin(th0)
                );
                Vector n10 = new Vector(
                        Math.cos(th0) * Math.cos(phi1),
                        Math.cos(th0) * Math.sin(phi1),
                        Math.sin(th0)
                );
                Vector n11 = new Vector(
                        Math.cos(th1) * Math.cos(phi1),
                        Math.cos(th1) * Math.sin(phi1),
                        Math.sin(th1)
                );
                Vector n01 = new Vector(
                        Math.cos(th1) * Math.cos(phi0),
                        Math.cos(th1) * Math.sin(phi0),
                        Math.sin(th1)
                );

                torus.add(
                        new Polygon(

                                p00, p10, p11, p01
                        )
                                .setEmission(new Color(80, 80, 200))
                                .setMaterial(new Material()
                                        .setKD(new Double3(0.6))
                                        .setKS(0.4)
                                        .setShininess(80))
                );
            }
        }
        scene.geometries.add(torus.toArray(new Geometry[0]));

        // 4) add a single spotlight above the donut
        scene.lights.add(
                new SpotLight(
                        new Color(255, 255, 255),
                        new Point(0, -100, 200),
                        new Vector(0, 1, -0.5)
                )
                        .setKl(1e-4)
                        .setKq(1e-5)
        );

        // 5) render with Phong shading (smooth normals) & anti-aliasing
        camera
                .setBlackboard(new Blackboard.Builder()
                        .setSoftShadows(false)
                        .setDepthOfField(false)
                        .setAntiAliasing(false)
                        .setAdaptiveSampling(false)
                        .setUseCircle(false)
                        .setBlurryAndGlossy(false)
                        .build())
                .setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("PhongShading/donutNoSmoothShadingTest");
    }
    @Test
    void crownShadingPhong() throws IOException {
        // --- 1) Load triangle coords and per-triangle metadata ---
        List<String> triLines  = Files.readAllLines(Paths.get("phongShadingFile/crownData.txt"));
        List<String> metaLines = Files.readAllLines(Paths.get("phongShadingFile/crownMeta.txt"));

        // --- 2) Define JSON materials in code ---
        Material sphereMat = new Material()
                .setKD(new Double3(0.8))   // JSON kd=0.8
                .setKS(0.5)                // JSON ks=0.5
                .setShininess(10);         // JSON ns=10

        Material redMat = new Material()
                .setKD(new Double3(0.15))  // JSON kd=0.15
                .setKS(0.95)               // JSON ks=0.95
                .setShininess(1000);       // JSON ns=1000

        Material goldMat = new Material()
                .setKA(new Double3(0.2))   // JSON ka=0.2
                .setKD(new Double3(0.9))   // JSON kd=0.9
                .setKS(0.9)                // JSON ks=0.9
                .setShininess(300);        // JSON ns=300

        Material[] mats = new Material[]{ sphereMat, redMat, goldMat };

        // --- 3) Build scene & camera ---
        Scene scene = new Scene("CrownSmoothShadingFull")
                .setBackground(new Color(0, 0, 0))
                .setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        Camera.Builder cam = Camera.getBuilder()
                .setLocation(new Point(0, -100, 20))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpDistance(150)
                .setVpSize(200, 200)
                .setMultithreading(-1)
                .setDebugPrint(0.1)
                .setRayTracer(scene, RayTracerType.VOXEL);

        // --- 4) Smooth-mesh builder for triangles ---
        SmoothMeshBuilder mesh = new SmoothMeshBuilder();
        for (String coords : triLines) {
            String[] v = coords.split(",");
            mesh.addFace(
                    new Point(Double.parseDouble(v[0]), Double.parseDouble(v[1]), Double.parseDouble(v[2])),
                    new Point(Double.parseDouble(v[3]), Double.parseDouble(v[4]), Double.parseDouble(v[5])),
                    new Point(Double.parseDouble(v[6]), Double.parseDouble(v[7]), Double.parseDouble(v[8]))
            );
        }
        List<Polygon> tris = mesh.build();

        // --- 5) Apply per-triangle emission & material from metaLines ---
        for (int i = 0; i < tris.size(); i++) {
            Polygon p = tris.get(i);
            String[] m = metaLines.get(i).split(",");
            int er = Integer.parseInt(m[0]),
                    eg = Integer.parseInt(m[1]),
                    eb = Integer.parseInt(m[2]),
                    mi = Integer.parseInt(m[3]);
            p.setEmission(new Color(er, eg, eb))
                    .setMaterial(mats[mi]);
            scene.geometries.add(p);
        }

        // --- 6) Add the 16 spheres with their JSON emission & sphereMat ---
        List<String> sphLines = Files.readAllLines(Paths.get("phongShadingFile/sphereData.txt"));
        for (String line : sphLines) {
            String[] s = line.split(",");
            double cx = Double.parseDouble(s[0]),
                    cy = Double.parseDouble(s[1]),
                    cz = Double.parseDouble(s[2]),
                    r  = Double.parseDouble(s[3]);
            int er = Integer.parseInt(s[4]),
                    eg = Integer.parseInt(s[5]),
                    eb = Integer.parseInt(s[6]);
            scene.geometries.add(
                    new Sphere(new Point(cx, cy, cz), r)
                            .setEmission(new Color(er, eg, eb))
                            .setMaterial(sphereMat)
            );
        }

        // --- 7) Add the base plane ---
        String planeLine = Files.readAllLines(Paths.get("phongShadingFile/planeData.txt")).get(0);
        String[] P = planeLine.split(",");
        Point  pp  = new Point(
                Double.parseDouble(P[0]), Double.parseDouble(P[1]), Double.parseDouble(P[2])
        );
        Vector nn = new Vector(
                Double.parseDouble(P[3]), Double.parseDouble(P[4]), Double.parseDouble(P[5])
        );
        int pr = Integer.parseInt(P[6]),
                pg = Integer.parseInt(P[7]),
                pb = Integer.parseInt(P[8]);
        double kd = Double.parseDouble(P[9]),
                ks = Double.parseDouble(P[10]),
                ns = Double.parseDouble(P[11]),
                kr = Double.parseDouble(P[12]);
        scene.geometries.add(
                new Plane(pp, nn)
                        .setEmission(new Color(pr, pg, pb))
                        .setMaterial(new Material()
                                .setKD(new Double3(kd))
                                .setKS(ks)
                                .setShininess((int)ns)
                                .setKR(kr)
                        )
        );


        scene.lights.add(
                new DirectionalLight(
                        new Color(new Double3(150)),
                        new Vector(0, 1, 0)
                )
        );

        cam.setBlackboard(new Blackboard.Builder()
                        .setSoftShadows(false)
                        .setDepthOfField(false)
                        .setAntiAliasing(true)
                        .setAdaptiveSampling(false)
                        .setUseCircle(false)
                        .setAdaptiveSampling(true)
                        .setBlurryAndGlossy(false)
                        .build())
                .setResolution(800, 800)
                .build()
                .renderImage()
                .writeToImage("PhongShading/crownPhongShading");
    }
    @Test
    void SpherePhongShadingTest() {
        // parameters
        int lat = 15, lon = 30;
        double radius = 50;
        Point center = new Point(0, 0, -100);
        Color emission = new Color(0, 0, 200);
        Material material = new Material()
                .setKD(new Double3(0.6))
                .setKS(0.5)
                .setShininess(100);

        // scene and camera
        Scene scene = new Scene("SmoothLowPolySphere")
                .setBackground(new Color(0,0,0))
                .setAmbientLight(new AmbientLight(new Color(10,10,10)));
        Camera.Builder cam = Camera.getBuilder()
                .setLocation(new Point(0, -200, -50))
                .setDirection(new Point(0, 0, -100), Vector.AXIS_Y)
                .setVpDistance(150)
                .setVpSize(200, 200)
                .setMultithreading(-1)
                .setDebugPrint(0.1)
                .setRayTracer(scene, RayTracerType.VOXEL)
                .setBlackboard(new Blackboard.Builder()
                        .setSoftShadows(false)
                        .setDepthOfField(false)
                        .setAntiAliasing(false)
                        .setAdaptiveSampling(false)
                        .setUseCircle(false)
                        .setBlurryAndGlossy(false)
                        .build());

        // compute vertices and normals
        Point[][] verts = new Point[lat+1][lon];
        Vector[][] norms = new Vector[lat+1][lon];
        for (int i = 0; i <= lat; i++) {
            double theta = Math.PI * i / lat;
            double sinT = Math.sin(theta), cosT = Math.cos(theta);
            for (int j = 0; j < lon; j++) {
                double phi = 2 * Math.PI * j / lon;
                double sinP = Math.sin(phi), cosP = Math.cos(phi);
                Vector n = new Vector(sinT*cosP, sinT*sinP, cosT).normalize();
                verts[i][j] = center.add(n.scale(radius));
                norms[i][j] = n;
            }
        }

        // top cap
        Point topPole = verts[0][0];
        Vector topNorm = norms[0][0];
        for (int j = 0; j < lon; j++) {
            int j2 = (j+1) % lon;
            scene.geometries.add(new Polygon(
                //    List.of(topNorm, norms[1][j], norms[1][j2]),
                    topPole, verts[1][j], verts[1][j2]
            ).setEmission(emission).setMaterial(material));
        }

        // middle bands (up to lat-1)
        for (int i = 1; i < lat - 1; i++) {
            for (int j = 0; j < lon; j++) {
                int j2 = (j+1) % lon;
                scene.geometries.add(new Polygon(
                    //    List.of(norms[i][j], norms[i+1][j], norms[i][j2]),
                        verts[i][j], verts[i+1][j], verts[i][j2]
                ).setEmission(emission).setMaterial(material));
                scene.geometries.add(new Polygon(
                       // List.of(norms[i+1][j], norms[i+1][j2], norms[i][j2]),
                        verts[i+1][j], verts[i+1][j2], verts[i][j2]
                ).setEmission(emission).setMaterial(material));
            }
        }

        // bottom cap
        Point botPole = verts[lat][0];
        Vector botNorm = norms[lat][0];
        for (int j = 0; j < lon; j++) {
            int j2 = (j+1) % lon;
            scene.geometries.add(new Polygon(
                  //  List.of(botNorm, norms[lat-1][j2], norms[lat-1][j]),
                    botPole, verts[lat-1][j2], verts[lat-1][j]
            ).setEmission(emission).setMaterial(material));
        }

        // spotlight
        scene.lights.add(new SpotLight(
                new Color(255,255,255),
                new Point(80, -80, 80),
                new Vector(-1, 1, -1)
        ).setKl(1e-4).setKq(1e-5));

        scene.geometries.add(
                new Plane(
                        new Point(0, 0, -145), Vector.AXIS_Z
                ).setEmission(Color.BLACK)
                        .setMaterial(new Material().setKD(0.7).setKR(0.8).setKS(0.5).setShininess(1)
                )
        );

        // render
        cam.setResolution(600, 600)
                .build()
                .renderImage()
                .writeToImage("PhongShading/SphereNoPhongShading");
    }

}

