package com.vaadin.testbench.screenshot;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.vaadin.testbench.qprofile.QProfile;

/**
 * These image utility functions are for internal use only.
 */
public class ImageUtil {

    /**
     * Contains ImageUtil-internal information about an image. Used by
     * getBlock() method.
     */
    public static class ImageProperties {
        private Raster raster = null;
        private int bands = 0;
        private boolean rgb = false;
        private boolean fallback = false;
        private int width = 0;
        private int height = 0;
    }

    /**
     * Encodes target image to a Base64 string
     * 
     * @param image
     *            BufferedImage to encode to String
     * @return Base64 encoded String of image
     */
    public static String encodeImageToBase64(BufferedImage image) {
        QProfile.begin();
        try {
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
        } finally {
            QProfile.end();
        }
    }

    /**
     * Get luminance value for the given rgb value.
     * 
     * @param rgb
     * @return
     */
    public static double getLuminance(int rgb) {
        QProfile.begin();
        try {
            int r = ((rgb >> 16) & 0xFF);
            int g = ((rgb >> 8) & 0xFF);
            int b = (rgb & 0xFF);

            return getLuminance(r, g, b);
        } finally {
            QProfile.end();
        }
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
        QProfile.begin();
        try {
            return (image1.getWidth() == image2.getWidth() && image1
                    .getHeight() == image2.getHeight());
        } finally {
            QProfile.end();
        }
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
        QProfile.begin();
        try {

            if (imagesSameSize(image1, image2)) {
                return Arrays.asList(image1, image2);
            }

            int minHeight = Math.min(image1.getHeight(), image2.getHeight());
            int minWidth = Math.min(image1.getWidth(), image2.getWidth());

            BufferedImage cropped1 = cropImage(image1, minWidth, minHeight);
            BufferedImage cropped2 = cropImage(image2, minWidth, minHeight);
            return Arrays.asList(cropped1, cropped2);
        } finally {
            QProfile.end();
        }
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
        QProfile.begin();
        try {

            if (image.getWidth() == width && image.getHeight() == height) {
                return image;
            }
            return image.getSubimage(0, 0, width, height);
        } finally {
            QProfile.end();
        }
    }

    /**
     * Create a 16x16 sample buffer with space for 4 color bands
     * 
     * @return
     */
    public static final int[] createSampleBuffer() {
        return new int[16 * 16 * 4];
    }

    /**
     * Extract magical image properties used by the getBlock function.
     * 
     * @param image
     *            a BufferedImage
     * @return magical properties of great power
     */
    public static final ImageProperties getImageProperties(BufferedImage image) {
        final int imageType = image.getType();
        ImageProperties p = new ImageProperties();
        p.raster = image.getRaster();
        p.bands = p.raster.getNumBands();
        p.rgb = imageType == TYPE_INT_ARGB || imageType == TYPE_INT_RGB;
        p.width = image.getWidth();
        p.height = image.getHeight();
        p.fallback = !p.rgb || p.bands < 3 || p.bands > 4;
        return p;
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
    public static final int[] getBlock(final BufferedImage image,
            final ImageProperties properties, int x, int y, int[] result,
            int[] sample) {

        QProfile.begin();
        try {

            final int width;
            final int height;

            if (result == null) {
                result = new int[16 * 16];
            }

            if (sample == null) {
                sample = new int[16 * 16 * 4];
            }

            if (x + 16 >= properties.width) {
                width = properties.width - x;
            } else {
                width = 16;
            }

            if (y + 16 >= properties.height) {
                height = properties.height - y;
            } else {
                height = 16;
            }

            if (properties.fallback) {
                image.getRGB(x, y, width, height, result, 0, width);
            } else {
                assert (properties.rgb);

                properties.raster.getPixels(x, y, width, height, sample);
                int xx, yy, i = 0;

                if (properties.bands == 4) {

                    // Image has an alpha channel
                    for (yy = 0; yy < height; ++yy) {

                        int p = (yy * height) << 2;

                        for (xx = 0; xx < width; ++xx) {
                            result[i++] = (sample[p + 3] << 24)
                                    | (sample[p] << 16) | (sample[p + 1] << 8)
                                    | sample[p + 2];
                            p += 4;
                        }

                        // Fill in possible overlap
                        for (xx = width; xx < 16; ++xx) {
                            result[i++] = 0;
                        }
                    }

                } else {

                    // Image does not have an alpha channel; assume alpha =
                    // 255
                    for (yy = 0; yy < height; ++yy) {

                        int p = (yy * height) * 3;

                        for (xx = 0; xx < width; ++xx) {
                            result[i++] = (255 << 24) | (sample[p] << 16)
                                    | (sample[p + 1] << 8) | sample[p + 2];
                            p += 3;
                        }

                        // Fill in possible overlap
                        for (xx = width; xx < 16; ++xx) {
                            result[i++] = 0;
                        }
                    }
                }

                // Fill in any possible overlap with transparent black
                for (yy = height; yy < 16; ++yy) {
                    for (xx = 0; xx < 16; ++xx) {
                        result[i++] = 0;
                    }
                }
            }

            return result;
        } finally {
            QProfile.end();
        }
    }

    /**
     * Clones the given BufferedImage
     * 
     * @param sourceImage
     *            The image to copy
     * @return A copy of sourceImage
     */
    public static BufferedImage cloneImage(BufferedImage sourceImage) {

        QProfile.begin();
        try {

            // This method could likely be optimized but the gain is probably
            // small
            int w = sourceImage.getWidth();
            int h = sourceImage.getHeight();

            BufferedImage newImage = new BufferedImage(w, h, TYPE_INT_RGB);

            Graphics2D g = (Graphics2D) newImage.getGraphics();
            g.drawImage(sourceImage, 0, 0, w, h, null);
            g.dispose();

            return newImage;
        } finally {
            QProfile.end();
        }
    }

}
