package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.TimeoutException;

import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6Test;
import com.vaadin.ui.Component;

public class BasicElementIT extends AbstractTB6Test {

    private TestBenchElement buttonElement;

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @Before
    public void openAndFindElement() {
        openTestURL();
        buttonElement = $(NativeButtonElement.class).first();
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
    public void getSetDoubleProperty() {
        Assert.assertNull(buttonElement.getPropertyDouble("foo"));
        buttonElement.setProperty("foo", 12.5);
        Assert.assertEquals("12.5", buttonElement.getPropertyString("foo"));
        Assert.assertEquals(12.5, buttonElement.getPropertyDouble("foo"), 0);
        Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
    }

    @Test(expected = TimeoutException.class)
    public void waitForNonExistant() {
        $(PolymerTemplateViewElement.class).waitForFirst();
        Assert.fail("Should not have found an element which does not exist");
    }
}
