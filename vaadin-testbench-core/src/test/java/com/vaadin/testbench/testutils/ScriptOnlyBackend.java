/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.testutils;

import java.util.List;
import java.util.Set;

import com.vaadin.testbench.Dimension;
import com.vaadin.testbench.spi.BrowserBackend;
import com.vaadin.testbench.spi.Input;

/**
 * A fake {@link BrowserBackend} for tests that only run scripts: a subclass
 * answers {@code executeScript}, and every page-level call throws.
 */
public abstract class ScriptOnlyBackend implements BrowserBackend {

    private static UnsupportedOperationException notFaked() {
        return new UnsupportedOperationException("Not in this fake backend");
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        throw notFaked();
    }

    @Override
    public String getBrowserName() {
        return "chrome";
    }

    @Override
    public void navigate(String url) {
        throw notFaked();
    }

    @Override
    public void back() {
        throw notFaked();
    }

    @Override
    public void forward() {
        throw notFaked();
    }

    @Override
    public void refresh() {
        throw notFaked();
    }

    @Override
    public String getCurrentUrl() {
        throw notFaked();
    }

    @Override
    public String getTitle() {
        throw notFaked();
    }

    @Override
    public String getPageSource() {
        throw notFaked();
    }

    @Override
    public Dimension getWindowSize() {
        throw notFaked();
    }

    @Override
    public void setWindowSize(Dimension size) {
        throw notFaked();
    }

    @Override
    public byte[] getScreenshot() {
        throw notFaked();
    }

    @Override
    public String getWindowHandle() {
        throw notFaked();
    }

    @Override
    public Set<String> getWindowHandles() {
        throw notFaked();
    }

    @Override
    public void switchToWindow(String handle) {
        throw notFaked();
    }

    @Override
    public void close() {
        throw notFaked();
    }

    @Override
    public void quit() {
        throw notFaked();
    }

    @Override
    public void perform(List<Input> steps) {
        throw notFaked();
    }
}
