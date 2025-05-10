package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static primitives.Util.*;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 * @author Dan
 */
public class Polygon extends Geometry {
   /** List of polygon's vertices */
   protected final List<Point> vertices;
   /** Associated plane in which the polygon lays */
   protected final Plane       plane;
   /** The size of the polygon - the amount of the vertices in the polygon */
   private final int           size;

   /**
    * Polygon constructor based on vertices list. The list must be ordered by edge
    * path. The polygon must be convex.
    * @param  vertices                 list of vertices according to their order by
    *                                  edge path
    * @throws IllegalArgumentException in any case of illegal combination of
    *                                  vertices:
    *                                  <ul>
    *                                  <li>Less than 3 vertices</li>
    *                                  <li>Consequent vertices are in the same
    *                                  point
    *                                  <li>The vertices are not in the same
    *                                  plane</li>
    *                                  <li>The order of vertices is not according
    *                                  to edge path</li>
    *                                  <li>Three consequent vertices lay in the
    *                                  same line (180&#176; angle between two
    *                                  consequent edges)
    *                                  <li>The polygon is concave (not convex)</li>
    *                                  </ul>
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      this.vertices = List.of(vertices);
      size          = vertices.length;

      // Generate the plane according to the first three vertices and associate the
      // polygon with this plane.
      // The plane holds the invariant normal (orthogonal unit) vector to the polygon
      plane         = new Plane(vertices[0], vertices[1], vertices[2]);
      if (size == 3) return; // no need for more tests for a Triangle

      Vector  n        = plane.getNormal(vertices[0]);
      // Subtracting any subsequent points will throw an IllegalArgumentException
      // because of Zero Vector if they are in the same point
      Vector  edge1    = vertices[size - 1].subtract(vertices[size - 2]);
      Vector  edge2    = vertices[0].subtract(vertices[size - 1]);

      // Cross Product of any subsequent edges will throw an IllegalArgumentException
      // because of Zero Vector if they connect three vertices that lay in the same
      // line.
      // Generate the direction of the polygon according to the angle between last and
      // first edge being less than 180deg. It is hold by the sign of its dot product
      // with the normal. If all the rest consequent edges will generate the same sign
      // - the polygon is convex ("kamur" in Hebrew).
      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < size; ++i) {
         // Test that the point is in the same plane as calculated originally
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
         // Test the consequent edges have
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }
   }

   @Override
   public Vector getNormal(Point point) { return plane.getNormal(point); }


   /**
    * Finds the intersection points between the polygon and a given ray.
    * @param ray the ray to intersect with the polygon
    * @return a list of intersection points between the ray and the polygon,
    */
   @Override
   protected List<Intersection> findIntersectionsHelper(Ray ray) {
// Create a list to store the normals formed by the polygon's sides
      List<Vector> edgeNormals = new LinkedList<>();

      // Extract the origin and direction vector of the given ray
      final Point rayOrigin = ray.getHead();
      final Vector rayDirection = ray.getDir();

      // Compute a normal vector for each side of the polygon
      Vector previousVector = vertices.getFirst().subtract(rayOrigin);
      for (Point vertex : vertices.subList(1, size)) {
         Vector currentVector = vertex.subtract(rayOrigin);
         edgeNormals.add(previousVector.crossProduct(currentVector).normalize());
         previousVector = currentVector;
      }
      // Handle the last side connecting the last and first vertices
      edgeNormals.add(vertices.getLast().subtract(rayOrigin).crossProduct(vertices.getFirst().subtract(rayOrigin)).normalize());

      // Check if the ray points to the same side for all edges
      boolean isInitiallyPositive = rayDirection.dotProduct(edgeNormals.getFirst()) > 0;
      for (Vector normal : edgeNormals) {
         double dot = rayDirection.dotProduct(normal);
         // If dot product is zero or the sign is inconsistent, no intersection
         if (Util.isZero(dot) || (dot > 0 != isInitiallyPositive)) {
            return null;
         }
      }

      // Build a plane using the first three vertices of the polygon
      Plane basePlane = new Plane(vertices.getFirst(), vertices.get(1), vertices.get(2));

      // Find intersection points between the ray and the constructed plane
      return Objects.requireNonNull(basePlane.findIntersections(ray)).stream()
              .map(point -> new Intersection(this, point))
              .toList();
   }
}
