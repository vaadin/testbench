package com.vaadin.tests.testbenchapi.components.optiongroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class OptionGroupSetValueIT extends MultiBrowserTest {
    @Before
    public void init() {
        openTestURL("theme=reindeer");
    }

    @Test
    @Ignore("needs to be fixed for TB5")
    public void testSetValue() {
        OptionGroupElement group = $(OptionGroupElement.class).first();
        String setValue = "item2";
        group.setValue(setValue);
        Assert.assertEquals(setValue, group.getValue());
    }

    @Test
    @Ignore("needs to be actually implemented for TB5")
    public void testSelectByText() {
        testSetValue();
    }

}
