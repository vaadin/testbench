/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Parameters;

/**
 * Which backend the JUnit integration starts its browser on, by
 * {@link Parameters#getBackend()}: Playwright unless told otherwise, Selenium
 * when Playwright is not on the classpath and nothing asked for it.
 */
public final class BackendChoice {

    private static final AtomicBoolean FALLBACK_LOGGED = new AtomicBoolean();
    private static final AtomicBoolean SUPPLIER_LOGGED = new AtomicBoolean();

    private BackendChoice() {
    }

    /**
     * Starts the browser on the chosen backend, if that is not Selenium.
     *
     * @param ignoredSupplier
     *            the test's own Selenium driver supplier, if it has one; a
     *            browser started here replaces it, which is logged once
     * @return {@code null} when the integration sets up Selenium as before
     * @throws IllegalStateException
     *             if Playwright is asked for and not on the classpath
     */
    public static @Nullable WebDriver launchBrowser(
            @Nullable Object ignoredSupplier) {
        String chosen = Parameters.getBackend();
        if (Parameters.SELENIUM.equals(chosen)) {
            return null;
        }
        BrowserLauncher launcher = find(Parameters.PLAYWRIGHT);
        if (launcher == null) {
            if (Parameters.PLAYWRIGHT.equals(chosen)) {
                throw new IllegalStateException(
                        "The backend is playwright, but vaadin-testbench-backend-playwright is not on the classpath");
            }
            if (FALLBACK_LOGGED.compareAndSet(false, true)) {
                LoggerFactory.getLogger(BackendChoice.class).info(
                        "vaadin-testbench-backend-playwright is not on the classpath, so the tests run on Selenium."
                                + " Set com.vaadin.testbench.Parameters.backend=selenium to say so");
            }
            return null;
        }
        if (ignoredSupplier != null
                && SUPPLIER_LOGGED.compareAndSet(false, true)) {
            LoggerFactory.getLogger(BackendChoice.class).info(
                    "{} supplies a Selenium driver, which the {} backend does not use."
                            + " Set com.vaadin.testbench.Parameters.backend=selenium to use it",
                    ignoredSupplier.getClass().getName(),
                    launcher.getBackendName());
        }
        return launcher.launch();
    }

    private static @Nullable BrowserLauncher find(String backendName) {
        for (BrowserLauncher launcher : ServiceLoader
                .load(BrowserLauncher.class)) {
            if (launcher.getBackendName().equals(backendName)) {
                return launcher;
            }
        }
        return null;
    }
}
