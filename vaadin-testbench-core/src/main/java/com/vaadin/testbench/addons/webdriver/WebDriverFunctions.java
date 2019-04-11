package com.vaadin.testbench.addons.webdriver;

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

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public interface WebDriverFunctions {

    static String webdriverName(WebDriver driver) {
        return driver instanceof RemoteWebDriver
                ? formatRemoteWebDriverName((RemoteWebDriver) driver)
                : driver.toString();
    }

    static String formatRemoteWebDriverName(RemoteWebDriver driver) {
        return driver.getCapabilities().getBrowserName()
                + " "
                + driver.getCapabilities().getVersion()
                + " / "
                + driver.getCapabilities().getPlatform();
    }

    static void takeScreenshot(String logicalName, WebDriver driver) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            // Write to target/screenshot-[timestamp].jpg
            final String directory = "target";
            Files.createDirectory(Paths.get(directory));
            final FileOutputStream out = new FileOutputStream(directory + "/screenshot-"
                    + logicalName + "-" + LocalDateTime.now() + ".png");
            out.write(outputStream.toByteArray());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void takeScreenshot(WebDriver driver) {
        takeScreenshot("", driver);
    }
}
