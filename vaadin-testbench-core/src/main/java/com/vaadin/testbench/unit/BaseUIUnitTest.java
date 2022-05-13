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

import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.googlecode.gentyref.GenericTypeReflector;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouteParameters;

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

    /**
     * Navigate to the given view class if it is registered.
     *
     * @param navigationTarget
     *         view class to navigate to
     * @param <T>
     *         view type
     * @return instantiated view
     */
    public <T extends Component> T navigate(Class<T> navigationTarget) {
        UI.getCurrent().navigate(navigationTarget);
        return navigationTarget.cast(getCurrentView());
    }

    /**
     * Navigate to view with url parameter.
     *
     * @param navigationTarget
     *         view class to navigate to
     * @param parameter
     *         parameter to send to view
     * @param <T>
     *         view type
     * @param <C>
     *         parameter type
     * @return instantiated view
     */
    public <C, T extends Component & HasUrlParameter<C>> T navigate(
            Class<T> navigationTarget, C parameter) {
        UI.getCurrent().navigate(navigationTarget, parameter);
        return navigationTarget.cast(getCurrentView());
    }

    /**
     * Navigate to view corresponding to the given navigation target with the
     * specified parameters.
     *
     * @param navigationTarget
     *         view class to navigate to
     * @param parameters
     *         parameters to pass to view.
     * @param <T>
     *         view type
     * @return instantiated view
     */
    public <T extends Component> T navigate(Class<T> navigationTarget,
            Map<String, String> parameters) {
        UI.getCurrent()
                .navigate(navigationTarget, new RouteParameters(parameters));
        return navigationTarget.cast(getCurrentView());
    }

    /**
     * Navigate to given location string.
     * Check that location navigated to is the expected view or throw exception.
     *
     * @param location
     *         location string for navigating
     * @param expectedTarget
     *         class that is expected for navigation
     * @param <T>
     *         view type
     * @return instantiated view
     */
    public <T extends Component> T navigate(String location,
            Class<T> expectedTarget) {
        UI.getCurrent().navigate(location);
        final HasElement currentView = getCurrentView();
        if (!expectedTarget.equals(currentView.getClass())) {
            throw new IllegalArgumentException(
                    "Navigation resulted in unexpected class "
                            + currentView.getClass().getName() + " instead of "
                            + expectedTarget.getName());
        }
        return expectedTarget.cast(currentView);
    }

    /**
     * Get the current view instance that is shown on the ui.
     *
     * @return current view
     */
    public HasElement getCurrentView() {
        return UI.getCurrent().getInternals().getActiveRouterTargetsChain()
                .get(0);
    }

    /**
     * Wrap component with wrapper best matching component type.
     *
     * @param component
     *         component to get test wrapper for
     * @param <T>
     *         wrapper type
     * @param <Y>
     *         component type
     * @return component in wrapper with test helpers
     */
    public <T extends ComponentWrap<Y>, Y extends Component> T $(Y component) {
        // TODO: add handler resolution by component
        return (T) initialize(ComponentWrap.class, component);
    }

    /**
     * Private initializer for wrapper classes.
     *
     * @param clazz
     *         wrapper class to initialize
     * @param component
     *         component used with wrapper class
     * @param <T>
     *         component wrapper type
     * @param <Y>
     *         component type
     * @return wrapper with component set
     */
    private <T extends ComponentWrap<Y>, Y extends Component> T initialize(
            Class<T> clazz, Y component) {
        try {
            // Get the generic class for given wrapper. Component should be an instance of this.
            final Class<?> aClass = Stream.of(clazz.getTypeParameters())
                    .map(type -> GenericTypeReflector.erase(type)).findFirst()
                    .get();
            return clazz.getConstructor(aClass).newInstance(component);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(
                    "Could not instantiate " + clazz.getSimpleName()
                            + " for component " + component.getClass()
                            .getSimpleName());
        }
    }
}
