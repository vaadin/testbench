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
    public void whiteLineStart_atTenWithAlpha() {
        int[] pixels = new int[100];
        for (int i = 10; i < 15; i++) {
            pixels[i] = 0xFFFFFFFF;
        }
        assertEquals(10, canvasFinder.findStartOfWhiteLine(pixels, 5));
    }

    private void assertCanvasPosition(String filename, int x, int y, int width,
            int height) throws IOException {
        CanvasPositionFinder finder = new CanvasPositionFinder(
                ImageLoader.loadImage(FOLDER, filename), width, height);
        finder.find();

        assertEquals(x, finder.getX());
        assertEquals(y, finder.getY());
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowDarkDesktop()
            throws IOException {
        assertCanvasPosition("findBrowserTest.png", 126, 94, 1090, 753);
    }

    @Test(expected = CanvasObstructedException.class)
    public void findCanvasPositionByScreenshot_whiteWindowDarkDesktopFlashPopup()
            throws IOException {
        assertCanvasPosition("findBrowserTest_flash.png", 126, 94, 1090, 753);
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktop()
            throws IOException {
        assertCanvasPosition("findBrowserTest2.png", 133, 174, 1090, 753);
    }

    @Test(expected = CanvasObstructedException.class)
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktopFlashPopup()
            throws IOException {
        assertCanvasPosition("findBrowserTest2_flash.png", 133, 174, 1090, 753);
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktopFFAt0x0()
            throws IOException {
        assertCanvasPosition("findBrowserTest3.png", 0, 109, 1024, 768);
    }

    @Test(expected = CanvasObstructedException.class)
    public void findCanvasPositionByScreenshot_whiteWindowEclipseOnDesktopFFAt0x0FlasPopup()
            throws IOException {
        assertCanvasPosition("findBrowserTest3_flash.png", 0, 109, 1024, 768);
    }

    @Test
    public void findCanvasPositionByScreenshot_whiteWindowOnSolidWhiteDesktop()
            throws IOException {
        assertCanvasPosition("findBrowserTest4.png", 103, 193, 1090, 755);
    }

    @Test(expected = CanvasObstructedException.class)
    public void findCanvasPositionByScreenshot_windowOverlappingTopLeftCorner()
            throws IOException {
        assertCanvasPosition("findBrowserTest4.png", 0, 0, 1091, 755);
    }

    @Test
    public void findCanvasPositionByScreenshot_opera11OnXP() throws IOException {
        assertCanvasPosition("findBrowser_operaXP.png", 20, 173, 1546, 796);
    }

    @Test
    public void findCanvasPositionByScreenshot_maximizedOpera11OnXP()
            throws IOException {
        assertCanvasPosition("findBrowser_opera11_maximized.png", 0, 61, 1920,
                1084);
    }

    @Test
    public void findCanvasPositionByScreenshot_borderlessWindowWithWhiteBehind()
            throws IOException {
        assertCanvasPosition("findBrowser_borderless_windowbehind.png", 1, 119,
                1024, 768);
    }

    @Test
    public void findCanvasPositionByScreenshot_firefox4WinXP()
            throws IOException {
        assertCanvasPosition("findBrowser_winxp_firefox4.png", 5, 114, 1500,
                850);
    }

    @Test
    public void findCanvasPositionByScreenshot_firefox6WinXP()
            throws IOException {
        assertCanvasPosition("findBrowser_winxp_firefox6.png", 5, 114, 1500,
                850);
    }

    @Test
    public void findCanvasPositionByScreenshot_firefox7WinXP()
            throws IOException {
        assertCanvasPosition("findBrowser_winxp_firefox7.png", 5, 114, 1500,
                850);
    }

    @Test
    public void findCanvasPositionByScreenshot_firefox8WinXP()
            throws IOException {
        assertCanvasPosition("findBrowser_winxp_firefox8.png", 5, 114, 1500,
                850);
    }

    @Test(expected = CanvasObstructedException.class)
    public void findCanvasPositionByScreenshot_tooManyNonWhitePixelsInDragCorner()
            throws IOException {
        assertCanvasPosition("findBrowser_winxp_firefox4_blackCorner.png", 5,
                114, 1500, 850);
    }

}
