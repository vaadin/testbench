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

import org.junit.Before;
import org.junit.Test;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestBenchCommandExecutorTest {

    private static final String IMG_FOLDER = ImageComparisonTest.class
            .getPackage().getName().replace('.', '/');

    @Before
    public void setUp() {
    }

    @Test
    public void testTestBenchCommandExecutorIsATestBenchCommands() {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                Mockito.mock(ImageComparison.class),
                Mockito.mock(ReferenceNameGenerator.class));
        tbce.setDriver(Mockito.mock(TestBenchDriverProxy.class));
        assertTrue(tbce instanceof TestBenchCommands);
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
        assertTrue(tbce.compareScreen("foo"));
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
        assertTrue(tbce.compareScreen("foo"));

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
        assertFalse(tbce.compareScreen("foo"));

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
            assertFalse(tbce.compareScreen("foo"));

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
        assertTrue(tbce.compareScreen(referenceFile));

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
            assertFalse(tbce.compareScreen(referenceFile));

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
        assertTrue(tbce.compareScreen(mockImg, "bar name"));

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
            assertFalse(tbce.compareScreen(mockImg, "bar name"));

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
        Mockito.when(driver.executeAsyncScript(
                Mockito.contains("window.Vaadin.Flow"), Mockito.any()))
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
        assertEquals(1000, milliseconds);

    }

    @Test
    public void testTotalTimeSpentRendering() {
        FirefoxDriver jse = mockJSExecutor(false);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.totalTimeSpentRendering();
        assertEquals(2000, milliseconds);

    }

    @Test
    public void testTotalTimeSpentServicingRequests() {
        FirefoxDriver jse = mockJSExecutor(true);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.totalTimeSpentServicingRequests();
        assertEquals(3000, milliseconds);

    }

    @Test
    public void waitForVaadin_idle_asksBrowserOnce() {
        FirefoxDriver driver = Mockito.mock(FirefoxDriver.class);
        Mockito.when(driver.executeAsyncScript(Mockito.contains("flow.ready("),
                Mockito.any())).thenReturn(Boolean.TRUE);

        newCommandExecutor(driver).waitForVaadin();

        Mockito.verify(driver).executeAsyncScript(
                Mockito.contains("flow.ready("),
                // Flow's own timeout must end the call before the default
                // WebDriver script timeout of 30s does
                Mockito.longThat(timeout -> timeout > 0 && timeout < 30000));
    }

    @Test
    public void waitForVaadin_notIdleOrInterrupted_asksAgain() {
        FirefoxDriver driver = Mockito.mock(FirefoxDriver.class);
        Mockito.when(
                driver.executeAsyncScript(Mockito.anyString(), Mockito.any()))
                .thenReturn(Boolean.FALSE)
                .thenThrow(new ScriptTimeoutException("script timeout"),
                        new JavascriptException("document unloaded"))
                .thenReturn(Boolean.TRUE);

        newCommandExecutor(driver).waitForVaadin();

        Mockito.verify(driver, Mockito.times(4))
                .executeAsyncScript(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void waitForVaadin_scriptError_isNotRetried() {
        FirefoxDriver driver = Mockito.mock(FirefoxDriver.class);
        JavascriptException error = new JavascriptException(
                "TypeError: clients[client].isActive is not a function");
        Mockito.when(
                driver.executeAsyncScript(Mockito.anyString(), Mockito.any()))
                .thenThrow(error);
        TestBenchCommandExecutor tbce = newCommandExecutor(driver);

        JavascriptException thrown = assertThrows(JavascriptException.class,
                tbce::waitForVaadin);

        assertSame(error, thrown);
        Mockito.verify(driver).executeAsyncScript(Mockito.anyString(),
                Mockito.any());
    }

    private TestBenchCommandExecutor newCommandExecutor(FirefoxDriver driver) {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        return tbce;
    }

    private FirefoxDriver mockJSExecutor(boolean forcesSync) {
        FirefoxDriver jse = Mockito.mock(FirefoxDriver.class);
        Mockito.when(jse.executeAsyncScript(
                Mockito.contains("window.Vaadin.Flow"), Mockito.any()))
                .thenReturn(Boolean.TRUE);
        Mockito.when(jse.executeScript(Mockito.contains("getProfilingData()")))
                .thenReturn(Arrays.asList(1000L, 2000L, 3000L));
        return jse;
    }
}
