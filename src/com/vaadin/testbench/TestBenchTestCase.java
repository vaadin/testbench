package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.commands.TestBenchCommands;

public class TestBenchTestCase {

    public TestBenchCommands testBench(WebDriver webDriver) {
        return (TestBenchCommands) webDriver;
    }
}
