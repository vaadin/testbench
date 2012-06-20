package com.vaadin.testbenchexample;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

/**
 * This example executes tests using a hub. Often the hub is run on a server in
 * your intranet and it provides an "army" of testing bots with various
 * operating systems and browsers. Refer to the manual how to setup a hub.
 * <p>
 * The hub can also be bought as a service. This test demonstrates this kind of
 * usage. To simplify setup (avoid setting up tunnel or deploying to server
 * visible in Internet) we actually run this test against the calculator demo
 * running in demo.vaadin.com. The example should though be easily be fitted to
 * your needs for either local hub or to external service.
 * <p>
 * The example is by default ignored as you need to fill in your username and
 * password.
 * 
 */
public class UsingHubITCase extends TestBenchTestCase {

    private String baseUrl;

    private String clientKey = "INSERT-YOUR-CLIENT-KEY-HERE";
    private String clientSecret = "INSERT-YOUR-CLIENT-KEY-HERE";

    @Before
    public void setUp() throws Exception {
        // Create a RemoteDriver against the hub.
        // In you local setup you don't need key and secret, but if you use
        // service like testingbot.com they can be used for authentication
        URL testingbotdotcom = new URL("http://" + clientKey + ":"
                + clientSecret + "@hub.testingbot.com:4444/wd/hub");
        setDriver(TestBench.createDriver(new RemoteWebDriver(testingbotdotcom,
                DesiredCapabilities.iphone())));
        baseUrl = "http://demo.vaadin.com/Calc/";
    }

    @Test
    @Ignore("Requires testingbot.com credientials")
    public void testOnePlusTwo() throws Exception {
        // run the test as with "local bots"
        openCalculator();
        getDriver().findElement(By.xpath("//*[text() = '1']")).click();
        getDriver().findElement(By.xpath("//*[text() = '+']")).click();
        getDriver().findElement(By.xpath("//*[text() = '2']")).click();
        getDriver().findElement(By.xpath("//*[text() = '=']")).click();
        assertTrue(isElementPresent(By
                .xpath("//div[text() = '3.0' and contains(@class, 'v-label')]")));

        // Thats it. Services may provide also some other goodies like the video
        // replay of your test in testingbot.com
    }

    private void openCalculator() {
        getDriver().get(baseUrl);
    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
