package com.vaadin.tests.testbenchapi.components.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testUI.WindowUI;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class WindowButtonsIT extends MultiBrowserTest {

    private WindowElement windowElement;

    @Override
    protected Class<?> getUIClass() {
        return WindowUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        windowElement = $(WindowElement.class).first();
    }

    @Test
    public void window_clickCloseButton_windowClosed() {
        windowElement.close();

        assertFalse($(WindowElement.class).exists());
    }

    @Test
    public void window_maximizeAndRestore_windowOriginalSize()
            throws IOException, InterruptedException {
        assertFalse(windowElement.isMaximized());
        final Dimension originalSize = windowElement.getSize();

        windowElement.maximize();

        assertTrue(windowElement.isMaximized());
        assertNotEquals(originalSize, windowElement.getSize());

        windowElement.restore();

        assertFalse(windowElement.isMaximized());
        assertEquals(originalSize, windowElement.getSize());
    }

    @Override
    public List<DesiredCapabilities> getBrowserConfiguration() {
        // window tests mostly work in IE8, but it is removed here
        // to make tests run reliably
        List<DesiredCapabilities> browsersWithoutIe8 = new LinkedList(
                getAllBrowsers());
        browsersWithoutIe8.remove(BrowserUtil.ie8());
        return browsersWithoutIe8;
    }
}
