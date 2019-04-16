package com.vaadin.testbench.addons.junit5.extensions.unittest;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.TestBenchLogger.logger;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.removePageObject;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.removeWebDriver;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.webdriver;

public class PageObjectWebDriverCleanerExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        logger().debug("PageObjectWebDriverCleanerExtension -> remove PageObject");
        removePageObject(context);

        final WebDriver webDriver = webdriver(context);
        logger().debug("Quit webdriver of type " + webdriverName(webDriver));
        webDriver.quit();

        logger().debug("PageObjectWebDriverCleanerExtension -> remove Webdriver");
        removeWebDriver(context);
    }
}
