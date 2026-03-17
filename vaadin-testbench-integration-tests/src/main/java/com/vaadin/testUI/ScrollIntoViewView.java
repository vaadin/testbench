/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

/**
 * Test view for verifying that {@code doubleClick()}, {@code click()} and
 * {@code click(x,y)} work on elements that are inside a scrollable container
 * but are scrolled out of the visible area.
 * <p>
 * Reproduces the scenario described in
 * <a href="https://github.com/vaadin/testbench/issues/2156">#2156</a> where the
 * target element is in the DOM and {@code isDisplayed()} returns {@code true},
 * but the element's bounding rect is outside the browser viewport because the
 * scrollable ancestor has not been scrolled.
 */
@Route("ScrollIntoViewView")
public class ScrollIntoViewView extends Div {

    public ScrollIntoViewView() {
        setId("scroll-into-view-test");

        // Scrollable container with fixed dimensions
        Div container = new Div();
        container.setId("scroll-container");
        container.getStyle().set("width", "100px");
        container.getStyle().set("height", "200px");
        container.getStyle().set("overflow", "auto");
        container.getStyle().set("border", "1px solid black");
        container.getStyle().set("display", "flex");

        // First div - visible
        Div first = new Div();
        first.setText("First");
        first.setWidth("100px");
        first.getStyle().set("min-width", "100px");
        first.getStyle().set("height", "50px");
        first.getStyle().set("background-color", "lightgreen");

        // Middle div - very wide, pushes the target out of view
        Div middle = new Div();
        middle.setWidth("5000px");
        middle.getStyle().set("min-width", "5000px");
        middle.getStyle().set("height", "50px");
        middle.getStyle().set("background-color", "lightyellow");

        // Target div at the end, outside the visible area
        // Must be narrower than the container so that after scrollIntoView
        // the element's center is within the container's visible area
        Div target = new Div();
        target.setWidth("80px");
        target.setId("target-element");
        target.getStyle().set("min-width", "80px");
        target.getStyle().set("height", "50px");
        target.getStyle().set("background-color", "lightblue");

        target.getElement().addEventListener("dblclick", e -> {
            Div result = new Div();
            result.setId("dblclick-result");
            result.setText("Double-click received");
            add(result);
        });
        target.getElement().addEventListener("click", e -> {
            Div result = new Div();
            result.setId("click-result");
            result.setText("Click received");
            add(result);
        });

        container.add(first, middle, target);
        add(container);
    }
}
