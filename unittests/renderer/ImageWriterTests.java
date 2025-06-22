package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ImageWriter} class.
 * This test creates an image containing an orange background with a black grid,
 * and verifies that the image generation process completes without throwing exceptions.
 */
class ImageWriterTests {

    /** Width of the image in pixels. */
    private static final int nX = 801;

    /** Height of the image in pixels. */
    private static final int nY = 501;

    /** Orange color used to fill the background (RGB: 255, 128, 0). */
    private final Color SoftBlueColor = new Color(72d, 135d, 183d);

    /** Black color used to draw the grid lines (RGB: 0, 0, 0). */
    private final Color blackColor = new Color(0d, 0d, 0d);

    /**
     * Test method for {@link ImageWriter#writeToImage(String)}.
     * This test generates an image with:
     * <ul>
     *     <li>An orange background filling the entire image.</li>
     *     <li>A black grid, where horizontal and vertical lines appear every 50 pixels.</li>
     * </ul>
     * The method asserts that no exceptions are thrown during the image creation and writing process.
     */
    @Test
    void testCreateImageWithGrid() {
        assertDoesNotThrow(() -> {
            ImageWriter imageWriter = new ImageWriter(nX, nY);

            // Loop through each pixel on the image
            for (int i = 0; i < nX; i++) {
                for (int j = 0; j < nY; j++) {
                    // Color the pixel black if it's on a grid line; otherwise, color it orange
                    imageWriter.writePixel(i, j, (i % 50 == 0 || j % 50 == 0) ? blackColor : SoftBlueColor);
                }
            }

            // Write the constructed image to a file named "FirstImage"
            imageWriter.writeToImage("imagwWriter/FirstImage");
        }, "Failed to create and write the image without exceptions.");
    }
}
