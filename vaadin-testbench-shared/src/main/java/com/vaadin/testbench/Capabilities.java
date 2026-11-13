/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * The browser a test runs on, or asks for, as W3C WebDriver capabilities:
 *
 * <pre>{@code
 * Capabilities chrome = Browser.CHROME.getDesiredCapabilities();
 * chrome.setCapability(Capabilities.BROWSER_VERSION, "120");
 * BrowserUtil.isChrome(getCapabilities());
 * }</pre>
 *
 * The getters answer as Selenium's {@code Capabilities} do, so a test reading
 * them ports by changing its import. Each backend turns these into its own
 * library's options; a capability it does not know is passed on or ignored.
 */
public final class Capabilities {

    public static final String BROWSER_NAME = "browserName";
    public static final String BROWSER_VERSION = "browserVersion";
    public static final String PLATFORM_NAME = "platformName";

    private final Map<String, Object> capabilities = new LinkedHashMap<>();

    public Capabilities() {
    }

    /** A copy of the given capabilities; {@code null} values are left out. */
    public Capabilities(Map<String, ?> capabilities) {
        capabilities.forEach(this::setCapability);
    }

    /** A copy of the given capabilities. */
    public Capabilities(Capabilities other) {
        this(other.capabilities);
    }

    /** @return the browser's name, or {@code ""} when none is set */
    public String getBrowserName() {
        return String.valueOf(
                Objects.requireNonNullElse(getCapability(BROWSER_NAME), ""));
    }

    /** @return the browser's version, or {@code ""} when none is set */
    public String getBrowserVersion() {
        return String.valueOf(
                Objects.requireNonNullElse(getCapability(BROWSER_VERSION), ""));
    }

    /**
     * @return the platform, or {@code null} when none is set or it names no
     *         {@link Platform}
     */
    public @Nullable Platform getPlatformName() {
        Object platform = getCapability(PLATFORM_NAME);
        if (platform == null || platform instanceof Platform) {
            return (Platform) platform;
        }
        try {
            return Platform.fromString(String.valueOf(platform));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public @Nullable Object getCapability(String name) {
        return capabilities.get(name);
    }

    /** Sets a capability; {@code null} removes it. */
    public void setCapability(String name, @Nullable Object value) {
        if (value == null) {
            capabilities.remove(name);
        } else {
            capabilities.put(name, value);
        }
    }

    /** @return a read-only view, in the order the capabilities were set */
    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(capabilities);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Capabilities other
                && capabilities.equals(other.capabilities);
    }

    @Override
    public int hashCode() {
        return capabilities.hashCode();
    }

    @Override
    public String toString() {
        return "Capabilities " + capabilities;
    }
}
