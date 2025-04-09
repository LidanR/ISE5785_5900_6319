package renderer;

import primitives.*;

import java.util.MissingResourceException;

public class Camera implements Cloneable
{
    private Point position;
    private Vector Vto;
    private Vector Vup;
    private Vector Vright;
    private double distance = 0;
    private double width= 0;
    private double height= 0;

    public  static class Builder{
        private final Camera cam = new Camera();
        public Builder setLocation(Point p)
        {
            cam.position = p;
            return this;
        }
     public Builder setDirection(Vector vTo, Vector vUp) {
         if (!vTo.crossProduct(vUp).equals(Vector.ZERO)) {
             throw new IllegalArgumentException("vTo and vUp must be orthogonal");
         }
         cam.Vto = vTo.normalize();
         cam.Vup = vUp.normalize();
         cam.Vright = cam.Vto.crossProduct(cam.Vup).normalize();
         return this;
     }

        public Builder setDirection(Point target, Vector vUp) {
            Vector vTo = target.subtract(cam.position).normalize();
            if (vTo.equals(Vector.ZERO)) {
                throw new IllegalArgumentException("Target point cannot be the same as the camera position");
            }
            cam.Vto = vTo;
            cam.Vright = vTo.crossProduct(vUp).normalize();
            cam.Vup = cam.Vright.crossProduct(cam.Vto).normalize();
            return this;
        }

        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0));
        }
        public Builder setVpSize(double width, double height)
        {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
            cam.width = width;
            cam.height = height;
            return this;
        }
        public Builder setVpDistance(double distance)
        {
            if (distance <= 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }
            cam.distance = distance;
            return this;
        }
        public Builder setResolution(int nX, int nY)
        {
            return null;
        }
        public Camera build()
        {
            if (cam.position == null) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "position");
            }
            if (cam.Vto == null) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "Vto");
            }
            if (cam.Vup == null) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "Vup");
            }
            if (cam.Vright == null) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "Vright");
            }
            if (cam.width <= 0) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "width");
            }
            if (cam.height <= 0) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "height");
            }
            if (cam.distance <= 0) {
                throw new MissingResourceException("A Renderer field is missing", "Camera", "distance");
            }

            return cam;
        }
    }


    private Camera() {}
    public static Builder getBuilder
    {

    }
    public Ray constructRay(int nX, int nY, int j, int i)
    {
       return null;
    }
}
