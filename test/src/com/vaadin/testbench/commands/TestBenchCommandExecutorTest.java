package com.vaadin.testbench.commands;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.collect.ImmutableMap;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageComparisonTest;
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
                createNiceMock(WebDriver.class),
                createNiceMock(ImageComparison.class));
        assertTrue(tbce instanceof TestBenchCommands);
    }

    @Test
    public void setTestName_executesOnRemote() {
        TestBenchCommandExecutor tbce = createMockBuilder(
                TestBenchCommandExecutor.class)
                .addMockedMethod("execute")
                .withConstructor(createNiceMock(RemoteWebDriver.class),
                        createNiceMock(ImageComparison.class)).createMock();
        expect(
                tbce.execute(TestBenchCommands.SET_TEST_NAME,
                        ImmutableMap.of("name", "foo"))).andReturn(null).once();
        replay(tbce);

        tbce.setTestName("foo");

        verify(tbce);
    }

    @Test
    public void testCompareScreen_takesScreenshotAndComparesImages()
            throws IOException {
        WebDriver driver = createMock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        expect(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
                .andReturn(screenshotBytes);
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        eq("foo"),
                        eq(Parameters.getScreenshotComparisonTolerance()),
                        eq(Parameters.isCaptureScreenshotOnFailure())))
                .andReturn(true);
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock);
        assertTrue(tbce.compareScreen("foo"));

        verify(driver, icMock);
    }

    @Test
    public void testCompareScreen_defaultNrOfRetriesImagesEqual_succeedsTheFirstTime()
            throws IOException {
        WebDriver driver = createMock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        expect(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
                .andReturn(screenshotBytes).once();
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        eq("foo"),
                        eq(Parameters.getScreenshotComparisonTolerance()),
                        eq(Parameters.isCaptureScreenshotOnFailure())))
                .andReturn(true).once();
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock);
        assertTrue(tbce.compareScreen("foo"));

        verify(driver, icMock);
    }

    @Test
    public void testCompareScreen_defaultNrOfRetriesImagesDiffer_retriesTwice()
            throws IOException {
        WebDriver driver = createMock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        expect(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
                .andReturn(screenshotBytes).times(2);
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        eq("foo"),
                        eq(Parameters.getScreenshotComparisonTolerance()),
                        eq(Parameters.isCaptureScreenshotOnFailure())))
                .andReturn(false).times(2);
        replay(driver, icMock);

        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(driver,
                icMock);
        assertFalse(tbce.compareScreen("foo"));

        verify(driver, icMock);
    }

    @Test
    public void testCompareScreen_fourRetriesImagesDiffer_retriesFourTimes()
            throws IOException {
        System.setProperty(Parameters.SCREENSHOT_MAX_RETRIES, "4");
        try {
            WebDriver driver = createMock(FirefoxDriver.class);
            byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                    "cursor-bottom-edge-off.png");
            expect(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
                    .andReturn(screenshotBytes).times(4);
            ImageComparison icMock = createMock(ImageComparison.class);
            expect(
                    icMock.imageEqualToReference(isA(BufferedImage.class),
                            eq("foo"),
                            eq(Parameters.getScreenshotComparisonTolerance()),
                            eq(Parameters.isCaptureScreenshotOnFailure())))
                    .andReturn(false).times(4);
            replay(driver, icMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                    driver, icMock);
            assertFalse(tbce.compareScreen("foo"));

            verify(driver, icMock);
        } finally {
            System.clearProperty(Parameters.SCREENSHOT_MAX_RETRIES);
        }
    }

}
