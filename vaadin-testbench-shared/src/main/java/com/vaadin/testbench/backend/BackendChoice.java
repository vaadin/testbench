/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * Which backend a test runs on: the one {@link Parameters#getBackend()} names,
 * else Playwright when {@code vaadin-testbench-backend-playwright} is on the
 * classpath, else Selenium. A driver the test built itself runs on the backend
 * of its library instead (D_browser_setup).
 */
public final class BackendChoice {

    private static final String BACKEND_PROPERTY = "com.vaadin.testbench.Parameters.backend";
    private static final AtomicBoolean FALLBACK_LOGGED = new AtomicBoolean();

    private BackendChoice() {
    }

    /**
     * Starts the browser the setup describes, on the chosen backend.
     *
     * @throws IllegalStateException
     *             if the chosen backend is not on the classpath
     */
    public static BrowserBackend launch(BrowserSetup setup) throws Exception {
        return chosen().launch(setup);
    }

    /**
     * The launcher of the backend tests run on.
     *
     * @throws IllegalStateException
     *             if the backend {@link Parameters#getBackend()} names, or any
     *             backend when it names none, is not on the classpath
     */
    public static BrowserLauncher chosen() {
        String asked = Parameters.getBackend();
        if (asked != null) {
            BrowserLauncher launcher = find(asked);
            if (launcher == null) {
                throw new IllegalStateException("The backend is " + asked
                        + ", but vaadin-testbench-backend-" + asked
                        + " is not on the classpath");
            }
            return launcher;
        }
        BrowserLauncher playwright = find(Parameters.PLAYWRIGHT);
        if (playwright != null) {
            return playwright;
        }
        BrowserLauncher selenium = find(Parameters.SELENIUM);
        if (selenium == null) {
            throw new IllegalStateException(
                    "No TestBench backend is on the classpath: add vaadin-testbench-backend-playwright"
                            + " or vaadin-testbench-backend-selenium");
        }
        if (FALLBACK_LOGGED.compareAndSet(false, true)) {
            LoggerFactory.getLogger(BackendChoice.class).info(
                    "vaadin-testbench-backend-playwright is not on the classpath, so the tests run on Selenium."
                            + " Set {}=selenium to say so",
                    BACKEND_PROPERTY);
        }
        return selenium;
    }

    /**
     * The backend over a driver the test built, of whichever library's backend
     * is on the classpath.
     *
     * @throws IllegalArgumentException
     *             if no backend on the classpath takes the driver
     * @throws IllegalStateException
     *             if {@link Parameters#getBackend()} names another backend
     */
    public static BrowserBackend wrap(Object driver) {
        for (BrowserLauncher launcher : Launchers.ALL) {
            BrowserBackend backend = launcher.wrap(driver);
            if (backend == null) {
                continue;
            }
            String asked = Parameters.getBackend();
            if (asked != null && !asked.equals(launcher.getBackendName())) {
                throw new IllegalStateException(driver.getClass().getName()
                        + " runs on the " + launcher.getBackendName()
                        + " backend, but " + BACKEND_PROPERTY + " is " + asked
                        + ": unset the property, or build a " + asked
                        + " driver");
            }
            return backend;
        }
        throw new IllegalArgumentException(driver.getClass().getName()
                + " is no driver a backend on the classpath takes. "
                + describeAll(BrowserLauncher::describeDrivers));
    }

    /**
     * Our capabilities from ours or a backend library's.
     *
     * @return {@code capabilities} itself when they are ours, else a copy
     * @throws IllegalArgumentException
     *             if no backend on the classpath knows their type
     */
    public static Capabilities toCapabilities(Object capabilities) {
        if (capabilities instanceof Capabilities ours) {
            return ours;
        }
        for (BrowserLauncher launcher : Launchers.ALL) {
            Capabilities converted = launcher.toCapabilities(capabilities);
            if (converted != null) {
                return converted;
            }
        }
        throw new IllegalArgumentException(capabilities.getClass().getName()
                + " are no capabilities a backend on the classpath knows");
    }

    private static String describeAll(
            java.util.function.Function<BrowserLauncher, String> describe) {
        if (Launchers.ALL.isEmpty()) {
            return "No TestBench backend is on the classpath.";
        }
        return Launchers.ALL.stream()
                .map(l -> "The " + l.getBackendName() + " backend takes "
                        + describe.apply(l) + ".")
                .collect(Collectors.joining(" "));
    }

    private static @Nullable BrowserLauncher find(String backendName) {
        for (BrowserLauncher launcher : Launchers.ALL) {
            if (launcher.getBackendName().equals(backendName)) {
                return launcher;
            }
        }
        return null;
    }

    // Loaded once, on first use
    private static final class Launchers {
        static final List<BrowserLauncher> ALL = load();

        private static List<BrowserLauncher> load() {
            List<BrowserLauncher> launchers = new ArrayList<>();
            ServiceLoader
                    .load(BrowserLauncher.class,
                            BackendChoice.class.getClassLoader())
                    .forEach(launchers::add);
            return List.copyOf(launchers);
        }
    }
}
