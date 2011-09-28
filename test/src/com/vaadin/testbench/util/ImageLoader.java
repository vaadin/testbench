/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.util;

import static junit.framework.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
        URL imgUrl = ImageLoader.class.getClassLoader().getResource(
                folder + "/" + filename);
        assertNotNull("Missing reference " + filename, imgUrl);
        File imgFile = new File(imgUrl.getPath());
        junit.framework.Assert.assertTrue(imgFile.exists());

        return ImageIO.read(imgFile);
    }

}
