/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Test;

public class CommandUtilTest {
    private static final String FOLDER = CommandUtilTest.class.getPackage()
            .getName().replace('.', '/');

    @Test
    public void isBrowserWindowPresent_shotRedRefWhite_returnsTrue()
            throws IOException {
        assertTrue(CommandUtil.isBrowserWindowPresent(loadImage("redbg.png"),
                loadImage("whitebg.png")));
    }

    @Test
    public void isBrowserWindowPresent_shotWhiteRefWhite_returnsFalse()
            throws IOException {
        assertFalse(CommandUtil.isBrowserWindowPresent(
                loadImage("whitebg.png"), loadImage("whitebg.png")));
    }

    private BufferedImage loadImage(String name) throws IOException {
        String fullpath = FOLDER + "/" + name;
        URL imgUrl = getClass().getClassLoader().getResource(fullpath);
        assertNotNull("Missing file " + fullpath, imgUrl);
        File imgFile = new File(imgUrl.getPath());
        assertTrue(imgFile.exists());

        return ImageIO.read(imgFile);
    }
}
