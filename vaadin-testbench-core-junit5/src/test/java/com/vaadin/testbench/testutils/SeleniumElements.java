/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.testutils;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.backend.selenium.SeleniumBackend;
import com.vaadin.testbench.backend.selenium.SeleniumElementBackend;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/** A Selenium element as a {@link TestBenchElement}, for unit tests. */
public final class SeleniumElements {

    private SeleniumElements() {
    }

    /**
     * @param executor
     *            {@code null}, or one on a {@link SeleniumBackend} or a mock
     */
    public static TestBenchElement wrap(WebElement element,
            TestBenchCommandExecutor executor) {
        SeleniumBackend backend = executor == null ? null
                : (SeleniumBackend) executor.getBackend();
        return TestBench.createElement(TestBenchElement.class,
                new SeleniumElementBackend(backend, element), executor);
    }
}
