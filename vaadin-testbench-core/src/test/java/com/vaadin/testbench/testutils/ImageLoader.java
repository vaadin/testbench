/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.testutils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

public class ImageLoader {

    public static String loadImageToString(String folder, String filename)
            throws IOException {
        BufferedImage img = loadImage(folder, filename);
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        ImageIO.write(img, "png", outStream);
        return new String(Base64.encodeBase64(outStream.toByteArray()));
    }

    public static BufferedImage loadImage(String folder, String filename)
            throws IOException {
        File imgFile = getImageFile(folder, filename);
        assertTrue(imgFile.exists());

        return ImageIO.read(imgFile);
    }

    public static File getImageFile(String folder, String filename) {
        URL imgUrl = ImageLoader.class.getClassLoader().getResource(
                folder + "/" + filename);
        assertNotNull("Missing reference " + filename, imgUrl);
        File imgFile = new File(imgUrl.getPath());
        return imgFile;
    }

    public static byte[] loadImageBytes(String folder, String filename)
            throws IOException {
        File imgFile = getImageFile(folder, filename);
        assertTrue(imgFile.exists());

        byte[] bytes = new byte[(int) imgFile.length()];
        new DataInputStream(new FileInputStream(imgFile)).readFully(bytes);
        return bytes;
    }
}
