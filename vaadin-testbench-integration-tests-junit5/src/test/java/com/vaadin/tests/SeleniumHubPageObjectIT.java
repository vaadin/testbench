/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.openqa.selenium.WebDriver;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.parallel.SauceLabsIntegration;
import com.vaadin.testbench.parallel.setup.SetupDriver;

@EnabledIf("isConfiguredForSauceLabs")
public class SeleniumHubPageObjectIT extends AbstractSeleniumSauceTB9Test
        implements DriverSupplier {

    private static Level loggerLevel;
    private final SetupDriver driverConfiguration = new SetupDriver();
    private static final Map<Handler, Level> oldLevels = new HashMap<>();

    @Override
    public WebDriver createDriver() {
        startLog();
        WebDriver driver = null;
        try {
            driver = driverConfiguration
                    .setupRemoteDriver(SauceLabsIntegration.getHubUrl());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            endLog();
        }
        return driver;

    }

    private static void startLog() {
        System.err.println("Logging for SeleniumHubPageObjectIT started");
        Logger logger = Logger.getLogger("");
        loggerLevel = logger.getLevel();
        logger.setLevel(Level.FINE);
        for (Handler h : logger.getHandlers()) {
            oldLevels.put(h, h.getLevel());
            h.setLevel(Level.FINE);
        }
    }

    private static void endLog() {
        System.err.println("Logging for SeleniumHubPageObjectIT ended");
        Logger logger = Logger.getLogger("");
        logger.setLevel(loggerLevel);
        for (Handler h : logger.getHandlers()) {
            h.setLevel(oldLevels.get(h));
        }
    }

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @Test
    public void findUsingValueAnnotation() {
        try {

            openTestURL();
            List<MyComponentWithIdElement> components = $(
                    MyComponentWithIdElement.class).all();

            Assertions.assertEquals(1, components.size());
            Assertions.assertEquals("MyComponentWithId",
                    components.get(0).getText());
        } finally {
            // for (Handler h : logger.getHandlers()) {
            // h.setLevel(oldLevels.get(h));
            // }
        }
    }

    @Test
    public void findUsingContainsAnnotation() {
        openTestURL();
        List<MyComponentWithClassesElement> components = $(
                MyComponentWithClassesElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithClasses",
                components.get(0).getText());
    }

}
