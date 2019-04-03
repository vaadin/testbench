package com.github.webdriverextensions;

import com.github.webdriverextensions.internal.Openable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

public abstract class WebSite implements Openable {

    public void initElements(WebDriver driver) {
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
    }

    public void initElements(FieldDecorator decorator) {
        PageFactory.initElements(decorator, this);
    }

    @Override
    public abstract void open(Object... arguments);

    @Override
    public boolean isOpen(Object... arguments) {
        try {
            assertIsOpen(arguments);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    public boolean isNotOpen(Object... arguments) {
        return !isOpen(arguments);
    }

    @Override
    public abstract void assertIsOpen(Object... arguments) throws AssertionError;

    @Override
    public void assertIsNotOpen(Object... arguments) throws AssertionError {
        if (isNotOpen(arguments)) {
            throw new AssertionError(this.getClass().getSimpleName() + " is open when it shouldn't");
        }
    }
}
