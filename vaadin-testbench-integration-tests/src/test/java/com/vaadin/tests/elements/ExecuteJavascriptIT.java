package com.vaadin.tests.elements;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6TestJUnit5;

public class ExecuteJavascriptIT extends AbstractTB6TestJUnit5 {

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @Test
    public void getProperty() throws Exception {
        openTestURL();

        TestBenchElement button = $(NativeButtonElement.class).first();
        Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
        Assertions.assertEquals(Long.valueOf(0), offsetTop);
    }
}
