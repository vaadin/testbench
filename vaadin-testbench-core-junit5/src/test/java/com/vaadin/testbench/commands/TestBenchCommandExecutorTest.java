/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageComparisonTest;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.testbench.testutils.ImageLoader;

public class TestBenchCommandExecutorTest {

    private static final String IMG_FOLDER = ImageComparisonTest.class
            .getPackage().getName().replace('.', '/');

    /**
     * The value the readiness probe returns when Vaadin is not idle but the
     * page exposes {@code window.Vaadin.Flow.ready()}. Mirrors the sentinel in
     * {@link TestBenchCommandExecutor}.
     */
    private static final String AWAIT_FLOW_READY = "await-flow-ready";

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testTestBenchCommandExecutorIsATestBenchCommands() {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                Mockito.mock(ImageComparison.class),
                Mockito.mock(ReferenceNameGenerator.class));
        tbce.setDriver(Mockito.mock(TestBenchDriverProxy.class));
        Assertions.assertTrue(tbce instanceof TestBenchCommands);
    }

    @Test
    public void testCompareScreen_takesScreenshotAndComparesImages()
            throws IOException {
        WebDriver driver = mockScreenshotDriver(1, true);
        ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                "foo_bar_11");
        ImageComparison icMock = mockImageComparison(1, "foo_bar_11", true);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                rngMock);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        Assertions.assertTrue(tbce.compareScreen("foo"));
    }

    @Test
    public void testCompareScreen_defaultNrOfRetriesImagesEqual_succeedsTheFirstTime()
            throws IOException {
        WebDriver driver = mockScreenshotDriver(1, true);
        ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                "foo_bar_11");
        ImageComparison icMock = mockImageComparison(1, "foo_bar_11", true);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                rngMock);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        Assertions.assertTrue(tbce.compareScreen("foo"));

    }

    @Test
    public void testCompareScreen_defaultNrOfRetriesImagesDiffer_retriesTwice()
            throws IOException {
        WebDriver driver = mockScreenshotDriver(2, true);
        ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                "foo_bar_11");
        ImageComparison icMock = mockImageComparison(2, "foo_bar_11", false);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                rngMock);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        Assertions.assertFalse(tbce.compareScreen("foo"));

    }

    @Test
    public void testCompareScreen_fourRetriesImagesDiffer_retriesFourTimes()
            throws IOException {
        Parameters.setMaxScreenshotRetries(4);
        try {
            WebDriver driver = mockScreenshotDriver(4, true);
            ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                    "foo_bar_11");
            ImageComparison icMock = mockImageComparison(4, "foo_bar_11",
                    false);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                    rngMock);
            tbce.setDriver(TestBench.createDriver(driver, tbce));
            Assertions.assertFalse(tbce.compareScreen("foo"));

        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    @Test
    public void testCompareScreen_acceptsFile() throws IOException {
        File referenceFile = ImageLoader.getImageFile(IMG_FOLDER,
                "cursor-bottom-edge-off.png");

        WebDriver driver = mockScreenshotDriver(1, false);
        ImageComparison icMock = Mockito.mock(ImageComparison.class);
        Mockito.when(
                icMock.imageEqualToReference(Mockito.any(BufferedImage.class),
                        Mockito.any(BufferedImage.class),
                        Mockito.matches("cursor-bottom-edge-off.png"),
                        Mockito.eq(
                                Parameters.getScreenshotComparisonTolerance())))
                .thenReturn(true);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                null);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        Assertions.assertTrue(tbce.compareScreen(referenceFile));

    }

    @Test
    public void testCompareScreen_acceptsFile_retries() throws IOException {
        Parameters.setMaxScreenshotRetries(4);
        try {
            File referenceFile = ImageLoader.getImageFile(IMG_FOLDER,
                    "cursor-bottom-edge-off.png");

            WebDriver driver = mockScreenshotDriver(4, false);
            ImageComparison icMock = Mockito.mock(ImageComparison.class);
            Mockito.when(icMock.imageEqualToReference(
                    Mockito.any(BufferedImage.class),
                    Mockito.any(BufferedImage.class),
                    Mockito.eq("cursor-bottom-edge-off.png"),
                    Mockito.eq(Parameters.getScreenshotComparisonTolerance())))
                    .thenReturn(false);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                    null);
            tbce.setDriver(TestBench.createDriver(driver, tbce));
            Assertions.assertFalse(tbce.compareScreen(referenceFile));

        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    @Test
    public void testCompareScreen_acceptsBufferedImage() throws IOException {
        BufferedImage mockImg = Mockito.mock(BufferedImage.class);

        WebDriver driver = mockScreenshotDriver(1, false);
        ImageComparison icMock = Mockito.mock(ImageComparison.class);
        Mockito.when(icMock.imageEqualToReference(
                Mockito.any(BufferedImage.class),
                Mockito.any(BufferedImage.class), Mockito.eq("bar name"),
                Mockito.eq(Parameters.getScreenshotComparisonTolerance())))
                .thenReturn(true);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                null);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        Assertions.assertTrue(tbce.compareScreen(mockImg, "bar name"));

    }

    @Test
    public void testCompareScreen_acceptsBufferedImage_retries()
            throws IOException {
        Parameters.setMaxScreenshotRetries(4);
        try {
            BufferedImage mockImg = Mockito.mock(BufferedImage.class);

            WebDriver driver = mockScreenshotDriver(4, false);
            ImageComparison icMock = Mockito.mock(ImageComparison.class);
            Mockito.when(icMock.imageEqualToReference(
                    Mockito.any(BufferedImage.class),
                    Mockito.any(BufferedImage.class), Mockito.eq("bar name"),
                    Mockito.eq(Parameters.getScreenshotComparisonTolerance())))
                    .thenReturn(false);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                    null);
            tbce.setDriver(TestBench.createDriver(driver, tbce));
            Assertions.assertFalse(tbce.compareScreen(mockImg, "bar name"));

        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    private WebDriver mockScreenshotDriver(int nrScreenshotsGrabbed,
            boolean expectGetCapabilities) throws IOException {
        RemoteWebDriver driver = Mockito.mock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        Mockito.when(driver.getScreenshotAs(OutputType.BYTES))
                .thenReturn(screenshotBytes);
        Mockito.when(
                driver.executeScript(Mockito.contains("window.Vaadin.Flow")))
                .thenReturn(Boolean.TRUE);
        if (expectGetCapabilities) {
            Capabilities mockedCapabilities = Mockito.mock(Capabilities.class);
            Mockito.when(mockedCapabilities.getBrowserName())
                    .thenReturn("Firefox");
            Mockito.when(driver.getCapabilities())
                    .thenReturn(mockedCapabilities);
        }
        return driver;
    }

    private ReferenceNameGenerator mockReferenceNameGenerator(String refId,
            String expected) {
        ReferenceNameGenerator rngMock = Mockito
                .mock(ReferenceNameGenerator.class);
        Mockito.when(rngMock.generateName(Mockito.eq(refId),
                Mockito.any(Capabilities.class))).thenReturn(expected);
        return rngMock;
    }

    private ImageComparison mockImageComparison(int timesCalled,
            String referenceName, boolean expected) throws IOException {
        ImageComparison icMock = Mockito.mock(ImageComparison.class);
        Mockito.when(icMock.imageEqualToReference(
                Mockito.any(BufferedImage.class), Mockito.eq(referenceName),
                Mockito.eq(Parameters.getScreenshotComparisonTolerance()),
                Mockito.any(Capabilities.class))).thenReturn(expected);
        return icMock;
    }

    @Test
    public void testTimeSpentRenderingLastRequest_callsJavaScript_returnsValueFetchedFromVaadinClient() {
        FirefoxDriver jse = mockJSExecutor(false);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.timeSpentRenderingLastRequest();
        Assertions.assertEquals(1000, milliseconds);

    }

    @Test
    public void testTotalTimeSpentRendering() {
        FirefoxDriver jse = mockJSExecutor(false);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.totalTimeSpentRendering();
        Assertions.assertEquals(2000, milliseconds);

    }

    @Test
    public void testTotalTimeSpentServicingRequests() {
        FirefoxDriver jse = mockJSExecutor(true);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.totalTimeSpentServicingRequests();
        Assertions.assertEquals(3000, milliseconds);

    }

    @Test
    public void waitForVaadin_flowReadyAvailable_delegatesWaitToBrowser() {
        FirefoxDriver driver = mockWaitForVaadinDriver(AWAIT_FLOW_READY,
                Boolean.TRUE);
        Mockito.when(driver.executeAsyncScript(
                Mockito.contains("window.Vaadin.Flow.ready"),
                Mockito.anyLong())).thenReturn(null);

        newCommandExecutor(driver).waitForVaadin();

        Mockito.verify(driver).executeAsyncScript(
                Mockito.contains("window.Vaadin.Flow.ready"),
                Mockito.anyLong());
        // Flow resolving is not enough on its own: the synchronous probe has
        // the final say on whether Vaadin is idle
        Mockito.verify(driver, Mockito.times(2))
                .executeScript(Mockito.contains("window.Vaadin.Flow.clients"));
    }

    @Test
    public void waitForVaadin_awaitingFlowReadyDoesNotSucceed_keepsProbing() {
        FirefoxDriver driver = mockWaitForVaadinDriver(AWAIT_FLOW_READY,
                AWAIT_FLOW_READY, AWAIT_FLOW_READY, Boolean.TRUE);
        // A rejected promise, a page load cancelling the pending script and
        // navigating away from Flow must all be retried, and none of them may
        // escape to the caller
        Mockito.when(driver.executeAsyncScript(Mockito.anyString(),
                Mockito.anyLong()))
                .thenReturn("Vaadin.Flow.ready timed out after 10000ms")
                .thenThrow(new ScriptTimeoutException("script timeout"),
                        new JavascriptException(
                                "window.Vaadin.Flow is undefined"));

        newCommandExecutor(driver).waitForVaadin();

        Mockito.verify(driver, Mockito.times(3))
                .executeAsyncScript(Mockito.anyString(), Mockito.anyLong());
        Mockito.verify(driver, Mockito.times(4))
                .executeScript(Mockito.contains("window.Vaadin.Flow.clients"));
    }

    @Test
    public void waitForVaadin_probeFailsWhilePageLoads_keepsProbing() {
        FirefoxDriver driver = Mockito.mock(FirefoxDriver.class);
        Mockito.when(driver
                .executeScript(Mockito.contains("window.Vaadin.Flow.clients")))
                .thenThrow(new JavascriptException("document is not defined"))
                .thenReturn(Boolean.TRUE);

        newCommandExecutor(driver).waitForVaadin();

        Mockito.verify(driver, Mockito.times(2))
                .executeScript(Mockito.contains("window.Vaadin.Flow.clients"));
    }

    private TestBenchCommandExecutor newCommandExecutor(FirefoxDriver driver) {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        return tbce;
    }

    /**
     * Mocks a driver whose readiness probe returns the given states in order,
     * repeating the last one once they run out.
     */
    private FirefoxDriver mockWaitForVaadinDriver(Object... probeStates) {
        FirefoxDriver driver = Mockito.mock(FirefoxDriver.class);
        Mockito.when(driver
                .executeScript(Mockito.contains("window.Vaadin.Flow.clients")))
                .thenReturn(probeStates[0],
                        Arrays.copyOfRange(probeStates, 1, probeStates.length));
        return driver;
    }

    private FirefoxDriver mockJSExecutor(boolean forcesSync) {
        FirefoxDriver jse = Mockito.mock(FirefoxDriver.class);
        Mockito.when(jse
                .executeScript(Mockito.contains("window.Vaadin.Flow.client")))
                .thenReturn(Boolean.TRUE);
        Mockito.when(jse.executeScript(Mockito.contains("getProfilingData()")))
                .thenReturn(Arrays.asList(1000L, 2000L, 3000L));
        return jse;
    }
}
