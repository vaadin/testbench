package com.vaadin.testbenchexample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.screenshot.ImageFileUtil;

public class ExampleITCase extends TestBenchTestCase {

    @Before
    public void setUp() {
        // Keep reference screen shots in the Maven resources directory
        Parameters
                .setScreenshotReferenceDirectory("src/test/resources/screenshots/reference");
        // .. and put error reports in the target directory
        Parameters.setScreenshotErrorDirectory("target/testbench/errors");
        Parameters.setCaptureScreenshotOnFailure(true);

        setDriver(TestBench.createDriver(new FirefoxDriver()));
        // dimension includes browser chrome
        getDriver().manage().window().setSize(new Dimension(1024, 768));
    }

    @After
    public void tearDown() {
        getDriver().quit();
    }

    @Test
    @Ignore
    public void testContextMenu() {
        getDriver().navigate().to("http://demo.vaadin.com/sampler#TreeActions");
        WebElement e = getDriver()
                .findElement(
                        By.xpath("//div[@class='v-tree-node-caption']/div[span='Desktops']"));
        new Actions(getDriver()).moveToElement(e).contextClick(e).perform();
    }

    @Test
    @Ignore
    public void testToolTip() {
        getDriver().navigate().to("http://demo.vaadin.com/sampler#Tooltips");
        WebElement e = getDriver()
                .findElement(
                        By.xpath("//span[@class='v-button-wrap' and span='Mouse over for plain tooltip']"));
        tbElement(e).showTooltip();
    }

    @Test
    @Ignore
    public void testVaadinSelectors() {
        getDriver().navigate()
                .to("http://demo.vaadin.com/sampler#TableSorting");
        // Sort the table in reverse alphabetical order on the country names.
        WebElement e = testBench(getDriver())
                .findElementByVaadinSelector(
                        "sampler::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VScrollTable[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[1]/domChild[2]");
        e.click();
        e.click();

        // Check that the first row is 'ÅLAND ISLANDS'
        e = testBench(getDriver())
                .findElementByVaadinSelector(
                        "sampler::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VScrollTable[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[0]");
        assertEquals("ÅLAND ISLANDS", e.getText());
    }

    @Test
    @Ignore("This test needs a running Hub with at least one Node providing a Firefox browser. Start a hub on localhost, a node anywhere and then enable this")
    public void testRemoteWebDriver() throws MalformedURLException {
        DesiredCapabilities capability = DesiredCapabilities.firefox();
        WebDriver driver = TestBench.createDriver(new RemoteWebDriver(new URL(
                "http://localhost:4444/wd/hub"), capability));

        try {
            driver.navigate().to("http://demo.vaadin.com/sampler#TreeActions");
            WebElement e = driver
                    .findElement(By
                            .xpath("//div[@class='v-tree-node-caption']/div[span='Desktops']"));
            new Actions(driver).moveToElement(e).contextClick(e).perform();
        } finally {
            driver.quit();
        }
    }

    @Test
    @Ignore
    public void testDemo() throws InterruptedException {
        getDriver().navigate().to(
                "http://demo.vaadin.com/sampler#FormAdvancedLayout");

        WebElement firstNameField = getDriver().findElement(
                By.xpath("(//input[@type='text'])[1]"));
        firstNameField.click();
        firstNameField.clear();
        firstNameField.sendKeys("John");

        WebElement lastNameField = getDriver().findElement(
                By.xpath("(//input[@type='text'])[2]"));
        lastNameField.clear();
        lastNameField.sendKeys("Doe");
        getDriver().findElement(By.cssSelector("div.v-filterselect-button"))
                .click();

        getDriver().findElement(By.cssSelector("input.v-filterselect-input"))
                .sendKeys("ber");

        getDriver()
                .findElement(
                        By.xpath("id('VAADIN_COMBOBOX_OPTIONLIST')//td[span='BERMUDA']"))
                .click();

        WebElement passwordField = getDriver().findElement(
                By.xpath("//input[@type='password']"));
        passwordField.clear();
        passwordField.sendKeys("asdf");

        getDriver().findElement(By.cssSelector("button.v-datefield-button"))
                .click();
        getDriver().findElement(
                By.xpath("id('PID_VAADIN_POPUPCAL')//td[span='17']/span"))
                .click();

        assertEquals(
                "5/17/12",
                getDriver().findElement(By.xpath("(//input)[5]")).getAttribute(
                        "value"));
    }

    @Test
    @Ignore
    public void testScreenshot() throws IOException {
        getDriver().navigate().to("http://google.com");
        assertTrue(
                "Screenshots differ",
                testBench(getDriver()).compareScreen(
                        ImageFileUtil
                                .getReferenceScreenshotFile("actualshot.png")));
        assertTrue(
                "Screenshots differ",
                testBench(getDriver()).compareScreen(
                        ImageFileUtil
                                .getReferenceScreenshotFile("actualshot2.png")));
        assertTrue("Screenshots differ",
                testBench(getDriver()).compareScreen("shot"));
    }
}
