package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;


/**
 * The `Geometry` class is an abstract base class for all geometric objects.
 * It provides a method to get the normal vector to the geometry at a given point.
 */
public abstract class Geometry extends Intersectable{
    /// The emission color of the geometry
    protected Color emission = Color.BLACK;

    /// The material of the geometry
    private Material material = new Material();

    /**
     * gets the material of the geometry.
     *
     * @return the current geometry object
     */
    public Material getMaterial() {return material;}

    /**
     * Sets the material of the geometry.
     *
     * @param material the material to set
     * @return the current geometry object
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission the emission color to set
     * @return the current geometry object
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Returns the emission color of the geometry.
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }
    /**
     * Returns the normal vector to the geometry at a given point.
     * This method should be implemented by subclasses to provide the specific normal vector calculation.
     *
     * @param point the point on the geometry
     * @return the normal vector to the geometry at the given point
     */
    Vector getNormal(Point point) { return null; }
}