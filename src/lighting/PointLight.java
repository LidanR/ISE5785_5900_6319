package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {
    protected Point position;
    private double KC;
    private double KL;
    private double KQ;
}
