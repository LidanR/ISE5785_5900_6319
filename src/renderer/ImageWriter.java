package renderer;

import primitives.Color;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

/**
 * Image writer class combines accumulation of pixel color matrix and finally
 * producing a non-optimized jpeg image from this matrix. The class although is
 * responsible of holding image related parameters of View Plane - pixel matrix
 * size and resolution
 * @author Dan
 */
final class ImageWriter {
   /** Horizontal resolution of the image - number of pixels in row */
   private final int nX;
   /** Vertical resolution of the image - number of pixels in column */
   private final int nY;

   /**
    * Directory path for the image file generation - relative to the user
    * directory
    */
   private static final String FOLDER_PATH = System.getProperty("user.dir") + "/images";

   /** Image generation buffer (the matrix of the pixels) */
   private final BufferedImage image;

   // ***************** Constructors ********************** //
   /**
    * Image Writer constructor accepting image name and View Plane parameters,
    * @param nX amount of pixels by Width
    * @param nY amount of pixels by height
    */
   ImageWriter(int nX, int nY) {
      this.nX = nX;
      this.nY = nY;

      image   = new BufferedImage(nX, nY, BufferedImage.TYPE_INT_RGB);
   }

   // ***************** Getters ********************** //
   /**
    * View Plane Y axis resolution
    * @return the amount of vertical pixels
    */
   int nY() { return nY; }

   /**
    * View Plane X axis resolution
    * @return the amount of horizontal pixels
    */
   int nX() { return nX; }

   // ***************** Operations ******************** //

   /**
    * Function writeToImage produces unoptimized png file of the image according
    * to pixel color matrix in the directory of the project
    * @param imageName the name of png file
    */
   void writeToImage(String imageName) {
      try {
         File file = new File(FOLDER_PATH + '/' + imageName + ".png");
         ImageIO.write(image, "png", file);
      } catch (IOException e) {
         throw new IllegalStateException("I/O error - may be missing directory " + FOLDER_PATH, e);
      }
   }

   /**
    * The function writePixel writes a color of a specific pixel into pixel color
    * matrix
    * @param xIndex X axis index of the pixel
    * @param yIndex Y axis index of the pixel
    * @param color  final color of the pixel
    */
   void writePixel(int xIndex, int yIndex, Color color) { image.setRGB(xIndex, yIndex, color.getColor().getRGB()); }

}
