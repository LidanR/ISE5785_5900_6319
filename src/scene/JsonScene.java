package scene;

import geometries.*;
import lighting.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import primitives.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * JsonScene is a utility class that provides methods to create a Scene object from a JSON file.
 * It parses the JSON data and constructs the corresponding geometries, lights, and other scene elements.
 */
public class JsonScene {
    /**
     * Creates a Scene object from a JSON file.
     *
     * @param path the path to the JSON file
     * @return a Scene object constructed from the JSON data
     * @throws IOException if there is an error reading the file
     * @throws ParseException if there is an error parsing the JSON data
     */
    public static Scene CreateScene(String path) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
        JSONObject sceneObj = (JSONObject) jsonObject.get("scene");

        String name = (String) sceneObj.get("name");
        Scene scene = new Scene(name);
        if(sceneObj.containsKey("background-color"))
            scene.setBackground(parseColor((String) sceneObj.get("background-color")));
        if(sceneObj.containsKey("ambient-light"))
        {
            JSONObject ambientLightObj = (JSONObject) sceneObj.get("ambient-light");
            Color ambientLight = parseColor((String) ambientLightObj.get("color"));
            scene.setAmbientLight(new AmbientLight(ambientLight));
        }
        if(sceneObj.containsKey("geometries")){
            JSONArray materials = (JSONArray) sceneObj.get("materials");
            scene.geometries.add(parseGeometries((JSONArray) sceneObj.get("geometries"), materials));
        }

        if(sceneObj.containsKey("lights"))
            scene.setLights(parseLights((JSONArray) sceneObj.get("lights")));

