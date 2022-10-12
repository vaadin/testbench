package com.vaadin.tests.elements;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6TestJUnit5;

public class BasicElementIT extends AbstractTB6TestJUnit5 {

    private TestBenchElement buttonElement;

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @BeforeEach
    public void openAndFindElement() {
        openTestURL();
        buttonElement = $(NativeButtonElement.class).waitForFirst();
    }

    @Test
    public void getSetStringProperty() {
        Assertions.assertNull(buttonElement.getPropertyString("foo"));
        buttonElement.setProperty("foo", "12");
        Assertions.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12.0, buttonElement.getPropertyDouble("foo"),
                0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetBooleanProperty() {
        Assertions.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", true);
        Assertions.assertEquals("true", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(1.0, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetFalseBooleanProperty() {
        Assertions.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", false);
        Assertions.assertEquals("false",
                buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(0.0, buttonElement.getPropertyDouble("foo"), 0);
        Assertions.assertFalse(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetDoubleProperty() {
        Assertions.assertNull(buttonElement.getPropertyDouble("foo"));
        buttonElement.setProperty("foo", 12.5);
        Assertions.assertEquals("12.5", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12.5, buttonElement.getPropertyDouble("foo"),
                0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetIntegerProperty() {
        Assertions.assertNull(buttonElement.getPropertyInteger("foo"));
        buttonElement.setProperty("foo", 12);
        Assertions.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assertions.assertEquals(12, buttonElement.getPropertyInteger("foo"), 0);
        Assertions.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetPropertyChain() {
        executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);

        Assertions.assertEquals(123L, buttonElement
                .getPropertyDouble("foo", "bar", "baz").longValue());
    }

    @Test
    public void getSetElementProperty() {
        Assertions.assertEquals(buttonElement, buttonElement
                .getPropertyElement("parentElement", "firstElementChild"));
        Assertions.assertNull(
                buttonElement.getPropertyElement("firstElementChild"));

    }

    @Test
    public void getSetElementsProperty() {
        Assertions.assertEquals(0,
                buttonElement.getPropertyElements("children").size());
        Assertions.assertEquals(1, buttonElement
                .getPropertyElements("parentElement", "children").size());

    }

    @Test
    public void getSetPropertyChainMissingValue() {
        executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);
        Assertions.assertNull(
                buttonElement.getPropertyDouble("foo", "baz", "baz"));
    }

    @Test
    public void waitForNonExistant() {
        Assertions.assertThrows(TimeoutException.class, () -> {
            $(TemplateViewElement.class).waitForFirst();
            Assertions.fail(
                    "Should not have found an element which does not exist");
        });
    }

    @Test
    public void hasAttribute() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        NativeButtonElement withoutAttributes = $(NativeButtonElement.class)
                .get(6);

        Assertions.assertTrue(withAttributes.hasAttribute("string"));
        Assertions.assertTrue(withAttributes.hasAttribute("boolean"));
        Assertions.assertFalse(withAttributes.hasAttribute("nonexistant"));

        Assertions.assertFalse(withoutAttributes.hasAttribute("string"));
        Assertions.assertFalse(withoutAttributes.hasAttribute("boolean"));
        Assertions.assertFalse(withoutAttributes.hasAttribute("nonexistant"));
    }

    @Test
    public void dispatchEvent() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123");
        Assertions.assertEquals("Event on Button 5 bubbles: false",
                $("div").id("msg").getText());
    }

    @Test
    public void dispatchEventWithDetails() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123",
                Collections.singletonMap("bubbles", "true"));
        Assertions.assertEquals("Event on Button 5 bubbles: true",
                $("div").id("msg").getText());
    }

    @Test
    public void nativeButtonDisabled() {
        NativeButtonElement enabled = $(NativeButtonElement.class).get(0);
        NativeButtonElement disabled = $(NativeButtonElement.class).get(2);
        Assertions.assertTrue(enabled.isEnabled());
        Assertions.assertFalse(disabled.isEnabled());
    }
}
