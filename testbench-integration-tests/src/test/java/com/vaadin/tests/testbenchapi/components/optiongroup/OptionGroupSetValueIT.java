package com.vaadin.tests.testbenchapi.components.optiongroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class OptionGroupSetValueIT extends MultiBrowserTest {
    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testSetValue() {
        OptionGroupElement group = $(OptionGroupElement.class).first();
        String setValue = "item2";
        group.setValue(setValue);
        Assert.assertEquals(setValue, group.getValue());
    }

    @Test
    public void testSelectByText() {
        testSetValue();
    }

}
