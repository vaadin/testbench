/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * The {@link BrowserDriver} on every backend: each call waits for Vaadin where
 * the driver's contract says so, then goes to the executor's
 * {@link BrowserBackend}.
 */
final class TestBenchDriver implements BrowserDriver {

    private final TestBenchCommandExecutor commandExecutor;

    TestBenchDriver(TestBenchCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    private BrowserBackend backend() {
        return commandExecutor.getBackend();
    }

    private void waitForVaadin() {
        commandExecutor.waitForVaadin();
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    @Override
    public void get(String url) {
        backend().navigate(url);
    }

    @Override
    public String getCurrentUrl() {
        waitForVaadin();
        return backend().getCurrentUrl();
    }

    @Override
    public String getTitle() {
        waitForVaadin();
        return backend().getTitle();
    }

    @Override
    public String getPageSource() {
        waitForVaadin();
        return backend().getPageSource();
    }

    @Override
    public Navigation navigate() {
        return new Navigation() {
            @Override
            public void back() {
                backend().back();
            }

            @Override
            public void forward() {
                backend().forward();
            }

            @Override
            public void to(String url) {
                backend().navigate(url);
            }

            @Override
            public void to(URL url) {
                backend().navigate(url.toString());
            }

            @Override
            public void refresh() {
                backend().refresh();
            }
        };
    }

    @Override
    public Options manage() {
        return () -> new Window() {
            @Override
            public Dimension getSize() {
                return backend().getWindowSize();
            }

            @Override
            public void setSize(Dimension size) {
                backend().setWindowSize(size);
            }
        };
    }

    @Override
    public @Nullable Object executeScript(String script,
            @Nullable Object... args) {
        return commandExecutor.executeScript(script, args);
    }

    @Override
    public @Nullable Object executeAsyncScript(String script,
            @Nullable Object... args) {
        return commandExecutor.executeAsyncScript(script, args);
    }

    @Override
    @SuppressWarnings({ "deprecation", "unchecked" })
    public List<TestBenchElement> findElements(By by) {
        return (List<TestBenchElement>) commandExecutor.executeScript(
                "return Array.from(document.querySelectorAll(arguments[0]))",
                by.toCssSelector());
    }

    @Override
    public TestBenchElement findElement(By by) {
        List<TestBenchElement> found = findElements(by);
        if (found.isEmpty()) {
            throw new NoSuchElementException(
                    "Unable to locate an element with " + by);
        }
        return found.get(0);
    }

    @Override
    public String getWindowHandle() {
        waitForVaadin();
        return backend().getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        waitForVaadin();
        return backend().getWindowHandles();
    }

    @Override
    public TargetLocator switchTo() {
        return new TargetLocator() {
            @Override
            public BrowserDriver window(String handle) {
                backend().switchToWindow(handle);
                return TestBenchDriver.this;
            }

            @Override
            public TestBenchElement activeElement() {
                return (TestBenchElement) commandExecutor
                        .executeScript("return document.activeElement");
            }
        };
    }

    @Override
    public void close() {
        backend().close();
    }

    @Override
    public void quit() {
        backend().quit();
    }
}
