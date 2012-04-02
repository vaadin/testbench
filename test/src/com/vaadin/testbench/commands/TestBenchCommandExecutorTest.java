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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.collect.ImmutableMap;
import com.vaadin.testbench.Parameters;
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
                createNiceMock(WebDriver.class),
                createNiceMock(ImageComparison.class),
                createNiceMock(ReferenceNameGenerator.class));
        assertTrue(tbce instanceof TestBenchCommands);
    }

    @Test
    public void setTestName_executesOnRemote() {
        TestBenchCommandExecutor tbce = createMockBuilder(
                TestBenchCommandExecutor.class)
                .addMockedMethod("execute")
                .withConstructor(createNiceMock(RemoteWebDriver.class),
                        createNiceMock(ImageComparison.class),
                        createNiceMock(ReferenceNameGenerator.class))
                .createMock();
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
        WebDriver driver = mockScreenshotDriver(1);
        ReferenceNameGenerator rngMock = mockReferenceNumberGenerator("foo",
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
        WebDriver driver = mockScreenshotDriver(1);
        ReferenceNameGenerator rngMock = mockReferenceNumberGenerator("foo",
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
        WebDriver driver = mockScreenshotDriver(2);
        ReferenceNameGenerator rngMock = mockReferenceNumberGenerator("foo",
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
        System.setProperty(Parameters.SCREENSHOT_MAX_RETRIES, "4");
        try {
            WebDriver driver = mockScreenshotDriver(4);
            ReferenceNameGenerator rngMock = mockReferenceNumberGenerator(
                    "foo", "foo_bar_11");
            ImageComparison icMock = mockImageComparison(4, "foo_bar_11", false);
            replay(driver, icMock, rngMock);

            TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                    driver, icMock, rngMock);
            assertFalse(tbce.compareScreen("foo"));

            verify(driver, icMock, rngMock);
        } finally {
            System.clearProperty(Parameters.SCREENSHOT_MAX_RETRIES);
        }
    }

    private WebDriver mockScreenshotDriver(int nrScreenshotsGrabbed)
            throws IOException {
        WebDriver driver = createMock(FirefoxDriver.class);
        byte[] screenshotBytes = ImageLoader.loadImageBytes(IMG_FOLDER,
                "cursor-bottom-edge-off.png");
        expect(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
                .andReturn(screenshotBytes).times(nrScreenshotsGrabbed);
        expect(((HasCapabilities) driver).getCapabilities()).andReturn(
                createNiceMock(Capabilities.class)).once();
        return driver;
    }

    private ReferenceNameGenerator mockReferenceNumberGenerator(String refId,
            String expected) {
        ReferenceNameGenerator rngMock = createMock(ReferenceNameGenerator.class);
        expect(rngMock.generateName(eq(refId), isA(Capabilities.class)))
                .andReturn(expected);
        return rngMock;
    }

    private ImageComparison mockImageComparison(int timesCalled,
            String referenceName, boolean expected) {
        ImageComparison icMock = createMock(ImageComparison.class);
        expect(
                icMock.imageEqualToReference(isA(BufferedImage.class),
                        eq(referenceName),
                        eq(Parameters.getScreenshotComparisonTolerance()),
                        eq(Parameters.isCaptureScreenshotOnFailure())))
                .andReturn(expected).times(timesCalled);
        return icMock;
    }

}
