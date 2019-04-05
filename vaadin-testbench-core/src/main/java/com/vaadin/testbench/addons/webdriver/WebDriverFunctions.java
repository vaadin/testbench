package com.vaadin.testbench.addons.webdriver;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
            final FileOutputStream out = new FileOutputStream("target/screenshot-"
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
