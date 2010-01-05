package com.vaadin.testbench.util;

import java.awt.Color;
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
     * Runs edge detection on image and returns a grayscale image with edges.
     * 
     * @param image
     *            Base64 encoded String of image.
     * @return Base64 encoded String of image. [TYPE_BYTE_GRAY]
     */
    public static String detectEdges(String image) {
        return encodeImageToBase64(robertsCrossEdges(stringToImage(image)));
    }

    /**
     * Runs edge detection on image and returns a grayscale image with edges.
     * 
     * @param image
     *            BufferedImage
     * @return BufferedImage [TYPE_BYTE_GRAY]
     */
    public static BufferedImage detectEdges(BufferedImage image) {
        return robertsCrossEdges(image);
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
     * Runns a Roberts Cross edge detection on image.
     * 
     * @param image
     *            BufferedImage
     * @return BufferedImage [TYPE_BYTE_GRAY] with found edges.
     */
    private static BufferedImage robertsCrossEdges(BufferedImage image) {
        BufferedImage edges = new BufferedImage(image.getWidth(), image
                .getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        float[] hx = new float[] { 1, 0, 0, -1 };
        float[] hy = new float[] { 0, 1, -1, 0 };

        int[] rgbX = new int[3];
        int[] rgbY = new int[3];

        for (int x = 1; x < image.getWidth() - 1; x++) {
            for (int y = 1; y < image.getHeight() - 1; y++) {
                convolvePixel(hx, 2, 2, image, x, y, rgbX);
                convolvePixel(hy, 2, 2, image, x, y, rgbY);

                int r = Math.abs(rgbX[0]) + Math.abs(rgbY[0]);
                int g = Math.abs(rgbX[1]) + Math.abs(rgbY[1]);
                int b = Math.abs(rgbX[2]) + Math.abs(rgbY[2]);

                if (r > 255) {
                    r = 255;
                }
                if (g > 255) {
                    g = 255;
                }
                if (b > 255) {
                    b = 255;
                }

                edges.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        threshold(edges);
        return edges;
    }

    /**
     * Creates a b&w image of grayscale image.
     * 
     * @param image
     */
    private static void threshold(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                double lum = lum(color);
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
    private static double lum(Color color) {
        // return the monochrome luminance of given color
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return .299 * r + .587 * g + .114 * b;
    }

    /**
     * Does a mild blur on the given image
     * 
     * @param image
     */
    @SuppressWarnings("unused")
    private static void blur(BufferedImage image) {

        float[] matrix = { 0.111f, 0.111f, 0.111f, 0.111f, 0.111f, 0.111f,
                0.111f, 0.111f, 0.111f, };

        int[] rgb = new int[3];

        for (int x = 1; x < image.getWidth() - 1; x++) {
            for (int y = 1; y < image.getHeight() - 1; y++) {
                convolvePixel(matrix, 3, 3, image, x, y, rgb);
                image.setRGB(x, y, getRGB(rgb));
            }
        }
    }

    /**
     * Creates a single int representation of r, g & b
     * 
     * @param rgb
     *            int[] rgb
     * @return int rgb
     */
    private static int getRGB(int[] rgb) {
        int r = Math.abs(rgb[0]);
        int g = Math.abs(rgb[1]);
        int b = Math.abs(rgb[2]);

        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }

        return ((r << 16) | (g << 8) | b);
    }
}
