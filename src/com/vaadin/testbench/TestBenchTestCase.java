package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;

public class TestBenchTestCase {

    /**
     * Convenience method that casts the specified {@link WebDriver} instance to
     * an instance of {@link TestBenchCommands}, making it easy to access the
     * special TestBench commands.
     * 
     * @param webDriver
     * @return
     */
    public TestBenchCommands testBench(WebDriver webDriver) {
        return (TestBenchCommands) webDriver;
    }

    /**
     * Convenience method that casts the specified {@link WebElement} instance
     * to an instance of {@link TestBenchElementCommands}, making it easy to
     * access the special TestBench commands.
     * 
     * @param webElement
     * @return
     */
    public TestBenchElementCommands tbElement(WebElement webElement) {
        return (TestBenchElementCommands) webElement;
    }
}
