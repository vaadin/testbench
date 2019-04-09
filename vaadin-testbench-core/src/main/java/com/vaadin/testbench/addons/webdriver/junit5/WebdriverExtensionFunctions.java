package com.vaadin.testbench.addons.webdriver.junit5;

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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

public interface WebdriverExtensionFunctions {

    String WEBDRIVER_STORAGE_KEY = "webdriver";

    static WebDriver webdriver(ExtensionContext context) {
        return storeMethodPlain(context).get(WEBDRIVER_STORAGE_KEY, WebDriver.class);
    }

    static void storeWebDriver(ExtensionContext context, WebDriver webDriver) {
        storeMethodPlain(context).put(WEBDRIVER_STORAGE_KEY, webDriver);
    }

    static void removeWebDriver(ExtensionContext context) {
        storeMethodPlain(context).remove(WEBDRIVER_STORAGE_KEY);
    }
}
