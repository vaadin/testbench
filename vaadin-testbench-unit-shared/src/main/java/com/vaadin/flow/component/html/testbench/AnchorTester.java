/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceRegistry;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.Tests;

@Tests(Anchor.class)
public class AnchorTester extends HtmlContainerTester<Anchor> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public AnchorTester(Anchor component) {
        super(component);
    }

    /**
     * Gets the URL that the anchor links to.
     *
     * @return the href value, or <code>""</code> if no href has been set
     */
    public String getHref() {
        ensureComponentIsUsable();
        return getComponent().getHref();
    }

    /**
     * Gets the path for the router-link.
     * Returns an empty {@link String} if there is no corresponding navigation target.
     *
     * @return a {@link String} containing the navigation target path or empty if not present
     */
    public String getPath() {
        return URI.create(getHref()).getPath();
    }

    /**
     * Gets the query parameters for the router-link.
     *
     * @return a {@link QueryParameters} containing the navigation target's query parameters
     */
    public QueryParameters getQueryParameters() {
        return QueryParameters.fromString(URI.create(getHref()).getQuery());
    }

    /**
     * Click the anchor for navigation if target is a registered route in the
     * application.
     *
     * @return navigated view
     * @throws IllegalStateException
     *             if anchor href is not a String or not a route
     */
    public HasElement click() {
        ensureComponentIsUsable();
        final Field href = getField(Anchor.class, "href");
        try {
            if (href.get(getComponent()) instanceof String) {
                if (RouteConfiguration.forSessionScope().getRoute(getPath()).isPresent()) {
                    UI.getCurrent().navigate(getPath(), getQueryParameters());
                    return UI.getCurrent().getInternals()
                            .getActiveRouterTargetsChain().get(0);
                } else {
                    throw new IllegalStateException(
                            "Anchor is not for an application route");
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Anchor target seems to be a resource");
    }

    /**
     * Download the stream resource linked by the anchor.
     *
     * @param outputStream
     *            output stream to write the stream resource to
     * @throws IllegalStateException
     *             if the anchor does not link to a stream resource
     */
    public void download(OutputStream outputStream) {
        ensureComponentIsUsable();

        Anchor anchor = getComponent();
        VaadinSession session = VaadinSession.getCurrent();
        StreamResourceRegistry registry = session.getResourceRegistry();

        Optional<AbstractStreamResource> maybeResource = Optional.empty();
        try {
            maybeResource = registry.getResource(new URI(anchor.getHref()));
        } catch (URISyntaxException e) {
            // Ignore, throws below if resource is empty
        }

        if (maybeResource.isEmpty() || !(maybeResource
                .get() instanceof StreamResource streamResource)) {
            throw new IllegalStateException(
                    "Anchor target does not seem to be a resource");
        }

        try {
            streamResource.getWriter().accept(outputStream, session);
        } catch (IOException e) {
            throw new RuntimeException("Download failed", e);
        }
    }
}
