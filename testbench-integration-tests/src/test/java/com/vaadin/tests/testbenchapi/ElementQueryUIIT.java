package com.vaadin.tests.testbenchapi;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;

public class ElementQueryUIIT extends MultiBrowserTest {

    @Test
    public void firstGetAllLast() {
        openTestURL();
        Assert.assertEquals("Button 0",
                $(ButtonElement.class).first().getCaption());
        Assert.assertEquals("Button 9",
                $(ButtonElement.class).last().getCaption());

        List<ButtonElement> all = $(ButtonElement.class).all();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("Button " + i, all.get(i).getCaption());
            Assert.assertEquals("Button " + i,
                    $(ButtonElement.class).get(i).getCaption());
        }
    }
}
