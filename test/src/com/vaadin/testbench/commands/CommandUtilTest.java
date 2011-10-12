/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.commands.CommandUtil.CanvasPositionFinder;
import com.vaadin.testbench.testutils.ImageLoader;

public class CommandUtilTest {
    private static final String FOLDER = CommandUtilTest.class.getPackage()
            .getName().replace('.', '/');

    private CanvasPositionFinder canvasFinder;

    @Before
    public void setUp() {
        canvasFinder = new CanvasPositionFinder(null, 1024, 768);
    }

    @Test
    public void isBrowserWindowPresent_shotRedRefWhite_returnsTrue()
            throws IOException {
        assertTrue(CommandUtil.isBrowserWindowPresent(
                ImageLoader.loadImage(FOLDER, "whitebg.png"),
                ImageLoader.loadImage(FOLDER, "redbg.png")));
    }

    @Test
    public void isBrowserWindowPresent_shotWhiteRefWhite_returnsFalse()
            throws IOException {
        assertFalse(CommandUtil.isBrowserWindowPresent(
                ImageLoader.loadImage(FOLDER, "whitebg.png"),
                ImageLoader.loadImage(FOLDER, "whitebg.png")));
    }

    @Test
    public void whiteLineStart_atFive() {
        int[] pixels = new int[100];
        for (int i = 5; i < 15; i++) {
            pixels[i] = 0xFFFFFF;
        }
        assertEquals(5, canvasFinder.findStartOfWhiteLine(pixels, 10));
    }

    @Test
    public void whiteLineStart_atTen() {
        int[] pixels = new int[100];
        for (int i = 10; i < 15; i++) {
            pixels[i] = 0xFFFFFF;
        }
        assertEquals(10, canvasFinder.findStartOfWhiteLine(pixels, 5));
    }

    @Test
    public void whiteLineStart_notLongEnough() {
        int[] pixels = new int[100];
        for (int i = 5; i < 10; i++) {
            pixels[i] = 0xFFFFFF;
        }
        assertEquals(-1, canvasFinder.findStartOfWhiteLine(pixels, 10));
    }

    @Test
    public void whiteLineStart_tooLong() {
        int[] pixels = new int[100];
        for (int i = 5; i < 30; i++) {
            pixels[i] = 0xFFFFFF;
        }
        assertEquals(-1, canvasFinder.findStartOfWhiteLine(pixels, 10));
    }

    @Test
    public void whiteLineStart_atTenWithAlpha() {
        int[] pixels = new int[100];
        for (int i = 10; i < 15; i++) {
            pixels[i] = 0xFFFFFFFF;
        }
        assertEquals(10, canvasFinder.findStartOfWhiteLine(pixels, 5));
    }

    @Test
    public void whiteLineStart_twoLinesOfDifferentLength() {
        int[] pixels = new int[100];
        for (int i = 5; i < 30; i++) {
            pixels[i] = 0xFFFFFF;
        }
        for (int i = 50; i < 60; i++) {
            pixels[i] = 0xFFFFFF;
        }
        assertEquals(50, canvasFinder.findStartOfWhiteLine(pixels, 10));
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowDarkDesktop()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest.png"), 1090, 753);
        finder.find();

        assertEquals(126, finder.getX());
        assertEquals(94, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowDarkDesktopFlashPopup()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest_flash.png"),
                1090, 753);
        finder.find();

        assertEquals(126, finder.getX());
        assertEquals(94, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktop()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest2.png"), 1090,
                753);
        finder.find();

        assertEquals(133, finder.getX());
        assertEquals(174, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktopFlashPopup()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest2_flash.png"),
                1090, 753);
        finder.find();

        assertEquals(133, finder.getX());
        assertEquals(174, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktopFFAt0x0()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest3.png"), 1024,
                768);
        finder.find();

        assertEquals(0, finder.getX());
        assertEquals(109, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktopFFAt0x0FlasPopup()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest3_flash.png"),
                1024, 768);
        finder.find();

        assertEquals(0, finder.getX());
        assertEquals(109, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowOnSolidWhiteDesktop()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest4.png"), 1090,
                755);
        finder.find();

        assertEquals(103, finder.getX());
        assertEquals(193, finder.getY());
    }

    @Test(expected = CanvasNotFoundException.class)
    public void findCanvasPositionByScreenshot_windowOverlappingTopLeftCorner()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowserTest4.png"), 1091,
                755);
        finder.find();
    }

    @Test
    public void findCanvasPositionByScreenshot_opera11OnXP() throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, "findBrowser_operaXP.png"), 1546,
                796);
        finder.find();

        assertEquals(20, finder.getX());
        assertEquals(173, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_maximizedOpera11OnXP()
            throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER,
                        "findBrowser_opera11_maximized.png"), 1920, 1084);
        finder.find();

        assertEquals(0, finder.getX());
        assertEquals(61, finder.getY());
    }

}
