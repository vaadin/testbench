package com.vaadin.testbench.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
     * Generates an image that is white where there are no differences and black
     * where the images differ.
     * 
     * @param image1
     *            First image
     * @param image2
     *            Second image
     * @param height
     * @param width
     * @param y
     * @param x
     * @return B&W image
     */
    public static BufferedImage createBlackAndWhiteDifferenceImage(
            BufferedImage image1, BufferedImage image2, int startX, int startY,
            int width, int height) {
        // Create empty image
        BufferedImage diff = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_GRAY);

        // Set background to white
        Graphics2D g = diff.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);

        // Convert both images to black and white and mark differences in the
        // "difference" image as black
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double luminance1 = getLuminance(image1.getRGB(x, y));
                double luminance2 = getLuminance(image2.getRGB(x, y));

                boolean black1 = (luminance1 < 150);
                boolean black2 = (luminance2 < 150);

                if (black1 != black2) {
                    diff.setRGB(x - startX, y - startY, Color.BLACK.getRGB());
                }
            }
        }
        return diff;
    }

    /**
     * Get luminance value for the given rgb value.
     * 
     * @param rgb
     * @return
     */
    private static double getLuminance(int rgb) {
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
     * @param image1
     * @param image2
     * @return true if at least one of the images was cropped, false otherwise
     * @return
     */
    public static boolean cropToBeSameSize(BufferedImage image1,
            BufferedImage image2) {
        if (imagesSameSize(image1, image2)) {
            return false;
        }

        int minHeight = Math.min(image1.getHeight(), image2.getHeight());
        int minWidth = Math.min(image1.getWidth(), image2.getWidth());

        cropImage(image1, minWidth, minHeight);
        cropImage(image2, minWidth, minHeight);
        return true;
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
    private static void cropImage(BufferedImage image, int width, int height) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return;
        }
        image = image.getSubimage(0, 0, width, height);
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
