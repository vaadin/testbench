package com.vaadin.tests.testbenchapi.components.nativeselect;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class NativeSelectSetValueTest extends MultiBrowserTest {

    @Test
    public void testSetValue() {
        openTestURL();
        NativeSelectElement select = $(NativeSelectElement.class).get(0);
        LabelElement counter = $(LabelElement.class).id("counter");
        select.setValue("item 2");
        // checks value has changed
        Assert.assertEquals("item 2", select.getValue());
        // checks change value event occures
        Assert.assertEquals("1", counter.getText());
    }

    // Exclude Phantom js for that test because of #14516
    @BrowserConfiguration
    public static List<DesiredCapabilities> getBrowserConfiguration() {
        List<DesiredCapabilities> browsers = new ArrayList<DesiredCapabilities>(
                getAllBrowsers());
        browsers.remove(BrowserUtil.phantomJS());
        return browsers;
    }
}
