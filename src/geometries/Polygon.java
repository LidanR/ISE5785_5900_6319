package geometries;

import acceleration.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static primitives.Util.isZero;

/**
 * Polygon class represents a two-dimensional convex polygon in 3D space,
 * with optional per-vertex normals for smooth (Phong) shading.
 */
public class Polygon extends Geometry {
   /** The ordered list of polygon vertices */
   protected final List<Point> vertices;
   /** Optional per-vertex normals (same order as vertices) */
   private final List<Vector> vertexNormals;
   /** The underlying plane in which the polygon lies */
   protected final Plane plane;
   /** Number of vertices */
   private final int size;
   /** Axis-aligned bounding box, built lazily */
   private AABB box;

   /**
    * Flat-shaded polygon: no per-vertex normals, uses a single plane normal.
    * @param vertices list of vertices in edge order (must be convex)
    */
   public Polygon(Point... vertices) {
      this(null, vertices);
   }

   /**
    * Smooth-shaded polygon: supply one normal per vertex.
    * @param normals   per-vertex normals (will be normalized)
    * @param vertices  list of vertices in edge order (must be convex)
    * @throws IllegalArgumentException if normals list is null/wrong size or vertices invalid
    */
   public Polygon(List<Vector> normals, Point... vertices) {
      // validate normals argument
      if (normals != null && normals.size() != vertices.length) {
         throw new IllegalArgumentException("Must supply exactly one normal per vertex");
      }
      this.vertexNormals = (normals == null ? null
              : normals.stream().map(Vector::normalize).toList());
      // copy vertices
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon must have at least 3 vertices");
      this.vertices = List.of(vertices);
      this.size     = vertices.length;

      // construct the plane
      this.plane = new Plane(vertices[0], vertices[1], vertices[2]);

      // if more than triangle, verify coplanarity and convexity
      if (size > 3) {
         Vector n     = plane.getNormal(vertices[0]);
         Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
         Vector edge2 = vertices[0].subtract(vertices[size - 1]);
         boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
         for (int i = 1; i < size; ++i) {
            // coplanarity
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
               throw new IllegalArgumentException("All vertices must lie in the same plane");
            // convexity
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
               throw new IllegalArgumentException("Polygon must be convex and vertices ordered");
         }
      }
   }

   /**
    * Returns the normal at point p:
    * – flat shading: constant plane normal
    * – smooth shading: barycentric-interpolated per-vertex normal
    */
   @Override
   public Vector getNormal(Point p) {
      if (vertexNormals == null) {
         // flat
         return plane.getNormal(p);
      }
      // smooth: find triangle (v0, vi, vi+1) that contains p and compute barycentric coords
      Point A = vertices.get(0);
      for (int i = 1; i < size - 1; i++) {
         Point B = vertices.get(i);
         Point C = vertices.get(i + 1);
         // vectors for barycentric
         Vector v0 = B.subtract(A);
         Vector v1 = C.subtract(A);
         Vector v2 = p.subtract(A);
         double d00 = v0.dotProduct(v0);
         double d01 = v0.dotProduct(v1);
         double d11 = v1.dotProduct(v1);
         double d20 = v2.dotProduct(v0);
         double d21 = v2.dotProduct(v1);
         double denom = d00 * d11 - d01 * d01;
         if (isZero(denom)) continue; // degenerate
         double v = (d11 * d20 - d01 * d21) / denom;
         double w = (d00 * d21 - d01 * d20) / denom;
         double u = 1 - v - w;
         if (u >= 0 && v >= 0 && w >= 0) {
            Vector nA = vertexNormals.get(0).scale(u);
            Vector nB = vertexNormals.get(i).scale(v);
            Vector nC = vertexNormals.get(i + 1).scale(w);
            return nA.add(nB).add(nC).normalize();
         }
      }
      // fallback
      return plane.getNormal(p);
   }

   /**
    * Ray–polygon intersection helper: first checks winding to see if the ray
    * goes through the polygon, then intersects with the underlying plane.
    */
   @Override
   protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
      // 1) build edge-based normals relative to ray origin
      List<Vector> edgeNormals = new LinkedList<>();
      Point  rayOrigin    = ray.getHead();
      Vector rayDirection = ray.getDirection();

      // first edge: v0 → v1
      Vector prev = vertices.get(0).subtract(rayOrigin);
      for (int i = 1; i < size; i++) {
         Vector curr = vertices.get(i).subtract(rayOrigin);
         edgeNormals.add(prev.crossProduct(curr).normalize());
         prev = curr;
      }
      // last edge: last → first
      Vector first = vertices.get(0).subtract(rayOrigin);
      edgeNormals.add(prev.crossProduct(first).normalize());

      // 2) ensure rayDirection is consistently on the same side of all edges
      boolean initialPositive = rayDirection.dotProduct(edgeNormals.get(0)) > 0;
      for (Vector n : edgeNormals) {
         double d = rayDirection.dotProduct(n);
         if (isZero(d) || (d > 0) != initialPositive) {
            return null; // misses polygon
         }
      }

      // 3) intersect underlying plane
      List<Point> pts = plane.findIntersections(ray, maxDistance);
      if (pts == null) return null;

      // 4) wrap in Intersection objects
      return pts.stream()
              .map(p -> new Intersection(this, p))
              .toList();
   }

   /**
    * Lazily builds and returns the axis-aligned bounding box of this polygon.
    */
   @Override
   public AABB getAABB() {
      if (box == null) {
         double minX = Double.POSITIVE_INFINITY, minY = minX, minZ = minX;
         double maxX = Double.NEGATIVE_INFINITY, maxY = maxX, maxZ = maxX;
         for (Point p : vertices) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
         }
         box = new AABB(
                 new Point(minX, minY, minZ),
                 new Point(maxX, maxY, maxZ)
         );
      }
      return box;
   }
}
