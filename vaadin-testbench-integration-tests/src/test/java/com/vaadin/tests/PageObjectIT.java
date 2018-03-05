package com.vaadin.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;

public class PageObjectIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @Test
    public void findUsingValueAnnotation() {
        openTestURL();
        List<MyComponentWithIdElement> components = $(
                MyComponentWithIdElement.class).all();

        Assert.assertEquals(1, components.size());
        Assert.assertEquals("MyComponentWithId", components.get(0).getText());
    }

    @Test
    public void findUsingContainsAnnotation() {
        openTestURL();
        List<MyComponentWithClassesElement> components = $(
                MyComponentWithClassesElement.class).all();

        Assert.assertEquals(1, components.size());
        Assert.assertEquals("MyComponentWithClasses",
                components.get(0).getText());
    }

}
