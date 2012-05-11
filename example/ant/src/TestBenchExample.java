import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.After;
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

public class TestBenchExample extends TestBenchTestCase {

    private WebDriver driver;

    @Before
    public void setUp() {
        Parameters.setScreenshotErrorDirectory("screenshots/errors");
        Parameters.setScreenshotReferenceDirectory("screenshots/reference");
        Parameters.setCaptureScreenshotOnFailure(true);

        driver = TestBench.createDriver(new FirefoxDriver());
        // dimension includes browser chrome
        driver.manage().window().setSize(new Dimension(1024, 768));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testContextMenu() {
        driver.navigate().to("http://demo.vaadin.com/sampler#TreeActions");
        WebElement e = driver
                .findElement(By
                        .xpath("//div[@class='v-tree-node-caption']/div[span='Desktops']"));
        new Actions(driver).moveToElement(e).contextClick(e).perform();
    }

    @Test
    public void testToolTip() {
        driver.navigate().to("http://demo.vaadin.com/sampler#Tooltips");
        WebElement e = driver
                .findElement(By
                        .xpath("//span[@class='v-button-wrap' and span='Mouse over for plain tooltip']"));
        testBench(driver).showTooltip(e);
    }

    @Test
    public void testVaadinSelectors() {
        driver.navigate().to("http://demo.vaadin.com/sampler#TableSorting");
        // Sort the table in reverse alphabetical order on the country names.
        WebElement e = testBench(driver)
                .findElementByVaadinSelector(
                        "sampler::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VScrollTable[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[1]/domChild[2]");
        e.click();
        e.click();

        // Check that the first row is 'ÅLAND ISLANDS'
        e = testBench(driver)
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
    public void testDemo() throws InterruptedException {
        driver.navigate().to(
                "http://demo.vaadin.com/sampler#FormAdvancedLayout");

        WebElement firstNameField = driver.findElement(By
                .xpath("(//input[@type='text'])[1]"));
        firstNameField.click();
        firstNameField.clear();
        firstNameField.sendKeys("John");

        WebElement lastNameField = driver.findElement(By
                .xpath("(//input[@type='text'])[2]"));
        lastNameField.clear();
        lastNameField.sendKeys("Doe");
        driver.findElement(By.cssSelector("div.v-filterselect-button")).click();

        driver.findElement(By.cssSelector("input.v-filterselect-input"))
                .sendKeys("ber");

        driver.findElement(
                By.xpath("id('VAADIN_COMBOBOX_OPTIONLIST')//td[span='BERMUDA']"))
                .click();

        WebElement passwordField = driver.findElement(By
                .xpath("//input[@type='password']"));
        passwordField.clear();
        passwordField.sendKeys("asdf");

        driver.findElement(By.cssSelector("button.v-datefield-button")).click();
        driver.findElement(
                By.xpath("id('PID_VAADIN_POPUPCAL')//td[span='17']/span"))
                .click();

        assertEquals("5/17/12", driver.findElement(By.xpath("(//input)[5]"))
                .getAttribute("value"));
    }

    @Test
    public void testScreenshot() throws IOException {
        driver.navigate().to("http://google.com");
        assertTrue(
                "Screenshots differ",
                testBench(driver).compareScreen(
                        ImageFileUtil
                                .getReferenceScreenshotFile("actualshot.png")));
        assertTrue(
                "Screenshots differ",
                testBench(driver).compareScreen(
                        ImageFileUtil
                                .getReferenceScreenshotFile("actualshot2.png")));
        assertTrue("Screenshots differ", testBench(driver)
                .compareScreen("shot"));
    }
}
