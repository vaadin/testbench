/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.routerlink;

import java.net.URI;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 *
 * Tester for RouterLink components.
 *
 * @param <T>
 *            component type
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Tests(RouterLink.class)
@Deprecated(forRemoval = true, since = "10.1")
public class RouterLinkTester<T extends RouterLink> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public RouterLinkTester(T component) {
        super(component);
    }

    /**
     * Gets the URL that the router-link links to.
     *
     * @return the href value, or empty string if no href has been set
     */
    public String getHref() {
        ensureComponentIsUsable();

        return getComponent().getHref();
    }

    /**
     * Gets the path for the router-link. Returns an empty {@link String} if
     * there is no corresponding navigation target.
     *
     * @return a {@link String} containing the navigation target path or empty
     *         if not present
     */
    public String getPath() {
        return URI.create(getHref()).getPath();
    }

    /**
     * Gets the query parameters for the router-link.
     *
     * @return a {@link QueryParameters} containing the navigation target's
     *         query parameters
     */
    public QueryParameters getQueryParameters() {
        return QueryParameters.fromString(URI.create(getHref()).getQuery());
    }

    /**
     * Gets the registered route class for the router-link. Returns an empty
     * optional if there is no corresponding navigation target.
     *
     * @return an {@link Optional} containing the navigation target class or
     *         empty if not found
     */
    public Optional<Class<? extends Component>> getRoute() {
        return RouteConfiguration.forSessionScope().getRoute(getPath());
    }

    /**
     * Navigate to the router-link target.
     *
     * @return navigated view
     */
    public HasElement navigate() {
        ensureComponentIsUsable();
        String path = getPath();
        if (getRoute().isEmpty()) {
            throw new IllegalStateException(
                    "Application route not found for path " + path);
        }

        UI.getCurrent().navigate(path, getQueryParameters());
        return UI.getCurrent().getInternals().getActiveRouterTargetsChain()
                .get(0);
    }

    /**
     * Click the router-link for navigation. This is equivalent to calling
     * {@link #navigate()}.
     *
     * For cases where you need to access the navigated view, use
     * {@link #navigate()} instead as it returns the target view.
     */
    @Override
    public void click() {
        navigate();
    }
}
