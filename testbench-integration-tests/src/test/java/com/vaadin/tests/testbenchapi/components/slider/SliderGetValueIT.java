package com.vaadin.tests.testbenchapi.components.slider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testUI.ComponentElementGetValue;
import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class SliderGetValueIT extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ComponentElementGetValue.class;
    }

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void checkSlider() {
        SliderElement pb = $(SliderElement.class).get(0);
        String expected = "" + ComponentElementGetValue.TEST_SLIDER_VALUE;
        String actual = pb.getValue();
        Assert.assertEquals(expected, actual);
    }
}
