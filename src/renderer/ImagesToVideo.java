package renderer;

import org.jcodec.api.awt.AWTSequenceEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class to create a video from a sequence of images.
 * The images should be named with a numeric suffix after the last underscore,
 * e.g., "frame_1.jpg", "frame_2.png", etc.
 * The output video will be encoded in H.264/MP4 format at 25 frames per second.
 */
public class ImagesToVideo {
    // Base directory for both input images and output video
    private static final String BASE_DIR = System.getProperty("user.dir") + "/images";

    /**
     * Reads all .jpg/.png frames from BASE_DIR/imagesSubfolder,
     * encodes them at 25 fps into H.264/MP4, and writes to
     * BASE_DIR/outputRelPathNoExt + ".mp4".
     * Repeats the frame sequence `loops` times, and if `pingPong` is true,
     * alternates forward/reverse each pass.
     *
     * @param imagesSubfolder      subfolder under BASE_DIR containing frames
     * @param outputRelPathNoExt   desired path+filename (no extension) under BASE_DIR
     * @param loops                number of times to repeat the sequence (must be ≥ 1)
     * @param pingPong             if true, alternate direction each pass
     * @throws IOException         if no images found, loops < 1, or writing fails
     */
    public static void createVideoFromImages(String imagesSubfolder,
                                             String outputRelPathNoExt,
                                             int loops,
                                             boolean pingPong) throws IOException {
        if (loops < 1) {
            throw new IllegalArgumentException("loops must be ≥ 1 (got " + loops + ")");
        }

        // Collect and filter image files
        File inputDir = new File(BASE_DIR, imagesSubfolder);
        File[] frames = inputDir.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".png")
        );
        if (frames == null || frames.length == 0) {
            throw new IOException("No images found in: " + inputDir.getAbsolutePath());
        }

        // Sort by numeric suffix (after the last underscore, before extension)
        Arrays.sort(frames, (f1, f2) ->
                Integer.compare(extractNumber(f1.getName()), extractNumber(f2.getName()))
        );

        // Prepare output file (append .mp4) and ensure parent directories exist
        String filename = outputRelPathNoExt.endsWith(".mp4")
                ? outputRelPathNoExt
                : outputRelPathNoExt + ".mp4";
        File outputFile = new File(BASE_DIR, filename);
        Path parentDir = outputFile.toPath().getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // Create MP4 encoder at 25 fps
        AWTSequenceEncoder encoder = null;
        try {
            encoder = AWTSequenceEncoder.createSequenceEncoder(outputFile, 25);

            // Encode frames, looping and ping-ponging as requested
            for (int pass = 1; pass <= loops; pass++) {
                boolean forward = !pingPong || (pass % 2 == 1);
                if (forward) {
                    for (File frame : frames) {
                        encodeFrame(encoder, frame);
                    }
                } else {
                    for (int i = frames.length - 1; i >= 0; i--) {
                        encodeFrame(encoder, frames[i]);
                    }
                }
            }
        } finally {
            if (encoder != null) {
                // finish() writes the MP4 trailer and closes the file
                encoder.finish();
            }
        }

        System.out.println("Created MP4 at: " + outputFile.getAbsolutePath()
                + " (loops=" + loops
                + ", pingPong=" + pingPong + ")");
    }

    /**
     * Reads a BufferedImage from file and encodes it.
     *
     * @param encoder   the open AWTSequenceEncoder
     * @param imgFile   image file to read and encode
     * @throws IOException if reading fails
     */
    private static void encodeFrame(AWTSequenceEncoder encoder, File imgFile) throws IOException {
        BufferedImage img = ImageIO.read(imgFile);
        if (img == null) {
            System.err.println("Skipped unreadable image: " + imgFile.getName());
            return;
        }
        encoder.encodeImage(img);
    }

    /**
     * Extracts the integer suffix after the last underscore in a filename.
     * For example, "scene_12.png" → 12. If parsing fails, returns 0.
     *
     * @param filename  the name of the file
     * @return the parsed integer or 0 on failure
     */
    private static int extractNumber(String filename) {
        int dot = filename.lastIndexOf('.');
        String base = (dot >= 0) ? filename.substring(0, dot) : filename;
        int under = base.lastIndexOf('_');
        if (under >= 0 && under < base.length() - 1) {
            try {
                return Integer.parseInt(base.substring(under + 1));
            } catch (NumberFormatException e) {
                // fall through to return 0
            }
        }
        return 0;
    }
}
