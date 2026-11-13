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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HarMode;
import org.jspecify.annotations.Nullable;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.backend.BrowserLauncher;
import com.vaadin.testbench.backend.BrowserSetup;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * Starts each test's browser as a fresh {@link BrowserContext}, its own cookies
 * and so its own Vaadin session, in a browser shared by the tests of one
 * thread. System properties: The browser is the one the setup's capabilities
 * name, as {@code @RunLocally} or {@code Parameters.runLocally} chose it:
 * Chrome is Chromium, Edge is Chromium's {@code msedge} channel, Firefox is
 * Firefox, Safari is WebKit. A hub, Sauce Labs included, is Selenium's alone. A
 * test can also hand TestBench its own {@link Browser}, {@link BrowserContext}
 * or {@link Page}. System properties:
 * <ul>
 * <li>{@code com.vaadin.testbench.playwright.browser}: {@code chromium},
 * {@code firefox} or {@code webkit}, over what the capabilities name;</li>
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

    // Playwright's objects belong to the thread that made them; one browser
    // per type and channel
    private static final ThreadLocal<Map<String, Browser>> BROWSERS = ThreadLocal
            .withInitial(HashMap::new);
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
    public BrowserBackend launch(BrowserSetup setup) {
        if (setup.hubUrl() != null) {
            throw new UnsupportedOperationException(
                    "Playwright cannot run a browser on a Selenium hub (@RunOnHub,"
                            + " com.vaadin.testbench.Parameters.hubHostname or Sauce Labs);"
                            + " set com.vaadin.testbench.Parameters.backend=selenium");
        }
        return newPage(browser(setup.capabilities()));
    }

    @Override
    public @Nullable BrowserBackend wrap(Object driver) {
        if (driver instanceof Page page) {
            return new PlaywrightBackend(page);
        }
        if (driver instanceof BrowserContext context) {
            return new PlaywrightBackend(context.newPage());
        }
        if (driver instanceof Browser browser) {
            return newPage(browser);
        }
        return null;
    }

    @Override
    public String describeDrivers() {
        return "a Playwright Browser, BrowserContext or Page";
    }

    @Override
    public @Nullable Capabilities toCapabilities(Object capabilities) {
        return null;
    }

    private static BrowserBackend newPage(Browser browser) {
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
        BrowserContext context = browser.newContext(options);
        return new PlaywrightBackend(context.newPage());
    }

    private static Browser browser(Capabilities capabilities) {
        String type = System.getProperty(PREFIX + "browser");
        @Nullable
        String channel = null;
        if (type == null) {
            if (BrowserUtil.isFirefox(capabilities)) {
                type = "firefox";
            } else if (BrowserUtil.isSafari(capabilities)) {
                type = "webkit";
            } else {
                type = "chromium";
                channel = BrowserUtil.isEdge(capabilities) ? "msedge" : null;
            }
        }
        String key = channel == null ? type : type + "/" + channel;
        Browser browser = BROWSERS.get().get(key);
        if (browser == null || !browser.isConnected()) {
            browser = startBrowser(type, channel);
            BROWSERS.get().put(key, browser);
        }
        return browser;
    }

    private static Browser startBrowser(String name, @Nullable String channel) {
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
        } else if (channel != null) {
            launch.setChannel(channel);
        }
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
