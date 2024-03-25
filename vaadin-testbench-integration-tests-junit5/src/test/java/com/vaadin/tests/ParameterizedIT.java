/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ParameterizedView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.ParameterizedBrowserTest;

public class ParameterizedIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ParameterizedView.class;
    }

    @ParameterizedBrowserTest
    @ValueSource(strings = { "first", "second" })
    public void testInvokeWithParameters(String parameter) {
        executeTest(parameter);
    }

    @ParameterizedBrowserTest
    @ValueSource(ints = { 10, 20 })
    public void anotherTestInvokeWithParameters(int parameter) {
        executeTest(Integer.toString(parameter));
    }

    // Just to ensure @ParameterizedBrowserTest and @BrowserTest can coexist in
    // the same class
    @BrowserTest
    public void normalTest() {
        executeTest("FIXED");
    }

    private void executeTest(String parameter) {
        String url = getTestUrl() + "/" + parameter;
        getDriver().get(url);

        WebElement paramHolder = waitUntil(
                d -> d.findElement(By.id("parameter-holder")));

        Assertions.assertEquals("PARAMETER: " + parameter,
                paramHolder.getText());
    }

    @Override
    public List<DesiredCapabilities> getBrowserConfiguration() {
        List<DesiredCapabilities> list = super.getBrowserConfiguration();
        // Base class forces WIN10 as platform
        // This is a temporary hack for testing against local selenium hub on
        // docker
        if (Boolean.getBoolean("test.hub.linux")) {
            list.forEach(c -> c.setPlatform(Platform.LINUX));
        }
        return list;
    }
}
