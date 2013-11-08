package com.vaadin.testbenchexample;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.TestBenchTestCase;

/**
 * Base class for all our tests, allowing us to change the applicable driver,
 * test URL or other configurations in one place. For an example of setting up a
 * hub configuration, see {@link UsingHubITCase}.
 * 
 */
public class TestBase extends TestBenchTestCase {

    private static final String baseUrl = "http://localhost:8080/testbenchexample/";

    @Before
    public void setUp() throws Exception {

        // Create a new Selenium driver - it is automatically extended to work
        // with TestBench
        setDriver(new FirefoxDriver());

        // Open the test application URL with the ?restartApplication URL
        // parameter to ensure Vaadin provides us with a fresh UI instance.
        getDriver().get(baseUrl + "?restartApplication");

    }

    @After
    public void tearDown() throws Exception {

        // Calling quit() on the driver closes the test browser.
        // When called like this, the browser is immediately closed on _any_
        // error. If you wish to take a screenshot of the browser at the time
        // the error occurred, you'll need to @Override this method and insert
        // code to grab a screenshot and _then_ call driver.quit().
        getDriver().quit();
    }
}
