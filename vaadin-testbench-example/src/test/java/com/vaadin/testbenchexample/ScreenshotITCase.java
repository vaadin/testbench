package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;

/**
 * This example contains usage examples of screenshot comparison feature.
 * <p>
 * To see how TestBench visualizes changed parts, either modify reference images
 * from src/test/resources/screenshots or change the application a bit (like add
 * components).
 * <p>
 * Results of failed screenshot comparisons are stored to
 * target/testbench/screenshot_errors. If you wish to make them pass, copy
 * failed screenshots to reference directory. Note that in in the random log
 * test the reference screenshot should have the log part masked as transparent
 * (i.e., opacity less than 100%). You can use software such as The GIMP (
 * http://www.gimp.org/ ) or some other photo manipulation application for
 * masking areas of images. You don't need to cut a hole, just make an area less
 * than 100% opaque.<br>
 * TestBench ignores transparent pixels from the comparison and changes on those
 * areas are permitted.
 * <p>
 * Note, that this test case is marked with {@code @Ignore} by default - you
 * need to remove that to have the test do anything. The test is disabled by
 * default, since the reference renderings vary somewhat from machine to
 * machine.
 */
@Ignore
public class ScreenshotITCase extends TestBase {

    /**
     * We'll want to perform some additional setup functions, so we override the
     * setUp() function defined in TestBase
     */
    @Override
    @Before
    public void setUp() throws Exception {

        // We must remember to explicitly call the superclass setUp() method to
        // get a driver set up
        super.setUp();

        // Try to adjust browser window size so that its content area is 500x400
        // pixels
        testBench().resizeViewPortTo(500, 400);

        // Define the default directory for reference screenshots
        Parameters
                .setScreenshotReferenceDirectory("src/test/resources/screenshots");

        // Define the directory where possible error files and screenshots
        // should go
        Parameters
                .setScreenshotErrorDirectory("target/testbench/screenshot_errors");
    }

    /**
     * Calculates one plus two for the absolute reference test
     */
    private void calculateOnePlusTwo() {
        $(ButtonElement.class).caption("1").first().click();
        $(ButtonElement.class).caption("+").first().click();
        $(ButtonElement.class).caption("2").first().click();
        $(ButtonElement.class).caption("=").first().click();
    }

    /**
     * Adds random values together for the masked reference test
     */
    private void addRandomValues() {
        WebElement plusButton = $(ButtonElement.class).caption("+").first();
        Random rnd = new Random();
        for (int i = 0; i < rnd.nextInt(10) + 1; i++) {
            $(ButtonElement.class).caption("" + (rnd.nextInt(9) + 1)).first().click();
            plusButton.click();
        }

        $(ButtonElement.class).caption("" + (rnd.nextInt(9) + 1)).first().click();
        $(ButtonElement.class).caption("=").first().click();
    }

    @Test
    public void testOnePlusTwo() throws Exception {

        calculateOnePlusTwo();
        assertEquals("3.0", $(TextFieldElement.class).first().getAttribute("value"));

        // Compare screen with reference image with id "oneplustwo" from the
        // reference image directory. Reference image filenames also contain
        // browser, version and platform.
        assertTrue(testBench().compareScreen("oneplustwo"));

        // If the id based reference image don't fit for your needs, you can
        // also use a direct File reference like this:
        // testBench().compareScreen(new File("path/to/MyFile"));
    }

    @Test
    public void testOnePlusTwoWithRandomLog() throws Exception {

        // Add a bunch of random values together to fill the log with
        // randomness.
        addRandomValues();
        WebElement display = $(TextFieldElement.class).first();

        // Clear and calculate 1 + 2
        $(ButtonElement.class).caption("C").first().click();
        calculateOnePlusTwo();
        assertEquals("3.0", display.getAttribute("value"));

        // Compare with a screenshot, which should pass as the log is
        // masked.
        assertTrue(testBench().compareScreen("onePlusTwoMasked"));
    }

}
