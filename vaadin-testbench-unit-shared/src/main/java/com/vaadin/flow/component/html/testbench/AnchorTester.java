/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import java.lang.reflect.Field;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouteConfiguration;
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
                if (RouteConfiguration.forSessionScope()
                        .getRoute(getComponent().getHref()).isPresent()) {
                    UI.getCurrent().navigate(getComponent().getHref());
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

}
