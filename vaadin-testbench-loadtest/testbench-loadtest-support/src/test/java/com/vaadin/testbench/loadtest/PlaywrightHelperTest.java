/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.nio.file.Paths;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.HarMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlaywrightHelperTest {

    private static final String HAR_PROPERTY = "k6.harOutputPath";

    @AfterEach
    void clearSystemProperty() {
        System.clearProperty(HAR_PROPERTY);
    }

    @Test
    void createBrowserContext_withOptions_passesUserOptionsWhenNoHarProperty() {
        Browser browser = Mockito.mock(Browser.class);
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Browser.NewContextOptions userOptions = new Browser.NewContextOptions()
                .setUserAgent("custom-agent");
        Mockito.when(browser
                .newContext(Mockito.any(Browser.NewContextOptions.class)))
                .thenReturn(context);

        BrowserContext result = PlaywrightHelper.createBrowserContext(browser,
                userOptions);

        assertSame(context, result);
        ArgumentCaptor<Browser.NewContextOptions> captor = ArgumentCaptor
                .forClass(Browser.NewContextOptions.class);
        Mockito.verify(browser).newContext(captor.capture());
        Browser.NewContextOptions captured = captor.getValue();
        assertSame(userOptions, captured);
        assertEquals("custom-agent", captured.userAgent);
        assertNull(captured.recordHarPath);
        assertNull(captured.recordHarMode);
    }

    @Test
    void createBrowserContext_withOptions_appliesHarRecordingWhenPropertySet() {
        String harPath = "build/test-recording.har";
        System.setProperty(HAR_PROPERTY, harPath);
        Browser browser = Mockito.mock(Browser.class);
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Browser.NewContextOptions userOptions = new Browser.NewContextOptions()
                .setUserAgent("custom-agent");
        Mockito.when(browser
                .newContext(Mockito.any(Browser.NewContextOptions.class)))
                .thenReturn(context);

        BrowserContext result = PlaywrightHelper.createBrowserContext(browser,
                userOptions);

        assertSame(context, result);
        ArgumentCaptor<Browser.NewContextOptions> captor = ArgumentCaptor
                .forClass(Browser.NewContextOptions.class);
        Mockito.verify(browser).newContext(captor.capture());
        Browser.NewContextOptions captured = captor.getValue();
        assertSame(userOptions, captured);
        assertEquals("custom-agent", captured.userAgent);
        assertEquals(Paths.get(harPath), captured.recordHarPath);
        assertEquals(HarMode.FULL, captured.recordHarMode);
    }

    @Test
    void createBrowserContext_withOptions_ignoresEmptyHarProperty() {
        System.setProperty(HAR_PROPERTY, "");
        Browser browser = Mockito.mock(Browser.class);
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Browser.NewContextOptions userOptions = new Browser.NewContextOptions();
        Mockito.when(browser
                .newContext(Mockito.any(Browser.NewContextOptions.class)))
                .thenReturn(context);

        PlaywrightHelper.createBrowserContext(browser, userOptions);

        ArgumentCaptor<Browser.NewContextOptions> captor = ArgumentCaptor
                .forClass(Browser.NewContextOptions.class);
        Mockito.verify(browser).newContext(captor.capture());
        Browser.NewContextOptions captured = captor.getValue();
        assertSame(userOptions, captured);
        assertNull(captured.recordHarPath);
        assertNull(captured.recordHarMode);
    }

    @Test
    void createBrowserContext_usesDefaultContextWhenNoHarProperty() {
        Browser browser = Mockito.mock(Browser.class);
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Mockito.when(browser.newContext()).thenReturn(context);

        BrowserContext result = PlaywrightHelper.createBrowserContext(browser);

        assertSame(context, result);
        Mockito.verify(browser).newContext();
        Mockito.verify(browser, Mockito.never())
                .newContext(Mockito.any(Browser.NewContextOptions.class));
    }

    @Test
    void createBrowserContext_appliesHarRecordingWhenPropertySet() {
        String harPath = "build/test-recording.har";
        System.setProperty(HAR_PROPERTY, harPath);
        Browser browser = Mockito.mock(Browser.class);
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Mockito.when(browser
                .newContext(Mockito.any(Browser.NewContextOptions.class)))
                .thenReturn(context);

        BrowserContext result = PlaywrightHelper.createBrowserContext(browser);

        assertSame(context, result);
        ArgumentCaptor<Browser.NewContextOptions> captor = ArgumentCaptor
                .forClass(Browser.NewContextOptions.class);
        Mockito.verify(browser).newContext(captor.capture());
        Browser.NewContextOptions captured = captor.getValue();
        assertEquals(Paths.get(harPath), captured.recordHarPath);
        assertEquals(HarMode.FULL, captured.recordHarMode);
        Mockito.verify(browser, Mockito.never()).newContext();
    }

    @Test
    void createBrowserContext_usesDefaultContextWhenHarPropertyEmpty() {
        System.setProperty(HAR_PROPERTY, "");
        Browser browser = Mockito.mock(Browser.class);
        BrowserContext context = Mockito.mock(BrowserContext.class);
        Mockito.when(browser.newContext()).thenReturn(context);

        BrowserContext result = PlaywrightHelper.createBrowserContext(browser);

        assertSame(context, result);
        Mockito.verify(browser).newContext();
        Mockito.verify(browser, Mockito.never())
                .newContext(Mockito.any(Browser.NewContextOptions.class));
    }
}
