package com.vaadin.testbench.screenshot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageUtil {

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
     * @param image1
     *            an image.
     * @param image2
     *            an image.
     * @return a list containing two images with the same dimensions
     */
    public static List<BufferedImage> cropToBeSameSize(BufferedImage image1,
            BufferedImage image2) {

        if (imagesSameSize(image1, image2)) {
            return Arrays.asList(image1, image2);
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
    public static BufferedImage cloneImage(BufferedImage sourceImage) {
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
