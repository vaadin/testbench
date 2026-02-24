/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.html.tester;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.DownloadEvent;
import com.vaadin.flow.server.streams.DownloadHandler;
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
     * Navigate to the anchor target if it's a registered route in the
     * application.
     *
     * @return navigated view
     * @throws IllegalStateException
     *             if anchor href is not a String or not a route
     */
    public HasElement navigate() {
        ensureComponentIsUsable();
        final Field href = getField(Anchor.class, "href");
        try {
            if (href.get(getComponent()) instanceof String) {
                if (RouteConfiguration.forSessionScope().getRoute(getPath())
                        .isPresent()) {
                    UI.getCurrent().navigate(getPath(), getQueryParameters());
                    return UI.getCurrent().getInternals()
                            .getActiveRouterTargetsChain().get(0);
                } else {
                    throw new IllegalStateException(
                            "Anchor is not for an application route");
                }
            } else {
                throw new IllegalStateException(
                        "Anchor target seems to be a resource");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Click the anchor for navigation if target is a registered route in the
     * application. This is equivalent to calling {@link #navigate()}.
     *
     * For cases where you need to access the navigated view, use
     * {@link #navigate()} instead as it returns the target view.
     *
     * @throws IllegalStateException
     *             if anchor href is not a String or not a route
     */
    @Override
    public void click() {
        navigate();
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

        AbstractStreamResource resource = maybeResource
                .filter(res -> res instanceof StreamResource
                        || (res instanceof StreamResourceRegistry.ElementStreamResource el
                                && el.getElementRequestHandler() instanceof DownloadHandler))
                .orElseThrow(() -> new IllegalStateException(
                        "Anchor target does not seem to be a resource"));
        if (resource instanceof StreamResource cast) {
            try {
                cast.getWriter().accept(outputStream, session);
            } catch (IOException e) {
                throw new RuntimeException("Download failed", e);
            }
        } else {
            StreamResourceRegistry.ElementStreamResource elementResource = (StreamResourceRegistry.ElementStreamResource) resource;
            DownloadHandler handler = (DownloadHandler) elementResource
                    .getElementRequestHandler();
            var event = new DownloadEvent(VaadinRequest.getCurrent(),
                    VaadinResponse.getCurrent(), session,
                    elementResource.getOwner()) {
                private boolean outputStreamCalled;
                private boolean writerCalled;

                @Override
                public OutputStream getOutputStream() {
                    if (writerCalled) {
                        throw new IllegalStateException(
                                "Cannot execute getOutputStream() after getWriter() has been called");
                    }
                    outputStreamCalled = true;
                    return outputStream;
                }

                @Override
                public PrintWriter getWriter() {
                    if (outputStreamCalled) {
                        throw new IllegalStateException(
                                "Cannot execute getWriter() after getOutputStream() has been called");
                    }
                    writerCalled = true;
                    return new PrintWriter(outputStream);
                }
            };
            try {
                handler.handleDownloadRequest(event);
            } catch (IOException e) {
                throw new RuntimeException("Download failed", e);
            }
        }
    }
}
