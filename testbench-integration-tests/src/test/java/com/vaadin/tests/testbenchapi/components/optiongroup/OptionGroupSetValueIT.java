package com.vaadin.tests.testbenchapi.components.optiongroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class OptionGroupSetValueIT extends MultiBrowserTest {

    private static final String NEW_VALUE = "item2";

    private OptionGroupElement group;

    @Before
    public void init() {
        openTestURL();
        group = $(OptionGroupElement.class).first();
    }

    @Test
    public void testSetValue() {
        group.setValue(NEW_VALUE);
        Assert.assertEquals(NEW_VALUE, group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(NEW_VALUE);
        Assert.assertEquals(NEW_VALUE, group.getValue());
    }

}
