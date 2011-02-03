package com.vaadin.testbench.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageComparisonUtil {

    /**
     * Generates blocks representing an image by dividing the image up in 16x16
     * pixel blocks and calculating a mean value of the color in each block.
     * 
     * @param image
     *            the image
     * @return the block representation of the image
     */
    public static int[] generateImageBlocks(BufferedImage image) {
        int xBlocks = (int) Math.floor(image.getWidth() / 16) + 1;
        int yBlocks = (int) Math.floor(image.getHeight() / 16) + 1;

        BufferedImage scaledImage = new BufferedImage(xBlocks, yBlocks,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        float scale = 0.0625f; // 1/16
        AffineTransform transform = AffineTransform.getScaleInstance(scale,
                scale);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(image, transform, null);
        graphics2D.dispose();

        return scaledImage.getRGB(0, 0, scaledImage.getWidth(),
                scaledImage.getHeight(), null, 0, scaledImage.getWidth());
    }

    /**
     * Checks whether the screen shot blocks are equal to the reference blocks
     * within a certain error tolerance.
     * 
     * @param referenceBlocks
     *            the reference blocks
     * @param shotBlocks
     *            the screen shot blocks
     * @param tolerance
     *            the tolerance
     * @return true if the blocks are equal
     */
    public static boolean blocksEqual(int[] referenceBlocks, int[] shotBlocks,
            float tolerance) {
        if (shotBlocks.length != referenceBlocks.length) {
            return false;
        }
        for (int i = 0; i < referenceBlocks.length; i++) {
            float diff = Math.abs(referenceBlocks[i] - shotBlocks[i]);
            if (diff > tolerance) {
                return false;
            }
        }
        return true;
    }

}
