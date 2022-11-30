/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageFileUtil;

/**
 * Example of how to use SeleniumJupiter together with TestBench 9+ features.
 *
 * @author Vaadin Ltd
 */
@Execution(ExecutionMode.SAME_THREAD)
public abstract class AbstractSeleniumTB9Test extends AbstractTB9Test {

    @RegisterExtension
    static SeleniumJupiter seleniumJupiter = new SeleniumJupiter();

    @BeforeAll
    static void setupSeleniumJupiter() {
        seleniumJupiter.getConfig().enableScreenshotWhenFailure();
        seleniumJupiter.getConfig().takeScreenshotAsPng();
        ImageFileUtil.createScreenshotDirectoriesIfNeeded();
        seleniumJupiter.getConfig()
                .setOutputFolder(Parameters.getScreenshotErrorDirectory());
    }

}
