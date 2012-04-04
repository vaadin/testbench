package com.vaadin.testbench;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;

import com.vaadin.testbench.commands.CanWaitForVaadin;
import com.vaadin.testbench.commands.TestBenchElementCommands;

public class TestBenchElement implements WrapsElement,
        TestBenchElementCommands, CanWaitForVaadin {
    // private static final Logger LOGGER = Logger
    // .getLogger(TestBenchElement.class.getName());

    private WebElement actualElement;
    private final TestBenchDriver tbDriver;

    protected TestBenchElement(WebElement element, TestBenchDriver tbDriver) {
        actualElement = element;
        this.tbDriver = tbDriver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.internal.WrapsElement#getWrappedElement()
     */
    @Override
    public WebElement getWrappedElement() {
        return actualElement;
    }

    @Override
    public void waitForVaadin() {
        tbDriver.waitForVaadin();
    }

}
