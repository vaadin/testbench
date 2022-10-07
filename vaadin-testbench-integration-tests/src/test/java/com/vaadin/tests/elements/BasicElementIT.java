package com.vaadin.tests.elements;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.TimeoutException;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6Test;

public class BasicElementIT extends AbstractTB6Test {

    private TestBenchElement buttonElement;

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @Before
    public void openAndFindElement() {
        openTestURL();
        buttonElement = $(NativeButtonElement.class).waitForFirst();
    }

    @Test
    public void getSetStringProperty() {
        Assert.assertNull(buttonElement.getPropertyString("foo"));
        buttonElement.setProperty("foo", "12");
        Assert.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(12.0, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetBooleanProperty() {
        Assert.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", true);
        Assert.assertEquals("true", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(1.0, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetFalseBooleanProperty() {
        Assert.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", false);
        Assert.assertEquals("false", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(0.0, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertFalse(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetDoubleProperty() {
        Assert.assertNull(buttonElement.getPropertyDouble("foo"));
        buttonElement.setProperty("foo", 12.5);
        Assert.assertEquals("12.5", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(12.5, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetIntegerProperty() {
        Assert.assertNull(buttonElement.getPropertyInteger("foo"));
        buttonElement.setProperty("foo", 12);
        Assert.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(12, buttonElement.getPropertyInteger("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetPropertyChain() {
        executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);

        Assert.assertEquals(123L, buttonElement
                .getPropertyDouble("foo", "bar", "baz").longValue());
    }

    @Test
    public void getSetElementProperty() {
        Assert.assertEquals(buttonElement, buttonElement
                .getPropertyElement("parentElement", "firstElementChild"));
        Assert.assertNull(
                buttonElement.getPropertyElement("firstElementChild"));

    }

    @Test
    public void getSetElementsProperty() {
        Assert.assertEquals(0,
                buttonElement.getPropertyElements("children").size());
        Assert.assertEquals(1, buttonElement
                .getPropertyElements("parentElement", "children").size());

    }

    @Test
    public void getSetPropertyChainMissingValue() {
        executeScript("arguments[0].foo = {bar: {baz: 123}};", buttonElement);
        Assert.assertNull(buttonElement.getPropertyDouble("foo", "baz", "baz"));
    }

    @Test(expected = TimeoutException.class)
    public void waitForNonExistant() {
        $(PolymerTemplateViewElement.class).waitForFirst();
        Assert.fail("Should not have found an element which does not exist");
    }

    @Test
    public void hasAttribute() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        NativeButtonElement withoutAttributes = $(NativeButtonElement.class)
                .get(6);

        Assert.assertTrue(withAttributes.hasAttribute("string"));
        Assert.assertTrue(withAttributes.hasAttribute("boolean"));
        Assert.assertFalse(withAttributes.hasAttribute("nonexistant"));

        Assert.assertFalse(withoutAttributes.hasAttribute("string"));
        Assert.assertFalse(withoutAttributes.hasAttribute("boolean"));
        Assert.assertFalse(withoutAttributes.hasAttribute("nonexistant"));
    }

    @Test
    public void dispatchEvent() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123");
        Assert.assertEquals("Event on Button 5 bubbles: false", $("div").id("msg").getText());
    }

    @Test
    public void dispatchEventWithDetails() {
        NativeButtonElement withAttributes = $(NativeButtonElement.class)
                .get(5);
        withAttributes.dispatchEvent("custom123",
                Collections.singletonMap("bubbles", "true"));
        Assert.assertEquals("Event on Button 5 bubbles: true", $("div").id("msg").getText());
    }

    @Test
    public void nativeButtonDisabled() {
        NativeButtonElement enabled = $(NativeButtonElement.class).get(0);
        NativeButtonElement disabled = $(NativeButtonElement.class).get(2);
        Assert.assertTrue(enabled.isEnabled());
        Assert.assertFalse(disabled.isEnabled());
    }
}