        return scene;
    }
    /**
     * Parses a JSON array of lights and returns a list of LightSource objects.
     *
     * @param lights the JSON array containing the lights
     * @return a list of LightSource objects constructed from the JSON data
     */
    private static List<LightSource> parseLights(JSONArray lights) {
        List<LightSource> lightSources = new LinkedList<>();
        for (Object obj : lights) {
            JSONObject lightObj = (JSONObject) obj;
            if (lightObj.containsKey("point")) {
                lightSources.add(parsePointLight((JSONObject) lightObj.get("point")));
            } else if (lightObj.containsKey("directional")) {
                lightSources.add(parseDirectionalLight((JSONObject) lightObj.get("directional")));
            } else if (lightObj.containsKey("spot")) {
                lightSources.add(parseSpotLight((JSONObject) lightObj.get("spot")));
            } else {
                throw new IllegalArgumentException("Unknown light type");
            }
        }
        return lightSources;
    }
    /**
     * Parses a JSON object representing a spot light and returns a SpotLight object.
     *
     * @param lightObj the JSON object representing the spot light
     * @return a SpotLight object constructed from the JSON data
     */
    private static LightSource parseSpotLight(JSONObject lightObj) {
        Color color = parseColor((String) lightObj.get("color"));
        Point position = parsePoint((String) lightObj.get("position"));
        Vector direction = parseVector((String) lightObj.get("direction"));
        SpotLight spotLight = new SpotLight(color,position, direction);
        if (lightObj.containsKey("kc")) {
            spotLight.setKc(((Number) lightObj.get("kc")).doubleValue());
        }
        if (lightObj.containsKey("kl")) {
            spotLight.setKl(((Number) lightObj.get("kl")).doubleValue());
        }
        if (lightObj.containsKey("kq")) {
            spotLight.setKq(((Number) lightObj.get("kq")).doubleValue());
        }
        if (lightObj.containsKey("narrow-beam")) {
            spotLight.setNarrowBeam(((Number) lightObj.get("narrow-beam")).doubleValue());
        }
        return spotLight;
    }

    private static LightSource parseDirectionalLight(JSONObject lightObj) {
        Color color = parseColor((String) lightObj.get("color"));
        Vector direction = parseVector((String) lightObj.get("direction"));
        return new DirectionalLight(color, direction);
    }

    private static LightSource parsePointLight(JSONObject lightObj) {
        Color color = parseColor((String) lightObj.get("color"));
        Point position = parsePoint((String) lightObj.get("position"));
        PointLight pointLight = new PointLight(color, position);
        if (lightObj.containsKey("kc")) {
            pointLight.setKc(((Number) lightObj.get("kc")).doubleValue());
        }
        if (lightObj.containsKey("kl")) {
            pointLight.setKl(((Number) lightObj.get("kl")).doubleValue());
        }
        if (lightObj.containsKey("kq")) {
            pointLight.setKq(((Number) lightObj.get("kq")).doubleValue());
        }

        return pointLight;
    }
    /**
     * Parses a string of coordinates into an array of doubles.
     *
     * @param coordStr the string containing the coordinates
     * @return an array of doubles representing the coordinates
     */
    private static double[] parseCoordinates(String coordStr) {
        return Arrays.stream(coordStr.split(" "))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }
    /**
     * Parses a string representation of a color into a Color object.
     *
     * @param rgb the string representation of the color
     * @return a Color object constructed from the string
     */
    private static Color parseColor(String rgb) {
        double[] colors = parseCoordinates(rgb);
        return new Color(colors[0], colors[1], colors[2]);
    }
    /**
     * Parses a JSON array of geometries and returns a Geometries object.
     *
     * @param geometriesArray the JSON array containing the geometries
     * @return a Geometries object constructed from the JSON data
     */
    private static Geometries parseGeometries(JSONArray geometriesArray, JSONArray materials) {
        Geometries geometries = new Geometries();
        for (Object obj : geometriesArray) {
            JSONObject geometryObj = (JSONObject) obj;
            Geometry geometry;
            if (geometryObj.containsKey("sphere")) {
                geometry = parseSphere((JSONObject) geometryObj.get("sphere"));
            } else if (geometryObj.containsKey("triangle")) {
                geometry = parseTriangle((JSONArray) geometryObj.get("triangle"));
            } else if (geometryObj.containsKey("plane")) {
                geometry = parsePlane((JSONObject) geometryObj.get("plane"));
            } else if (geometryObj.containsKey("polygon")) {
                geometry = parsePolygon((JSONArray) geometryObj.get("polygon"));
            } else if (geometryObj.containsKey("cylinder")) {
                geometry = parseCylinder((JSONObject) geometryObj.get("cylinder"));
            } else if (geometryObj.containsKey("tube")) {
                geometry = parseTube((JSONObject) geometryObj.get("tube"));
            } else {
                throw new IllegalArgumentException("Unknown geometry type");
            }

            if (geometryObj.containsKey("material"))
                parseMaterial(geometryObj, geometry, materials);

            if(geometryObj.containsKey("emission"))
                geometry.setEmission(parseColor((String) geometryObj.get("emission")));

            geometries.add(geometry);
        }
        return geometries;
    }

    /**
     * Parses the material properties from a JSON object and sets them to the geometry.
     * @param geometryObj the JSON object representing the geometry
     * @param geometry the geometry object to set the material for
     * @param materials the JSON array containing the materials
     */
    private static void parseMaterial(JSONObject geometryObj, Geometry geometry, JSONArray materials) {

        Object objCheck = geometryObj.get("material");
        JSONObject materialObj = null;
        if (objCheck instanceof String) {
            int materialIndex = Integer.parseInt((String) objCheck);
            materialObj = (JSONObject) materials.get(materialIndex);
        } else {
            materialObj = (JSONObject) objCheck;
        }

        Material material = new Material();
        if (materialObj.containsKey("kd")) {
            if(materialObj.get("kd") instanceof Number)
                material.setKd(((Number) materialObj.get("kd")).doubleValue());
            else{
                String[] kd = ((String) materialObj.get("kd")).split(" ");
                Double3 kdColor = new Double3(Double.parseDouble(kd[0]), Double.parseDouble(kd[1]), Double.parseDouble(kd[2]));
                material.setKd(kdColor);
            }
        }
        if (materialObj.containsKey("ks")) {
            if(materialObj.get("ks") instanceof Number)
                material.setKd(((Number) materialObj.get("ks")).doubleValue());
            else{
                String[] ks = ((String) materialObj.get("ks")).split(" ");
                Double3 ksColor = new Double3(Double.parseDouble(ks[0]), Double.parseDouble(ks[1]), Double.parseDouble(ks[2]));
                material.setKd(ksColor);
            }
        }
        if (materialObj.containsKey("ns")) {
            material.setShininess(((Number) materialObj.get("ns")).intValue());
        }
        if (materialObj.containsKey("ka")) {
            material.setKa(((Number) materialObj.get("ka")).doubleValue());
        }

        geometry.setMaterial(material);
    }

    /**
     * Parses a string representation of a vector into a Vector object.
     * @param vector the string representation of the vector
     * @return a Vector object constructed from the string
     */
    private static Vector parseVector(String vector) {
        double[] coords = parseCoordinates(vector);
        return new Vector(coords[0], coords[1], coords[2]);
    }
    /**
     * Parses a string representation of a point into a Point object.
     * @param pointStr the string representation of the point
     * @return a Point object constructed from the string
     */
    private static Point parsePoint(String pointStr) {
        double[] coords = parseCoordinates(pointStr);
        return new Point(coords[0], coords[1], coords[2]);
    }

    /**
     * Parses a JSON object representing a tube and returns a Tube object.
     *
     * @param tube the JSON object representing the tube
     * @return a Tube object constructed from the JSON data
     */
    private static Geometry parseTube(JSONObject tube) {
        double radius = ((Number) tube.get("radius")).doubleValue();
        Ray axis = parseRay((JSONObject) tube.get("axis"));
        return new Tube(radius, axis);
    }
    /**
     * Parses a JSON object representing a cylinder and returns a Cylinder object.
     *
     * @param cylinder the JSON object representing the cylinder
     * @return a Cylinder object constructed from the JSON data
     */
    private static Geometry parseCylinder(JSONObject cylinder) {
        double radius = ((Number) cylinder.get("radius")).doubleValue();
        double height = ((Number) cylinder.get("height")).doubleValue();
        Ray axis = parseRay((JSONObject) cylinder.get("axis"));
        return new Cylinder(radius, axis, height);
    }
    /**
     * Parses a JSON object representing a ray and returns a Ray object.
     *
     * @param axis the JSON object representing the ray
     * @return a Ray object constructed from the JSON data
     */
    private static Ray parseRay(JSONObject axis) {
        Point point = parsePoint((String) axis.get("origin"));
        Vector direction = parseVector((String) axis.get("direction"));
        return new Ray(point, direction);
    }
    /**
     * Parses a JSON array representing a polygon and returns a Polygon object.
     *
     * @param polygon the JSON array representing the polygon
     * @return a Polygon object constructed from the JSON data
     */
    private static Geometry parsePolygon(JSONArray polygon) {
        return new Polygon(parseVertices(polygon));
    }
    /**
     * Parses a JSON object representing a sphere and returns a Sphere object.
     *
     * @param sphereObj the JSON object representing the sphere
     * @return a Sphere object constructed from the JSON data
     */
    private static Geometry parseSphere(JSONObject sphereObj) {
        Point center = parsePoint((String) sphereObj.get("center"));
        double radius = ((Number) sphereObj.get("radius")).doubleValue();
        return new Sphere(center, radius);
    }
    /**
     * Parses a JSON array representing a triangle and returns a Triangle object.
     *
     * @param triangleObj the JSON array representing the triangle
     * @return a Triangle object constructed from the JSON data
     */
    private static Geometry parseTriangle(JSONArray triangleObj) {
        Point[] points = parseVertices(triangleObj);
        return new Triangle(points[0], points[1], points[2]);
    }
    /**
     * Parses a JSON object representing a plane and returns a Plane object.
     *
     * @param planeObj the JSON object representing the plane
     * @return a Plane object constructed from the JSON data
     */
    private static Geometry parsePlane(JSONObject planeObj) {
        Point point = parsePoint((String) planeObj.get("point"));
        Vector normal = parseVector((String) planeObj.get("normal"));
        return new Plane(point, normal);
    }
    /**
     * Parses a JSON array of vertices and returns an array of Point objects.
     *
     * @param vertices the JSON array containing the vertices
     * @return an array of Point objects constructed from the JSON data
     */
    private static Point[] parseVertices(JSONArray vertices) {
        Point[] points = new Point[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            points[i] = parsePoint((String) vertices.get(i));
        }
        return points;
    }
}
