package com.vaadin.testbench.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.vaadin.testbench.Parameters;

public class ImageFileUtil {

    public static final String REFERENCE_DIRECTORY = "reference";
    public static final String ERROR_DIRECTORY = "errors";

    private static String baseDirectory = null;

    /**
     * Returns the base directory used for screenshots. This directory contains
     * the "error" and "reference" folders.
     * 
     * @return The base directory used for screenshots, ending in a slash.
     */
    public static String getScreenshotBaseDirectory() {
        if (baseDirectory != null) {
            return baseDirectory;
        }

        baseDirectory = Parameters.getScreenshotDirectory();

        if (baseDirectory == null || baseDirectory.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + Parameters.SCREENSHOT_DIRECTORY
                            + "=c:\\screenshot\\. ");
        }

        if (!File.separator
                .equals(baseDirectory.charAt(baseDirectory.length() - 1))) {
            baseDirectory = baseDirectory + File.separator;
        }

        return baseDirectory;
    }

    /**
     * Returns the directory used for screenshot references.
     * 
     * @return The screenshot reference directory, ending in a slash.
     * @return
     */
    public static String getScreenshotReferenceDirectory() {
        return getScreenshotBaseDirectory() + REFERENCE_DIRECTORY
                + File.separator;
    }

    /**
     * Returns the directory used for screenshot error images.
     * 
     * @return The screenshot error directory, ending in a slash.
     */
    public static String getScreenshotErrorDirectory() {
        return getScreenshotBaseDirectory() + ERROR_DIRECTORY + File.separator;

    }

    /**
     * Creates all directories used to store screenshots unless they already
     * exist.
     */
    public static void createScreenshotDirectoriesIfNeeded() {
        // Check directories and create if needed
        File dir = new File(getScreenshotBaseDirectory());
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(getScreenshotReferenceDirectory());
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(getScreenshotErrorDirectory());
        if (!dir.exists()) {
            dir.mkdir();
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

    /**
     * Reads the given reference image into a BufferedImage
     * 
     * @param referenceImageFileName
     * @return
     * @throws IOException
     */
    public static BufferedImage readReferenceImage(String referenceImageFileName)
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
    private static BufferedImage readImage(String fullyQualifiedFileName)
            throws IOException {
        File file = new File(fullyQualifiedFileName);
        return ImageIO.read(file);
    }

    public static File getErrorScreenshotFile(String errorImageFileName) {
        return new File(getScreenshotErrorDirectory() + errorImageFileName);
    }

    public static File getReferenceScreenshotFile(String referenceImageFileName) {
        return new File(getScreenshotReferenceDirectory()
                + referenceImageFileName);
    }

    public static Iterable<BufferedImage> getReferenceImages(
            String referenceImageFileName) throws IOException {
        ArrayList<BufferedImage> referenceImages = new ArrayList<BufferedImage>();
        String nextName = referenceImageFileName;
        File file = ImageFileUtil.getReferenceScreenshotFile(nextName);
        int i = 1;
        while (file.exists()) {
            referenceImages.add(ImageIO.read(file));
            nextName = referenceImageFileName.replace(".png",
                    String.format("_%d.png", i++));
            file = ImageFileUtil.getReferenceScreenshotFile(nextName);
        }

        return referenceImages;
    }
}
