/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.util.concurrent.ConcurrentHashMap;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;

import com.vaadin.flow.component.UI;

/**
 * Base class for UI unit tests.
 * 
 * Provides methods to set up and clean a mocked Vaadin environment.
 *
 * Subclasses should typically restrict classpath scanning to a specific package
 * for faster bootstrap, by overriding {@link #scanPackage()} method.
 *
 * For internal use only. May be renamed or removed in a future release.
 */
class BaseUIUnitTest {

    private static final ConcurrentHashMap<String, Routes> routesCache = new ConcurrentHashMap<>();

    private static synchronized Routes discoverRoutes(String packageName) {
        packageName = packageName == null ? "" : packageName;
        return routesCache.computeIfAbsent(packageName,
                pn -> new Routes().autoDiscoverViews(pn));
    }

    protected void initVaadinEnvironment() {
        MockVaadin.setup(discoverRoutes(scanPackage()), UI::new);
    }

    protected void cleanVaadinEnvironment() {
        MockVaadin.tearDown();
    }

    /**
     * Gets the name of the package that should be used as root to scan for
     * routes and error views.
     *
     * Provide {@literal null} or empty string to scan the whole classpath, but
     * note that this may be quite slow.
     *
     * @return package name for classpath scanning.
     */
    protected String scanPackage() {
        return null;
    }
}
