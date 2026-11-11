/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.net.URL;
import java.util.List;
import java.util.Set;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.ViewportSize;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.Logs;

import com.vaadin.testbench.backend.HasBackend;
import com.vaadin.testbench.spi.Backend;

import static com.vaadin.testbench.NotYetImplementedException.notYetImplemented;

/**
 * A Playwright page seen as a Selenium {@link WebDriver}, while browser setup
 * and the test-level {@code getDriver()} are still Selenium-typed: navigation,
 * the window size, screenshots and closing. TestBench's driver proxy wraps it
 * and runs scripts and finds through {@link #getBackend()}; anything else
 * throws {@code notYetImplemented}. Temporary, until browser setup has an API
 * of its own.
 */
public class PlaywrightDriver
        implements WebDriver, HasBackend, TakesScreenshot, HasCapabilities {

    private final BrowserContext context;
    private final Page page;
    private final PlaywrightBackend backend;

    PlaywrightDriver(BrowserContext context, Page page) {
        this.context = context;
        this.page = page;
        this.backend = new PlaywrightBackend(page);
    }

    @Override
    public Backend getBackend() {
        return backend;
    }

    /** The page; the escape hatch to Playwright. */
    public Page getPage() {
        return page;
    }

    @Override
    public void get(String url) {
        try {
            page.navigate(url);
        } catch (PlaywrightException e) {
            throw PlaywrightBackend.translate(e);
        }
    }

    @Override
    public String getCurrentUrl() {
        return page.url();
    }

    @Override
    public String getTitle() {
        return page.title();
    }

    @Override
    public String getPageSource() {
        return page.content();
    }

    /** Never called: TestBench's driver proxy finds through the backend. */
    @Override
    public List<WebElement> findElements(By by) {
        throw notYetImplemented("PlaywrightDriver.findElements");
    }

    /** Never called: TestBench's driver proxy finds through the backend. */
    @Override
    public WebElement findElement(By by) {
        throw notYetImplemented("PlaywrightDriver.findElement");
    }

    @Override
    public void close() {
        page.close();
    }

    /** Closes the browser context, which also writes a HAR being recorded. */
    @Override
    public void quit() {
        context.close();
    }

    @Override
    public Set<String> getWindowHandles() {
        throw notYetImplemented("getWindowHandles on Playwright");
    }

    @Override
    public String getWindowHandle() {
        throw notYetImplemented("getWindowHandle on Playwright");
    }

    @Override
    public TargetLocator switchTo() {
        throw notYetImplemented("switchTo on Playwright");
    }

    @Override
    public Navigation navigate() {
        return new Navigation() {
            @Override
            public void back() {
                page.goBack();
            }

            @Override
            public void forward() {
                page.goForward();
            }

            @Override
            public void to(String url) {
                get(url);
            }

            @Override
            public void to(URL url) {
                get(url.toString());
            }

            @Override
            public void refresh() {
                page.reload();
            }
        };
    }

    @Override
    public Options manage() {
        return new Options() {
            @Override
            public void addCookie(Cookie cookie) {
                throw notYetImplemented("cookies on Playwright");
            }

            @Override
            public void deleteCookieNamed(String name) {
                throw notYetImplemented("cookies on Playwright");
            }

            @Override
            public void deleteCookie(Cookie cookie) {
                throw notYetImplemented("cookies on Playwright");
            }

            @Override
            public void deleteAllCookies() {
                context.clearCookies();
            }

            @Override
            public Set<Cookie> getCookies() {
                throw notYetImplemented("cookies on Playwright");
            }

            @Override
            public Cookie getCookieNamed(String name) {
                throw notYetImplemented("cookies on Playwright");
            }

            @Override
            public Timeouts timeouts() {
                throw notYetImplemented("timeouts on Playwright");
            }

            @Override
            public Window window() {
                return new PageWindow();
            }

            @Override
            public Logs logs() {
                throw notYetImplemented("logs on Playwright");
            }
        };
    }

    /** The viewport: a Playwright page has no window around it. */
    private final class PageWindow implements Window {

        @Override
        public Dimension getSize() {
            ViewportSize size = page.viewportSize();
            return new Dimension(size.width, size.height);
        }

        @Override
        public void setSize(Dimension targetSize) {
            page.setViewportSize(targetSize.getWidth(), targetSize.getHeight());
        }

        @Override
        public Point getPosition() {
            return new Point(0, 0);
        }

        @Override
        public void setPosition(Point targetPosition) {
            throw notYetImplemented("window position on Playwright");
        }

        @Override
        public void maximize() {
            throw notYetImplemented("maximize on Playwright");
        }

        @Override
        public void minimize() {
            throw notYetImplemented("minimize on Playwright");
        }

        @Override
        public void fullscreen() {
            throw notYetImplemented("fullscreen on Playwright");
        }
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        return target.convertFromPngBytes(page.screenshot());
    }

    @Override
    public Capabilities getCapabilities() {
        String name = backend.getBrowserName();
        return name == null ? new ImmutableCapabilities()
                : new ImmutableCapabilities("browserName", name);
    }
}
