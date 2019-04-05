package com.vaadin.testbench.addons.junit5.extensions.unitest;

import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.openqa.selenium.WebDriver;

public interface WebDriverTemplateInvocationContext extends TestTemplateInvocationContext {
    WebDriver webdriver();
}
