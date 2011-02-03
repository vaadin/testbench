package com.vaadin.testbench.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.vaadin.testbench.Parameters;

public class ImageData {

    // referenceDirectory is the name of the directory with the reference
    // pictures of the same name as the one to be compared
    public static final String REFERENCE_DIRECTORY = "reference";
    public static final String ERROR_DIRECTORY = "errors";

    private String fileName = null;
    private String originalImage = null;
    private String baseDirectory = null;
    private String referenceDirectory = null;
    private String errorDirectory = null;

    private int cursorX, cursorY;
    private double difference = 0.025;

    private BrowserDimensions dimensions = null;

    // collect errors that are then written to a .log file
    private StringBuilder imageErrors = new StringBuilder();

    private BufferedImage referenceImage = null;
    private BufferedImage comparisonImage = null;

    // Constructors
    public ImageData(String fileName, BrowserDimensions dimensions) {
        this.fileName = fileName;
        this.dimensions = dimensions;

        setDifference();
    }

    public ImageData(String originalImage, String fileName,
            BrowserDimensions dimensions) {
        this.originalImage = originalImage;
        this.fileName = fileName;
        this.dimensions = dimensions;

        setDifference();
    }

    public ImageData(String originalImage, String fileName,
            BrowserDimensions dimensions, double difference) {
        this.originalImage = originalImage;
        this.fileName = fileName;
        this.dimensions = dimensions;
        this.difference = difference;

        setDifference();
    }

    // Functions
    private void setDifference() {
        if (Parameters.getScreenshotComparisonTolerance() != null) {
            difference = Parameters.getScreenshotComparisonTolerance();
        }

        // Check that [difference] value inside allowed range.
        // if false set [difference] to default value.
        if (difference < 0 || difference > 1) {
            difference = 0.025;
        }
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
        comparisonImage = ImageUtil.stringToImage(originalImage);
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
                + getFileName()));
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
    public String getFileName() {
        if (fileName.endsWith(".png")) {
            return fileName;
        }
        return fileName + ".png";
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String image) {
        originalImage = image;
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

    public void setReferenceImage(BufferedImage referenceImage) {
        this.referenceImage = referenceImage;
    }

    public BufferedImage getComparisonImage() {
        return comparisonImage;
    }

    public void setComparisonImage(BufferedImage comparisonImage) {
        this.comparisonImage = comparisonImage;
    }

    public void setCursorError(int cursorX, int cursorY) {
        this.cursorX = cursorX;
        this.cursorY = cursorY;
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }
}
