package com.vaadin.tests.elements;

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractBrowserTB9Test;

public class ExecuteJavascriptIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @BrowserTest
    public void getProperty() {
        openTestURL();

        TestBenchElement button = $(NativeButtonElement.class).first();
        Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
        Assertions.assertEquals(Long.valueOf(0), offsetTop);
    }
}
