package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 */
public interface TestBenchCommands extends CanWaitForVaadin {
    static String SET_TEST_NAME = "setTestName";

    void setTestName(String testName);

    String getRemoteControlName();

    void expectDialog(WebElement element, Keys... modifierKeysPressed);

    boolean closeNotification(WebElement element);

    void showTooltip(WebElement element);

    void scroll(WebElement element, int scrollTop);

    void scrollLeft(WebElement element, int scrollLeft);

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.vaadin.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     * 
     * @param referenceId
     *            the ID of the reference image
     * @return
     * @throws IOException
     * @throws AssertionError
     */
    boolean compareScreen(String referenceId) throws IOException,
            AssertionError;

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.vaadin.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     * 
     * @param reference
     *            the reference image file
     * @return
     * @throws IOException
     * @throws AssertionError
     */
    boolean compareScreen(File reference) throws IOException, AssertionError;

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.vaadin.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     * 
     * @param reference
     *            the reference image
     * @param referenceName
     *            the filename of the reference image. Used when writing the
     *            error files.
     * @return
     * @throws IOException
     * @throws AssertionError
     */
    boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException, AssertionError;

    long timeSpentRenderingLastRequest();

    long totalTimeSpentRendering();

    long timeSpentServicingLastRequest();

    long totalTimeSpentServicingRequests();

    WebElement findElementByVaadinSelector(String selector);
}
