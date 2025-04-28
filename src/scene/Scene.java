package scene;

import geometries.Geometries;
import lighting.AmbientLight;
import primitives.Color;
/**
 * Represents a 3D scene containing geometries, lighting, and background settings.
 */
public class Scene {
    // The name of the scene
    public String name;
    // The background color of the scene
    public Color background;
    // The ambient light in the scene
    public AmbientLight ambientLight=AmbientLight.NONE;
    // The geometries in the scene
    public Geometries geometries=new Geometries();

    /**
     * Constructs a new Scene with the specified name.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }
    /**
     * Sets the background color of the scene.
     *
     * @param background the background color to set
     * @return the current Scene object
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }
    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the ambient light to set
     * @return the current Scene object
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }
    /**
     * Adds geometries to the scene.
     *
     * @param geometries the geometries to add
     * @return the current Scene object
     */
    public Scene addGeometries(Geometries geometries) {
        this.geometries.add(geometries);
        return this;
    }
}
