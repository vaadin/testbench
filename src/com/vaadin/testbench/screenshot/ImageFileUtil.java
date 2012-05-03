package com.vaadin.testbench.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.vaadin.testbench.Parameters;

public class ImageFileUtil {

    private static ImageFileUtilImpl impl = new ImageFileUtilImpl();

    /**
     * Returns the directory used for screenshot references.
     * 
     * @return The screenshot reference directory, ending in a slash.
     * @return
     */
    public static String getScreenshotReferenceDirectory() {
        return impl.getScreenshotReferenceDirectory();
    }

    /**
     * Returns the directory used for screenshot error images.
     * 
     * @return The screenshot error directory, ending in a slash.
     */
    public static String getScreenshotErrorDirectory() {
        return impl.getScreenshotErrorDirectory();

    }

    /**
     * Creates all directories used to store screenshots unless they already
     * exist.
     */
    public static void createScreenshotDirectoriesIfNeeded() {
        impl.createScreenshotDirectoriesIfNeeded();
    }

    /**
     * Reads the given reference image into a BufferedImage
     * 
     * @param referenceImageFileName
     * @return
     * @throws IOException
     */
    public static BufferedImage readReferenceImage(String referenceImageFileName)
            throws IOException {
        return impl.readReferenceImage(referenceImageFileName);
    }

    public static File getErrorScreenshotFile(String errorImageFileName) {
        return impl.getErrorScreenshotFile(errorImageFileName);
    }

    public static File getReferenceScreenshotFile(String referenceImageFileName) {
        return impl.getReferenceScreenshotFile(referenceImageFileName);
    }

    /**
     * Returns the relative file names of reference images. The actual image
     * file for a relative file name can be retrieved with
     * {@link #getReferenceScreenshotFile(String)}.
     * 
     * @param referenceImageFileName
     * @return file names of reference images
     */
    public static List<String> getReferenceImageFileNames(
            String referenceImageFileName) {
        return impl.getReferenceImageFileNames(referenceImageFileName);
    }

    public static class ImageFileUtilImpl {
        /**
         * Returns the directory used for screenshot references.
         * 
         * @return The screenshot reference directory, ending in a slash.
         * @return
         */
        public String getScreenshotReferenceDirectory() {
            return Parameters.getScreenshotReferenceDirectory();
        }

        /**
         * Returns the directory used for screenshot error images.
         * 
         * @return The screenshot error directory, ending in a slash.
         */
        public String getScreenshotErrorDirectory() {
            return Parameters.getScreenshotErrorDirectory();

        }

        /**
         * Creates all directories used to store screenshots unless they already
         * exist.
         */
        public void createScreenshotDirectoriesIfNeeded() {
            if (getScreenshotReferenceDirectory() != null) {
                // Check directories and create if needed
                File dir = new File(getScreenshotReferenceDirectory());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            if (getScreenshotErrorDirectory() != null) {
                File dir = new File(getScreenshotErrorDirectory());
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (Parameters.isDebug()) {
                    dir = new File(getScreenshotErrorDirectory() + "diff");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    dir = new File(getScreenshotErrorDirectory() + "logs");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                }
            }
        }

        /**
         * Reads the given reference image into a BufferedImage
         * 
         * @param referenceImageFileName
         * @return
         * @throws IOException
         */
        public BufferedImage readReferenceImage(String referenceImageFileName)
                throws IOException {
            return readImage(ImageFileUtil.getScreenshotReferenceDirectory()
                    + referenceImageFileName);
        }

        /**
         * Reads the given file into a BufferedImage.
         * 
         * @param fullyQualifiedFileName
         * @return
         * @throws IOException
         */
        private BufferedImage readImage(String fullyQualifiedFileName)
                throws IOException {
            File file = new File(fullyQualifiedFileName);
            return ImageIO.read(file);
        }

        public File getErrorScreenshotFile(String errorImageFileName) {
            return new File(getScreenshotErrorDirectory() + errorImageFileName);
        }

        public File getReferenceScreenshotFile(String referenceImageFileName) {
            return new File(getScreenshotReferenceDirectory()
                    + referenceImageFileName);
        }

        /**
         * Returns the relative file names of reference images. The actual image
         * file for a relative file name can be retrieved with
         * {@link #getReferenceScreenshotFile(String)}.
         * 
         * @param referenceImageFileName
         * @return file names of reference images
         */
        public List<String> getReferenceImageFileNames(
                String referenceImageFileName) {
            ArrayList<String> referenceImages = new ArrayList<String>();
            String nextName = referenceImageFileName;
            File file = getReferenceScreenshotFile(nextName);
            int i = 1;
            while (file.exists()) {
                referenceImages.add(nextName);
                nextName = referenceImageFileName.replace(".png",
                        String.format("_%d.png", i++));
                file = getReferenceScreenshotFile(nextName);
            }

            return referenceImages;
        }
    }
}
