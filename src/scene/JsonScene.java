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

public class JsonScene {
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
            double ka = ((Number) ambientLightObj.get("ka")).doubleValue();
            scene.setAmbientLight(new AmbientLight(ambientLight, ka));
        }
        if(sceneObj.containsKey("geometries")){
            JSONArray materials = (JSONArray) sceneObj.get("materials");
            scene.geometries.add(parseGeometries((JSONArray) sceneObj.get("geometries"), materials));
        }


        return scene;
    }
    private static double[] parseCoordinates(String coordStr) {
        return Arrays.stream(coordStr.split(" "))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }
    private static Color parseColor(String rgb) {
        double[] colors = parseCoordinates(rgb);
        return new Color(colors[0], colors[1], colors[2]);
    }
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


            geometries.add(geometry);
        }
        return geometries;
    }
    // Primitive parsing methods
    private static Vector parseVector(String vector) {
        double[] coords = parseCoordinates(vector);
        return new Vector(coords[0], coords[1], coords[2]);
    }

    private static Point parsePoint(String pointStr) {
        double[] coords = parseCoordinates(pointStr);
        return new Point(coords[0], coords[1], coords[2]);
    }
    // Geometry parsing methods
    private static Geometry parseTube(JSONObject tube) {
        double radius = ((Number) tube.get("radius")).doubleValue();
        Ray axis = parseRay((JSONObject) tube.get("axis"));
        return new Tube(radius, axis);
    }

    private static Geometry parseCylinder(JSONObject cylinder) {
        double radius = ((Number) cylinder.get("radius")).doubleValue();
        double height = ((Number) cylinder.get("height")).doubleValue();
        Ray axis = parseRay((JSONObject) cylinder.get("axis"));
        return new Cylinder(radius, axis, height);
    }

    private static Ray parseRay(JSONObject axis) {
        Point point = parsePoint((String) axis.get("origin"));
        Vector direction = parseVector((String) axis.get("direction"));
        return new Ray(point, direction);
    }

    private static Geometry parsePolygon(JSONArray polygon) {
        return new Polygon(parseVertices(polygon));
    }

    private static Geometry parseSphere(JSONObject sphereObj) {
        Point center = parsePoint((String) sphereObj.get("center"));
        double radius = ((Number) sphereObj.get("radius")).doubleValue();
        return new Sphere(center, radius);
    }

    private static Geometry parseTriangle(JSONArray triangleObj) {
        Point[] points = parseVertices(triangleObj);
        return new Triangle(points[0], points[1], points[2]);
    }

    private static Geometry parsePlane(JSONObject planeObj) {
        Point point = parsePoint((String) planeObj.get("point"));
        Vector normal = parseVector((String) planeObj.get("normal"));
        return new Plane(point, normal);
    }

    private static Point[] parseVertices(JSONArray vertices) {
        Point[] points = new Point[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            points[i] = parsePoint((String) vertices.get(i));
        }
        return points;
    }

}
