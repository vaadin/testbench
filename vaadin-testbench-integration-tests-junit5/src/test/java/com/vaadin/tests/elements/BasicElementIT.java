/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractBrowserTB9Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.TimeoutException;

import java.util.Collections;

public class BasicElementIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @BeforeEach
    public void openAndFindElement() {
        openTestURL();
    }

    @BrowserTest
    public void getSetStringProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertNull(buttonElement.getPropertyString("foo"));
        buttonElement.setProperty("foo", "12");
        Assertions.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12.0, buttonElement.getPropertyDouble("foo"),
                0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @BrowserTest
    public void getSetBooleanProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", true);
        Assertions.assertEquals("true", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(1.0, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @BrowserTest
    public void getSetFalseBooleanProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", false);
        Assertions.assertEquals("false",
                buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(0.0, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertFalse(buttonElement.getPropertyBoolean("foo"));
    }

    @BrowserTest
    public void getSetDoubleProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertNull(buttonElement.getPropertyDouble("foo"));
        buttonElement.setProperty("foo", 12.5);
        Assertions.assertEquals("12.5", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12.5, buttonElement.getPropertyDouble("foo"),
                0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @BrowserTest
    public void getSetIntegerProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertNull(buttonElement.getPropertyInteger("foo"));
        buttonElement.setProperty("foo", 12);
        Assertions.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12, buttonElement.getPropertyInteger("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @BrowserTest
    public void getSetPropertyChain() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);

        Assertions.assertEquals(123L, buttonElement
                .getPropertyDouble("foo", "bar", "baz").longValue());
    }

    @BrowserTest
    public void getSetElementProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertEquals(buttonElement, buttonElement
                .getPropertyElement("parentElement", "firstElementChild"));
        Assertions.assertNull(
                buttonElement.getPropertyElement("firstElementChild"));

    }

    @BrowserTest
    public void getSetElementsProperty() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        Assertions.assertEquals(0,
                buttonElement.getPropertyElements("children").size());
        Assertions.assertEquals(1, buttonElement
                .getPropertyElements("parentElement", "children").size());

    }

    @BrowserTest
    public void getSetPropertyChainMissingValue() {
        TestBenchElement buttonElement = $(NativeButtonElement.class)
                .waitForFirst();
        executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);
        Assertions.assertNull(
                buttonElement.getPropertyDouble("foo", "baz", "baz"));
    }

    @BrowserTest
    public void waitForNonExistent() {
        var templateViewElementElementQuery = $(TemplateViewElement.class);
        Assertions.assertThrows(TimeoutException.class, () -> {
            templateViewElementElementQuery.waitForFirst();
            Assertions.fail(
                    "Should not have found an element which does not exist");
        });
    }

    @BrowserTest
    public void hasAttribute() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        NativeButtonElement withoutAttributes = $(NativeButtonElement.class)
                .get(6);

        Assertions.assertTrue(withAttributes.hasAttribute("string"));
        Assertions.assertTrue(withAttributes.hasAttribute("boolean"));
        Assertions.assertFalse(withAttributes.hasAttribute("nonexistent"));

        Assertions.assertFalse(withoutAttributes.hasAttribute("string"));
        Assertions.assertFalse(withoutAttributes.hasAttribute("boolean"));
        Assertions.assertFalse(withoutAttributes.hasAttribute("nonexistent"));
    }

    @BrowserTest
    public void dispatchEvent() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123");
        Assertions.assertEquals("Event on Button 5 bubbles: false",
                $("div").id("msg").getText());
    }

    @BrowserTest
    public void dispatchEventWithDetails() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123",
                Collections.singletonMap("bubbles", "true"));
        Assertions.assertEquals("Event on Button 5 bubbles: true",
                $("div").id("msg").getText());
    }

    @BrowserTest
    public void nativeButtonDisabled() {
        NativeButtonElement enabled = $(NativeButtonElement.class).get(0);
        NativeButtonElement disabled = $(NativeButtonElement.class).get(2);
        Assertions.assertTrue(enabled.isEnabled());
        Assertions.assertFalse(disabled.isEnabled());
    }
}
