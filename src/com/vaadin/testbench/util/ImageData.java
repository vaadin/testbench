package com.vaadin.testbench.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageData {

    // referenceDirectory is the name of the directory with the reference
    // pictures of the same name as the one to be compared
    public static final String REFERENCE_DIRECTORY = ImageFileUtil.REFERENCE_DIRECTORY;
    public static final String ERROR_DIRECTORY = ImageFileUtil.ERROR_DIRECTORY;

    private String referenceImageFileName = null;
    private String screenshotAsBase64String = null;

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
        this(referenceImageFileName, dimensions, difference);
        this.screenshotAsBase64String = screenshotAsBase64String;
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
        referenceImage = ImageIO.read(new File(ImageFileUtil
                .getScreenshotReferenceDirectory()
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
        File file = new File(ImageFileUtil.getScreenshotReferenceDirectory()
                + nextName);
        int i = 1;
        while (file.exists()) {
            referenceImages.add(ImageIO.read(file));
            nextName = getReferenceImageFileName().replace(".png",
                    String.format("_%d.png", i++));
            file = new File(ImageFileUtil.getScreenshotReferenceDirectory()
                    + nextName);
        }
    }

    public void debug(String text) {
        imageErrors.append(text + "\n");
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
