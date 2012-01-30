package com.vaadin.testbench;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;

import com.vaadin.testbench.commands.TestBenchElementCommands;

public class TestBenchElement<WE extends WebElement> implements WrapsElement,
        TestBenchElementCommands {
    // private static final Logger LOGGER = Logger
    // .getLogger(TestBenchElement.class.getName());

    private WE actualElement;

    protected TestBenchElement(WE element) {
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
