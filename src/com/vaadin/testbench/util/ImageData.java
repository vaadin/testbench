package com.vaadin.testbench.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.vaadin.testbench.Parameters;

public class ImageData {

    // referenceDirectory is the name of the directory with the reference
    // pictures of the same name as the one to be compared
    public static final String REFERENCE_DIRECTORY = "reference";
    public static final String ERROR_DIRECTORY = "errors";

    private String referenceImageFileName = null;
    private String screenshotAsBase64String = null;

    /**
     * Screenshot base directory. Ends with a slash.
     */
    private String baseDirectory = null;

    /**
     * Screenshot reference directory. Ends with a slash.
     */
    private String referenceDirectory = null;

    /**
     * Screenshot error directory. Ends with a slash.
     */
    private String errorDirectory = null;

    private double difference;

    private BrowserDimensions dimensions = null;

    // collect errors that are then written to a .log file
    private StringBuilder imageErrors = new StringBuilder();

    private BufferedImage referenceImage = null;
    private BufferedImage comparisonImage = null;
    private ArrayList<BufferedImage> referenceImages;

    // Constructors
    public ImageData(String referenceImageFileName,
            BrowserDimensions dimensions, double difference) {
        this.referenceImageFileName = referenceImageFileName;
        this.dimensions = dimensions;
        this.difference = difference;
    }

    public ImageData(String screenshotAsBase64String,
            String referenceImageFileName, BrowserDimensions dimensions,
            double difference) {
        this(screenshotAsBase64String, dimensions, difference);
        this.referenceImageFileName = referenceImageFileName;
    }

    /**
     * Get base directory
     */
    public void generateBaseDirectory() {
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
    }

    /**
     * Convert base64 image to buffered image and crop out canvas
     */
    public void generateComparisonImage() {
        comparisonImage = ImageUtil.stringToImage(screenshotAsBase64String);
        // Crop the image if not already cropped client-side. This is true for
        // when there is no reference image.
        if (comparisonImage.getWidth() > dimensions.getCanvasWidth()
                || comparisonImage.getHeight() > dimensions.getCanvasHeight()) {
            comparisonImage = comparisonImage.getSubimage(
                    dimensions.getCanvasXPosition(),
                    dimensions.getCanvasYPosition(),
                    dimensions.getCanvasWidth(), dimensions.getCanvasHeight());
        }
    }

    /**
     * Get referenceImage from reference directory
     * 
     * @throws IOException
     */
    public void generateReferenceImage() throws IOException {
        referenceImage = ImageIO.read(new File(getReferenceDirectory()
                + getReferenceImageFileName()));
    }

    /**
     * Generates reference images from the reference directory.
     * 
     * @throws IOException
     */
    public void generateReferenceImages() throws IOException {
        referenceImages = new ArrayList<BufferedImage>();
        String nextName = getReferenceImageFileName();
        File file = new File(getReferenceDirectory() + nextName);
        int i = 1;
        while (file.exists()) {
            referenceImages.add(ImageIO.read(file));
            nextName = getReferenceImageFileName().replace(".png",
                    String.format("_%d.png", i++));
            file = new File(getReferenceDirectory() + nextName);
        }
    }

    /**
     * Check canvas sizes and resize images to same size
     * 
     * @return true/false
     */
    public boolean checkIfCanvasSizesDiffer() {
        if (referenceImage.getHeight() != comparisonImage.getHeight()
                || referenceImage.getWidth() != comparisonImage.getWidth()) {
            // smallest height and width of images
            int minHeight, minWidth;
            if (referenceImage.getHeight() > comparisonImage.getHeight()) {
                minHeight = comparisonImage.getHeight();

                debug("Screenshot height less than reference image.");
            } else {
                minHeight = referenceImage.getHeight();

                if (referenceImage.getHeight() != comparisonImage.getHeight()) {
                    debug("Reference image height less than screenshot.");
                }
            }
            if (referenceImage.getWidth() > comparisonImage.getWidth()) {
                minWidth = comparisonImage.getWidth();

                debug("Screenshot width less than reference image.");
            } else {
                minWidth = referenceImage.getWidth();

                if (referenceImage.getWidth() != comparisonImage.getWidth()) {
                    debug("Reference image width less than screenshot.");
                }
            }
            cropImages(minWidth, minHeight);

            return true;
        }
        return false;
    }

    /**
     * Crop reference and comparison images to same size
     * 
     * @param width
     *            width in pixels
     * @param height
     *            height in pixels
     */
    private void cropImages(int width, int height) {
        referenceImage = referenceImage.getSubimage(0, 0, width, height);
        comparisonImage = comparisonImage.getSubimage(0, 0, width, height);
    }

    public void debug(String text) {
        imageErrors.append(text + "\n");
    }

    /**
     * Get 16x16 rgb block from referenceImage
     * 
     * @param x
     *            x position
     * @param y
     *            y position
     * @return RGB values for macroblock
     */
    public int[] getReferenceBlock(int x, int y) {
        return referenceImage.getRGB(x, y, 16, 16, null, 0, 16);
    }

    /**
     * Get 16x16 rgb block from comparisonImage
     * 
     * @param x
     *            x position
     * @param y
     *            y position
     * @return RGB values for macroblock
     */
    public int[] getComparisonBlock(int x, int y) {
        return comparisonImage.getRGB(x, y, 16, 16, null, 0, 16);
    }

    /**
     * Copy comparisonImage as referenceImage
     */
    public void copyComparison() {
        BufferedImage newImage = new BufferedImage(dimensions.getCanvasWidth(),
                dimensions.getCanvasHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(comparisonImage, 0, 0, dimensions.getCanvasWidth(),
                dimensions.getCanvasHeight(), null);
        g.dispose();

        referenceImage = newImage;
    }

    // Getters and setters
    public String getReferenceImageFileName() {
        if (referenceImageFileName.endsWith(".png")) {
            return referenceImageFileName;
        }
        return referenceImageFileName + ".png";
    }

    public void setReferenceImageFileName(String fileName) {
        referenceImageFileName = fileName;
    }

    public String getScreenshotAsBase64String() {
        return screenshotAsBase64String;
    }

    public void setScreenshotAsBase64String(String image) {
        screenshotAsBase64String = image;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getReferenceDirectory() {
        if (referenceDirectory == null) {
            return baseDirectory + REFERENCE_DIRECTORY + File.separator;
        }
        return referenceDirectory;
    }

    public void setReferenceDirectory(String referenceDirectory) {
        this.referenceDirectory = referenceDirectory;
    }

    public String getErrorDirectory() {
        if (errorDirectory == null) {
            return baseDirectory + ERROR_DIRECTORY + File.separator;
        }
        return errorDirectory;
    }

    public void setErrorDirectory(String errorDirectory) {
        this.errorDirectory = errorDirectory;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public BrowserDimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(BrowserDimensions dimensions) {
        this.dimensions = dimensions;
    }

    public StringBuilder getImageErrors() {
        return imageErrors;
    }

    public void setImageErrors(StringBuilder imageErrors) {
        this.imageErrors = imageErrors;
    }

    public BufferedImage getReferenceImage() {
        return referenceImage;
    }

    public Iterable<BufferedImage> getReferenceImages() {
        return referenceImages;
    }

    public void setReferenceImage(BufferedImage referenceImage) {
        this.referenceImage = referenceImage;
    }

    public BufferedImage getComparisonImage() {
        return comparisonImage;
    }

    public void setComparisonImage(BufferedImage comparisonImage) {
        this.comparisonImage = comparisonImage;
    }
}
