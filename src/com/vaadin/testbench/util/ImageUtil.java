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
     * Generates a grayscale image of give image.
     * 
     * @param image
     *            Image to turn to grayscale
     * @return BufferedImage [TYPE_BYTE_GRAY]
     */
    public static BufferedImage grayscaleImage(BufferedImage image) {
        BufferedImage gray = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = (Graphics2D) gray.getGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return gray;
    }

    /**
     * Creates a black and white version of the given image.
     * 
     * @param image
     *            The image to convert into a black and white image
     * @return A black and white version of the given image
     */
    public static BufferedImage createBlackAndWhiteImage(BufferedImage image) {
        BufferedImage bw = grayscaleImage(image);
        convertGrayscaleToBlackAndWhite(bw);
        return bw;
    }

    /**
     * Generates an image that is white where there are no differences and black
     * where the images differ.
     * 
     * @param image1
     *            First image
     * @param image2
     *            Second image
     * @return B&W image
     */
    public static BufferedImage createBlackAndWhiteDifferenceImage(
            BufferedImage image1, BufferedImage image2) {
        // Get black and white images for both images
        BufferedImage bw1 = createBlackAndWhiteImage(image1);
        BufferedImage bw2 = createBlackAndWhiteImage(image2);

        // Create empty image
        BufferedImage diff = new BufferedImage(image1.getWidth(),
                image1.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        // For each pixel, if pixel in both images equal "draw" white pixel.
        for (int x = 0; x < bw1.getWidth(); x++) {
            for (int y = 0; y < bw1.getHeight(); y++) {
                int color1 = bw1.getRGB(x, y);
                int color2 = bw2.getRGB(x, y);
                if (color1 == color2) {
                    diff.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    diff.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return diff;
    }

    /**
     * Makes a matrix convolution on pixel. Used to find edges.
     * 
     * @param kernel
     *            convolution kernel
     * @param kernWidth
     * @param kernHeight
     * @param src
     *            Source image
     * @param x
     *            X position of pixel
     * @param y
     *            Y position of pixel
     * @param rgb
     *            int[] to save new r, g and b values
     * @return new rgb values
     */
    private static int[] convolvePixel(float[] kernel, int kernWidth,
            int kernHeight, BufferedImage src, int x, int y, int[] rgb) {
        if (rgb == null) {
            rgb = new int[3];
        }

        int halfWidth = kernWidth / 2;
        int halfHeight = kernHeight / 2;

        /*
         * This algorithm pretends as though the kernel is indexed from
         * -halfWidth to halfWidth horizontally and -halfHeight to halfHeight
         * vertically. This makes the center pixel indexed at row 0, column 0.
         */

        for (int component = 0; component < 3; component++) {
            float sum = 0;
            for (int i = 0; i < kernel.length; i++) {
                int row = (i / kernWidth) - halfWidth;
                int column = (i - (kernWidth * row)) - halfHeight;

                // Check range
                if (x - row < 0 || x - row > src.getWidth()) {
                    continue;
                }
                if (y - column < 0 || y - column > src.getHeight()) {
                    continue;
                }
                int srcRGB = src.getRGB(x - row, y - column);
                sum = sum + kernel[i]
                        * ((srcRGB >> (16 - 8 * component)) & 0xff);
            }
            rgb[component] = (int) sum;
        }

        return rgb;
    }

    /**
     * Creates a b&w image of grayscale image.
     * 
     * @param image
     */
    private static void convertGrayscaleToBlackAndWhite(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                double lum = getLuminance(color);
                if (lum >= 150) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    /**
     * Gets the luminance value for given color
     * 
     * @param color
     * @return Luminance value
     */
    private static double getLuminance(Color color) {
        // return the monochrome luminance of given color
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
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

    public static void cropImage(BufferedImage rawImage,
            BrowserDimensions browserAndCanvasDimensions) {
        cropImage(rawImage, browserAndCanvasDimensions.getCanvasWidth(),
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
    private static void cropImage(BufferedImage image, int width, int height,
            int x, int y) {
        if (image.getWidth() == width && image.getHeight() == height) {
            return;
        }
        image = image.getSubimage(x, y, width, height);
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
