package geometries;

import primitives.Point;
import primitives.Vector;

import java.util.*;
import java.util.stream.*;

/**
 * Helper to turn a set of *flat* Polygons into *smooth* (Phong-shaded) ones
 * by computing per-vertex normals automatically.
 */
public class SmoothMeshBuilder {
    private final List<Point[]> faces = new ArrayList<>();

    public SmoothMeshBuilder addFace(Point... verts) {
        faces.add(verts);
        return this;
    }

    public List<Polygon> build() {
        // 1) accumulate un-normalized normals per unique coordinate
        Map<String, Vector> sumMap = new HashMap<>();
        for (Point[] verts : faces) {
            Vector fn = new Plane(verts[0], verts[1], verts[2]).getNormal(verts[0]);
            for (Point v : verts) {
                String key = key(v);
                sumMap.merge(key, fn, Vector::add);
            }
        }

        // 2) normalize each sum
        Map<String, Vector> normalMap = sumMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().normalize()
                ));

        // 3) rebuild Polygons with the correct per-vertex normals
        List<Polygon> result = new ArrayList<>();
        for (Point[] verts : faces) {
            List<Vector> perV = Arrays.stream(verts)
                    .map(v -> normalMap.get(key(v)))
                    .collect(Collectors.toList());
            result.add(new Polygon(perV, verts));
        }
        return result;
    }

    // coordinate key with a fixed precision
    private String key(Point p) {
        return String.format(Locale.ROOT, "%.6f,%.6f,%.6f",
                p.getX(), p.getY(), p.getZ());
    }
}

