package com.vaadin.testbench;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;

import com.vaadin.testbench.commands.TestBenchElementCommands;

public class TestBenchElement implements WrapsElement, TestBenchElementCommands {
    // private static final Logger LOGGER = Logger
    // .getLogger(TestBenchElement.class.getName());

    private WebElement actualElement;

    protected TestBenchElement(WebElement element) {
        actualElement = element;
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

}
