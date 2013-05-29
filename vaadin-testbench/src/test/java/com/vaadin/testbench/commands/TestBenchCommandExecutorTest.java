/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.testbench.commands;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageComparisonTest;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.testbench.testutils.ImageLoader;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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

public class TestBenchCommandExecutorTest {

    private static final String IMG_FOLDER = ImageComparisonTest.class
            .getPackage().getName().replace('.', '/');

    @Before
    public void setUp() {
    }

    @Test
    public void testTestBenchCommandExecutorIsATestBenchCommands() {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                createNiceMock(WebDriver.class),
                createNiceMock(ImageComparison.class),
                createNiceMock(ReferenceNameGenerator.class));
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

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock, rngMock);
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

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock, rngMock);
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

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock, rngMock);
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
            ImageComparison icMock = mockImageComparison(4, "foo_bar_11", false);
            replay(driver, icMock, rngMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                    driver, icMock, rngMock);
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
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        isA(BufferedImage.class),
                        eq("cursor-bottom-edge-off.png"),
                        eq(Parameters.getScreenshotComparisonTolerance())
                ))
                .andReturn(true);
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock, null);
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
            expect(
                    icMock.imageEqualToReference(isA(BufferedImage.class),
                            isA(BufferedImage.class),
                            eq("cursor-bottom-edge-off.png"),
                            eq(Parameters.getScreenshotComparisonTolerance())
                    ))
                    .andReturn(false).times(4);
            replay(driver, icMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                    driver, icMock, null);
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
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        isA(BufferedImage.class), eq("bar name"),
                        eq(Parameters.getScreenshotComparisonTolerance())
                ))
                .andReturn(true);
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock, null);
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
            expect(
                    icMock.imageEqualToReference(isA(BufferedImage.class),
                            isA(BufferedImage.class), eq("bar name"),
                            eq(Parameters.getScreenshotComparisonTolerance())
                    ))
                    .andReturn(false).times(4);
            replay(driver, icMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                    driver, icMock, null);
            assertFalse(tbce.compareScreen(mockImg, "bar name"));

            verify(driver, icMock);
        } finally {
            Parameters.setMaxScreenshotRetries(2);
        }
    }

    private WebDriver mockScreenshotDriver(int nrScreenshotsGrabbed,
                                           boolean expectGetCapabilities) throws IOException {
        WebDriver driver = createMock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        expect(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
                .andReturn(screenshotBytes).times(nrScreenshotsGrabbed);
        if (expectGetCapabilities) {
            expect(((HasCapabilities) driver).getCapabilities()).andReturn(
                    createNiceMock(Capabilities.class)).once();
        }
        return driver;
    }

    private ReferenceNameGenerator mockReferenceNameGenerator(String refId,
                                                              String expected) {
        ReferenceNameGenerator rngMock = createMock(ReferenceNameGenerator.class);
        expect(rngMock.generateName(eq(refId), isA(Capabilities.class)))
                .andReturn(expected);
        return rngMock;
    }

    private ImageComparison mockImageComparison(int timesCalled,
                                                String referenceName, boolean expected) throws IOException {
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        eq(referenceName),
                        eq(Parameters.getScreenshotComparisonTolerance()),
                        isA(Capabilities.class))).andReturn(expected).times(
                timesCalled);
        return icMock;
    }

    @Test
    public void testProvidesPerformanceData() {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                createNiceMock(WebDriver.class), null, null);
        long milliseconds = 0;
        milliseconds = tbce.timeSpentRenderingLastRequest();
        assertEquals(-1, milliseconds);
        milliseconds = tbce.totalTimeSpentRendering();
        assertEquals(-1, milliseconds);
        milliseconds = tbce.timeSpentServicingLastRequest();
        assertEquals(-1, milliseconds);
        milliseconds = tbce.totalTimeSpentServicingRequests();
        assertEquals(-1, milliseconds);
    }

    @Test
    public void testTimeSpentRenderingLastRequest_callsJavaScript_returnsValueFetchedFromVaadinClient() {
        FirefoxDriver jse = mockJSExecutor(false);
        replay(jse);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(jse, null,
                null);
        long milliseconds = tbce.timeSpentRenderingLastRequest();
        assertEquals(1000, milliseconds);

        verify(jse);
    }

    @Test
    public void testTotalTimeSpentRendering() {
        FirefoxDriver jse = mockJSExecutor(false);
        replay(jse);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(jse, null,
                null);
        long milliseconds = tbce.totalTimeSpentRendering();
        assertEquals(2000, milliseconds);

        verify(jse);
    }

    @Test
    public void testTotalTimeSpentServicingRequests() {
        FirefoxDriver jse = mockJSExecutor(true);
        replay(jse);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(jse, null,
                null);
        long milliseconds = tbce.totalTimeSpentServicingRequests();
        assertEquals(3000, milliseconds);

        verify(jse);
    }

    private FirefoxDriver mockJSExecutor(boolean forcesSync) {
        FirefoxDriver jse = createMock(FirefoxDriver.class);
        if (forcesSync) {
            expect(jse.executeScript("window.vaadin.forceSync()")).andReturn(
                    null);
        }
        expect(jse.executeScript(contains("getProfilingData()"))).andReturn(
                Arrays.asList(1000L, 2000L, 3000L));
        return jse;
    }
}
