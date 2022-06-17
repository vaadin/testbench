/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.html.testbench;

import java.lang.reflect.Field;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.AbstractStreamResource;
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
