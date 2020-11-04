/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.commands;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
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

    @Before
    public void setUp() {
    }

    @Test
    public void testTestBenchCommandExecutorIsATestBenchCommands() {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                createNiceMock(ImageComparison.class),
                createNiceMock(ReferenceNameGenerator.class));
        tbce.setDriver(createNiceMock(TestBenchDriverProxy.class));
        assertTrue(tbce instanceof TestBenchCommands);
    }

    @Test
    public void testCompareScreen_takesScreenshotAndComparesImages()
            throws IOException {
        WebDriver driver = mockScreenshotDriver(1, true);
        ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                "foo_bar_11");
        ImageComparison icMock = mockImageComparison(1, "foo_bar_11", true);
        replay(driver, icMock, rngMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                rngMock);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        assertTrue(tbce.compareScreen("foo"));

        verify(driver, icMock, rngMock);
    }

    @Test
    public void testCompareScreen_defaultNrOfRetriesImagesEqual_succeedsTheFirstTime()
            throws IOException {
        WebDriver driver = mockScreenshotDriver(1, true);
        ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                "foo_bar_11");
        ImageComparison icMock = mockImageComparison(1, "foo_bar_11", true);
        replay(driver, icMock, rngMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                rngMock);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        assertTrue(tbce.compareScreen("foo"));

        verify(driver, icMock, rngMock);
    }

    @Test
    public void testCompareScreen_defaultNrOfRetriesImagesDiffer_retriesTwice()
            throws IOException {
        WebDriver driver = mockScreenshotDriver(2, true);
        ReferenceNameGenerator rngMock = mockReferenceNameGenerator("foo",
                "foo_bar_11");
        ImageComparison icMock = mockImageComparison(2, "foo_bar_11", false);
        replay(driver, icMock, rngMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                rngMock);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        assertFalse(tbce.compareScreen("foo"));

        verify(driver, icMock, rngMock);
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
            replay(driver, icMock, rngMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                    rngMock);
            tbce.setDriver(TestBench.createDriver(driver, tbce));
            assertFalse(tbce.compareScreen("foo"));

            verify(driver, icMock, rngMock);
        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    @Test
    public void testCompareScreen_acceptsFile() throws IOException {
        File referenceFile = ImageLoader.getImageFile(IMG_FOLDER,
                "cursor-bottom-edge-off.png");

        WebDriver driver = mockScreenshotDriver(1, false);
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(icMock.imageEqualToReference(isA(BufferedImage.class),
                isA(BufferedImage.class), eq("cursor-bottom-edge-off.png"),
                eq(Parameters.getScreenshotComparisonTolerance())))
                        .andReturn(true);
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                null);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        assertTrue(tbce.compareScreen(referenceFile));

        verify(driver, icMock);
    }

    @Test
    public void testCompareScreen_acceptsFile_retries() throws IOException {
        Parameters.setMaxScreenshotRetries(4);
        try {
            File referenceFile = ImageLoader.getImageFile(IMG_FOLDER,
                    "cursor-bottom-edge-off.png");

            WebDriver driver = mockScreenshotDriver(4, false);
            ImageComparison icMock = createMock(ImageComparison.class);
            expect(icMock.imageEqualToReference(isA(BufferedImage.class),
                    isA(BufferedImage.class), eq("cursor-bottom-edge-off.png"),
                    eq(Parameters.getScreenshotComparisonTolerance())))
                            .andReturn(false).times(4);
            replay(driver, icMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                    null);
            tbce.setDriver(TestBench.createDriver(driver, tbce));
            assertFalse(tbce.compareScreen(referenceFile));

            verify(driver, icMock);
        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    @Test
    public void testCompareScreen_acceptsBufferedImage() throws IOException {
        BufferedImage mockImg = createNiceMock(BufferedImage.class);

        WebDriver driver = mockScreenshotDriver(1, false);
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(icMock.imageEqualToReference(isA(BufferedImage.class),
                isA(BufferedImage.class), eq("bar name"),
                eq(Parameters.getScreenshotComparisonTolerance())))
                        .andReturn(true);
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                null);
        tbce.setDriver(TestBench.createDriver(driver, tbce));
        assertTrue(tbce.compareScreen(mockImg, "bar name"));

        verify(driver, icMock);
    }

    @Test
    public void testCompareScreen_acceptsBufferedImage_retries()
            throws IOException {
        Parameters.setMaxScreenshotRetries(4);
        try {
            BufferedImage mockImg = createNiceMock(BufferedImage.class);

            WebDriver driver = mockScreenshotDriver(4, false);
            ImageComparison icMock = createMock(ImageComparison.class);
            expect(icMock.imageEqualToReference(isA(BufferedImage.class),
                    isA(BufferedImage.class), eq("bar name"),
                    eq(Parameters.getScreenshotComparisonTolerance())))
                            .andReturn(false).times(4);
            replay(driver, icMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(icMock,
                    null);
            tbce.setDriver(TestBench.createDriver(driver, tbce));
            assertFalse(tbce.compareScreen(mockImg, "bar name"));

            verify(driver, icMock);
        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    private WebDriver mockScreenshotDriver(int nrScreenshotsGrabbed,
            boolean expectGetCapabilities) throws IOException {
        RemoteWebDriver driver = createMock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        expect(driver.getScreenshotAs(OutputType.BYTES))
                .andReturn(screenshotBytes).times(nrScreenshotsGrabbed);
        expect(driver.executeScript(contains("window.Vaadin.Flow")))
                .andReturn(Boolean.TRUE).anyTimes();
        if (expectGetCapabilities) {
            Capabilities mockedCapabilities = createNiceMock(Capabilities.class);
            expect(mockedCapabilities.getBrowserName()).andReturn("Firefox");
            replay(mockedCapabilities);
            expect(driver.getCapabilities())
                    .andReturn(mockedCapabilities).once();
        }
        return driver;
    }

    private ReferenceNameGenerator mockReferenceNameGenerator(String refId,
            String expected) {
        ReferenceNameGenerator rngMock = createMock(
                ReferenceNameGenerator.class);
        expect(rngMock.generateName(eq(refId), isA(Capabilities.class)))
                .andReturn(expected);
        return rngMock;
    }

    private ImageComparison mockImageComparison(int timesCalled,
            String referenceName, boolean expected) throws IOException {
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(icMock.imageEqualToReference(isA(BufferedImage.class),
                eq(referenceName),
                eq(Parameters.getScreenshotComparisonTolerance()),
                isA(Capabilities.class))).andReturn(expected)
                        .times(timesCalled);
        return icMock;
    }

    @Test
    public void testTimeSpentRenderingLastRequest_callsJavaScript_returnsValueFetchedFromVaadinClient() {
        FirefoxDriver jse = mockJSExecutor(false);
        replay(jse);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.timeSpentRenderingLastRequest();
        assertEquals(1000, milliseconds);

        verify(jse);
    }

    @Test
    public void testTotalTimeSpentRendering() {
        FirefoxDriver jse = mockJSExecutor(false);
        replay(jse);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.totalTimeSpentRendering();
        assertEquals(2000, milliseconds);

        verify(jse);
    }

    @Test
    public void testTotalTimeSpentServicingRequests() {
        FirefoxDriver jse = mockJSExecutor(true);
        replay(jse);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(null,
                null);
        tbce.setDriver(TestBench.createDriver(jse, tbce));
        long milliseconds = tbce.totalTimeSpentServicingRequests();
        assertEquals(3000, milliseconds);

        verify(jse);
    }

    private FirefoxDriver mockJSExecutor(boolean forcesSync) {
        FirefoxDriver jse = createNiceMock(FirefoxDriver.class);
        expect(jse.executeScript(contains("getProfilingData()")))
                .andReturn(Arrays.asList(1000L, 2000L, 3000L));
        expect(jse.executeScript(contains("window.Vaadin.Flow.client")))
                .andReturn(Boolean.TRUE).anyTimes();
        return jse;
    }
}
