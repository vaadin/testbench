/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.screenshot;

import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageComparisonUtil {

    /**
     * Generates blocks representing an image by dividing the image up in 16x16
     * pixel blocks and calculating a mean value of the color in each block.
     *
     * @param image
     *            the image
     * @return the block representation of the image
     */
    public static String generateImageHash(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        byte[] data = new byte[width * height * 3];

        int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                rgb &= 0x00FCFCFC;

                // Skip the two last bits for fuzzy comparison
                data[idx++] = (byte) ((rgb >> 16));
                data[idx++] = (byte) ((rgb >> 8));
                data[idx++] = (byte) (rgb);
            }
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            String hash = byteToHex(md5.digest());
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm provider not found", e);
        }
    }

    private static String byteToHex(byte[] bytes) {
        String hex = "";
        for (int i = 0; i < bytes.length; i++) {
            hex += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return hex;
    }

    /**
     * Returns the number of blocks used for the given number of pixels. All
     * blocks are full size with the (possible) exception of the bottom and
     * right edges.
     *
     * @param pixels
     *            The number of pixels for the dimension.
     * @return The number of blocks used for that dimension
     */
    public static int getNrBlocks(int pixels) {
        return (int) Math.floor(pixels + 15) / 16;
    }
}
