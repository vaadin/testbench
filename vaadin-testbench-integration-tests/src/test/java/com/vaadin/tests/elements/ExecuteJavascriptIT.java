package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.ui.Component;

public class ExecuteJavascriptIT extends MultiBrowserTest {

    @Override
    protected Class<? extends Component> getTestView() {
        return ElementQueryView.class;
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();

        TestBenchElement button = $(NativeButtonElement.class).first();
        Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
        Assert.assertEquals(Long.valueOf(0), offsetTop);
    }
}
