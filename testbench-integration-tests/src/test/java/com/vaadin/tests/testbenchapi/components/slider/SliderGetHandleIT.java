package com.vaadin.tests.testbenchapi.components.slider;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testUI.SliderGetHandle;
import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class SliderGetHandleIT extends MultiBrowserTest {

    @Test
    public void testGetHanlder() {
        openTestURL();
        // get second slider, to check that getHandler get the handler
        // of the correct slider, not of the very first one
        SliderElement sl2 = $(SliderElement.class).get(1);
        WebElement handler = sl2.getHandle();

        // We get the slider handler and move it
        Actions builder = new Actions(driver);
        Action moveHandler = builder.clickAndHold(handler).moveByOffset(10, 0)
                .release().build();
        moveHandler.perform();

        String initial = "" + (int) SliderGetHandle.INITIAL_VALUE;
        String actual = sl2.getValue();
        Assert.assertNotEquals(initial, actual);
    }
}
