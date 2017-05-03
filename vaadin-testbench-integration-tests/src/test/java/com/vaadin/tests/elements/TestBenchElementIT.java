package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.ElementQueryUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class TestBenchElementIT extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ElementQueryUI.class;
    }

    @Test
    public void getSetStringProperty() {
        openTestURL();

        TestBenchElement buttonElement = (TestBenchElement) findElements(
                By.className("v-button")).get(0);

        Assert.assertNull(buttonElement.getPropertyString("foo"));
        buttonElement.setProperty("foo", "12");
        Assert.assertEquals("12", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(12.0, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetBooleanProperty() {
        openTestURL();

        TestBenchElement buttonElement = (TestBenchElement) findElements(
                By.className("v-button")).get(0);

        Assert.assertNull(buttonElement.getPropertyBoolean("foo"));
        buttonElement.setProperty("foo", true);
        Assert.assertEquals("true", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(1.0, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test
    public void getSetDoubleProperty() {
        openTestURL();

        TestBenchElement buttonElement = (TestBenchElement) findElements(
                By.className("v-button")).get(0);

        Assert.assertNull(buttonElement.getPropertyDouble("foo"));
        buttonElement.setProperty("foo", 12.5);
        Assert.assertEquals("12.5", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(12.5, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

}
