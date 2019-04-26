package com.vaadin.testbench.tests.ui.element;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.testbench.tests.ui.element.ElementQueryView.ROUTE;

@VaadinTest
class ExecuteJavaScriptIT {

    @VaadinTest(navigateTo = ROUTE)
    void getProperty(GenericTestPageObject po) throws Exception {
        TestBenchElement button = po.$(NativeButtonElement.class).first();
        Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
        Assertions.assertEquals(Long.valueOf(0), offsetTop);
    }
}
