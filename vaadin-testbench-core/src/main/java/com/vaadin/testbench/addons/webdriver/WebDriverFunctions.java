package com.vaadin.testbench.addons.webdriver;

import com.vaadin.frp.matcher.Case;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.success;
import static java.util.Optional.ofNullable;
import static org.openqa.selenium.By.id;

public interface WebDriverFunctions {

    static Function<WebDriver, String> webdriverName() {
        return driver -> Case
                .match(
                        matchCase(() -> success(driver.toString())),
                        matchCase(() -> driver instanceof RemoteWebDriver, () -> success(formatRemoteWebDriverName().apply((RemoteWebDriver) driver))))
                .getOrElse(() -> "WebDriver has no name (unexpected)");
    }

    static Function<RemoteWebDriver, String> formatRemoteWebDriverName() {
        return (webDriver) -> webDriver.getCapabilities().getBrowserName()
                + " "
                + webDriver.getCapabilities().getVersion()
                + " / "
                + webDriver.getCapabilities().getPlatform();
    }

    static BiFunction<WebDriver, String, Optional<WebElement>> elementFor() {
        return (driver, id) -> ofNullable(driver.findElement(id(id)));
    }

    static Consumer<WebDriver> takeScreenShot(String logicalName) {
        return (webDriver) -> {
            // Take screenshot.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES));
                // Write to target/screenshot-[timestamp].jpg
                final FileOutputStream out = new FileOutputStream("target/screenshot-" + logicalName + "-" + LocalDateTime.now() + ".png");
                out.write(outputStream.toByteArray());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    static Consumer<WebDriver> takeScreenShot() {
        return takeScreenShot("");
    }
}
