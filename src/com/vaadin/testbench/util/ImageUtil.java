package com.vaadin.testbench.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageUtil {

    /**
     * Decodes target string from base64 to byteArray that is converted to an
     * image
     * 
     * @param imageString
     *            Base64 encoded image
     * @return BufferedImage
     */
    public static BufferedImage stringToImage(String imageString) {
        // string to ByteArrayInputStream
        BufferedImage bImage = null;
        Base64 b64dec = new Base64();
        try {
            byte[] output = b64dec.decode(imageString.getBytes());
            ByteArrayInputStream bais = new ByteArrayInputStream(output);
            bImage = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bImage;
    }

    /**
     * Encodes target image to a Base64 string
     * 
     * @param image
     *            BufferedImage to encode to String
     * @return Base64 encoded String of image
     */
    public static String encodeImageToBase64(BufferedImage image) {
        String encodedImage = "";
        Base64 encoder = new Base64();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] encodedBytes = encoder.encode(baos.toByteArray());
            encodedImage = new String(encodedBytes);
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    /**
     * Get luminance value for the given rgb value.
     * 
     * @param rgb
     * @return
     */
    public static double getLuminance(int rgb) {
        int r = ((rgb >> 16) & 0xFF);
        int g = ((rgb >> 8) & 0xFF);
        int b = (rgb & 0xFF);

        return getLuminance(r, g, b);
    }

    private static double getLuminance(int r, int g, int b) {
        return .299 * r + .587 * g + .114 * b;
    }

    /**
     * Gets the luminance value for given color
     * 
     * @param color
     * @return Luminance value
     */
    private static double getLuminance(Color color) {
        // return the monochrome luminance of given color
        return getLuminance(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Check canvas sizes and resize images to same size
     * 
     * @return true/false
     */
    public static boolean imagesSameSize(BufferedImage image1,
            BufferedImage image2) {
        return (image1.getWidth() == image2.getWidth() && image1.getHeight() == image2
                .getHeight());
    }

    /**
     * Resize images to be same size. The size is determined by the minimum
     * height and minimum width of the images.
     * 
     * @param images
     *            a list of two images.
     * @return a list containing two images with the same dimensions
     */
    public static List<BufferedImage> cropToBeSameSize(
            List<BufferedImage> images) {
        if (images.size() != 2) {
            throw new IllegalArgumentException(
                    "the images list must contain exactly two images");
        }
        BufferedImage image1 = images.get(0);
        BufferedImage image2 = images.get(1);
        if (imagesSameSize(image1, image2)) {
            return images;
        }

        int minHeight = Math.min(image1.getHeight(), image2.getHeight());
        int minWidth = Math.min(image1.getWidth(), image2.getWidth());

        BufferedImage cropped1 = cropImage(image1, minWidth, minHeight);
        BufferedImage cropped2 = cropImage(image2, minWidth, minHeight);
        return Arrays.asList(cropped1, cropped2);
    }

    /**
     * Crops the image to the given size starting at (0,0)
     * 
     * @param image
     *            The image to crop
     * @param width
     *            width in pixels
     * @param height
     *            height in pixels
     */
    private static BufferedImage cropImage(BufferedImage image, int width,
            int height) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return image;
        }
        return image.getSubimage(0, 0, width, height);
    }

    public static BufferedImage cropImage(BufferedImage rawImage,
            BrowserDimensions browserAndCanvasDimensions) {
        return cropImage(rawImage, browserAndCanvasDimensions.getCanvasWidth(),
                browserAndCanvasDimensions.getCanvasHeight(),
                browserAndCanvasDimensions.getCanvasXPosition(),
                browserAndCanvasDimensions.getCanvasYPosition());
    }

    /**
     * Crops the image to the given size starting at (x,y)
     * 
     * @param image
     *            The image to crop
     * @param width
     *            width in pixels
     * @param height
     *            height in pixels
     * @param x
     *            x-coordinate of top left corner
     * @param y
     *            y-coordinate of top left corner
     */
    private static BufferedImage cropImage(BufferedImage image, int width,
            int height, int x, int y) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return image;
        }
        return image.getSubimage(x, y, width, height);
    }

    /**
     * Returns the size of the image as a human readable string.
     * 
     * @param image
     * @return
     */
    public static String getSizeAsString(BufferedImage image) {
        return image.getWidth() + "x" + image.getHeight();
    }

    /**
     * Returns the 16x16 RGB block starting at (x,y) from the given image
     * 
     * @param image
     *            The image containing the block
     * @param x
     *            The x coordinate of the block (in pixels)
     * @param y
     *            The y coordinate of the block (in pixels)
     * @return An array of RGB values for the block
     */
    public static int[] getBlock(BufferedImage image, int x, int y) {
        return image.getRGB(x, y, 16, 16, null, 0, 16);
    }

    /**
     * Clones the given BufferedImage
     * 
     * @param sourceImage
     *            The image to copy
     * @return A copy of sourceImage
     */
    public static BufferedImage duplicateImage(BufferedImage sourceImage) {
        // This method could likely be optimized but the gain is probably small
        int w = sourceImage.getWidth();
        int h = sourceImage.getHeight();

        BufferedImage newImage = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(sourceImage, 0, 0, w, h, null);
        g.dispose();

        return newImage;
    }

}
