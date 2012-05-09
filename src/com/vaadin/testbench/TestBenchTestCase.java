package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.commands.TestBenchCommands;

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
}
