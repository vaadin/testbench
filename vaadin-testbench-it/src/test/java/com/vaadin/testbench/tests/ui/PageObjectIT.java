package com.vaadin.testbench.tests.ui;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.annotations.Attribute;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.testbench.tests.ui.PageObjectView.ROUTE;

@VaadinTest(navigateTo = ROUTE)
class PageObjectIT {

    @VaadinTest
    void findUsingValueAnnotation(VaadinPageObject po) {
        List<MyComponentWithIdElement> components = po.$(
                MyComponentWithIdElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithId", components.get(0).getText());
    }

    @VaadinTest
    void findUsingContainsAnnotation(VaadinPageObject po) {
        List<MyComponentWithClassesElement> components = po.$(
                MyComponentWithClassesElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithClasses",
                components.get(0).getText());
    }

    @Attribute(name = "id", value = Attribute.SIMPLE_CLASS_NAME)
    static class MyComponentWithIdElement extends TestBenchElement {

    }

    @Attribute(name = "class", contains = Attribute.SIMPLE_CLASS_NAME)
    @Attribute(name = "class", contains = "my-component-first")
    static class MyComponentWithClassesElement extends TestBenchElement {

    }
}
