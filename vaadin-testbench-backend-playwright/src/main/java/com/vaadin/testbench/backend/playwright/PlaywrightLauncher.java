/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HarMode;
import org.jspecify.annotations.Nullable;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.backend.BrowserLauncher;
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * Starts each test's browser as a fresh {@link BrowserContext}, its own cookies
 * and so its own Vaadin session, in a browser shared by the tests of one
 * thread. System properties:
 * <ul>
 * <li>{@code com.vaadin.testbench.playwright.browser}: {@code chromium} (the
 * default), {@code firefox} or {@code webkit};</li>
 * <li>{@code com.vaadin.testbench.playwright.executablePath}: the browser to
 * start, instead of one Playwright downloads; with it set, Playwright downloads
 * nothing;</li>
 * <li>{@code com.vaadin.testbench.playwright.viewport}: {@code 1280x720} (the
 * default) or another {@code <width>x<height>};</li>
 * <li>{@code com.vaadin.testbench.Parameters.headless}: headless unless
 * {@code false};</li>
 * <li>{@code k6.harOutputPath}: records the test's traffic to that HAR file,
 * for loadtest's {@code record-playwright} goal.</li>
 * </ul>
 * Temporary names, until browser setup has an API of its own.
 */
public class PlaywrightLauncher implements BrowserLauncher {

    private static final String PREFIX = "com.vaadin.testbench.playwright.";

    // Playwright's objects belong to the thread that made them
    private static final ThreadLocal<Browser> BROWSERS = new ThreadLocal<>();
    private static final List<Playwright> STARTED = new CopyOnWriteArrayList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Playwright playwright : STARTED) {
                try {
                    playwright.close();
                } catch (RuntimeException e) {
                    LoggerFactory.getLogger(PlaywrightLauncher.class)
                            .debug("Closing Playwright failed", e);
                }
            }
        }, "testbench-playwright-shutdown"));
    }

    @Override
    public String getBackendName() {
        return Parameters.PLAYWRIGHT;
    }

    @Override
    public BrowserBackend launch() {
        Browser.NewContextOptions options = new Browser.NewContextOptions();
        String viewport = System.getProperty(PREFIX + "viewport");
        if (viewport != null) {
            String[] size = viewport.trim().split("x");
            options.setViewportSize(Integer.parseInt(size[0]),
                    Integer.parseInt(size[1]));
        }
        String har = System.getProperty("k6.harOutputPath");
        if (har != null && !har.isEmpty()) {
            options.setRecordHarPath(Path.of(har))
                    .setRecordHarMode(HarMode.FULL);
        }
        BrowserContext context = browser().newContext(options);
        return new PlaywrightBackend(context.newPage());
    }

    private static Browser browser() {
        Browser browser = BROWSERS.get();
        if (browser == null || !browser.isConnected()) {
            browser = startBrowser();
            BROWSERS.set(browser);
        }
        return browser;
    }

    private static Browser startBrowser() {
        @Nullable
        String executable = System.getProperty(PREFIX + "executablePath");
        Playwright.CreateOptions createOptions = new Playwright.CreateOptions();
        if (executable != null) {
            createOptions
                    .setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1"));
        }
        Playwright playwright = Playwright.create(createOptions);
        STARTED.add(playwright);
        BrowserType.LaunchOptions launch = new BrowserType.LaunchOptions()
                .setHeadless(!"false".equalsIgnoreCase(System.getProperty(
                        "com.vaadin.testbench.Parameters.headless")));
        if (executable != null) {
            launch.setExecutablePath(Path.of(executable));
        }
        String name = System.getProperty(PREFIX + "browser", "chromium");
        BrowserType type = switch (name) {
        case "chromium" -> playwright.chromium();
        case "firefox" -> playwright.firefox();
        case "webkit" -> playwright.webkit();
        default -> throw new IllegalArgumentException("'" + name + "' is not a "
                + PREFIX + "browser: use chromium, firefox or webkit");
        };
        return type.launch(launch);
    }
}
