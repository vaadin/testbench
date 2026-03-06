/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

/**
 * Test view that reproduces {@code MoveTargetOutOfBoundsException} by mimicking
 * vaadin-grid's Shadow DOM structure.
 * <p>
 * The key aspects of vaadin-grid that cause the exception:
 * <ul>
 * <li>Shadow DOM with an internal scrollable container
 * ({@code overflow: auto})</li>
 * <li>Absolutely-positioned rows inside the container</li>
 * <li>Cells that extend far beyond the container's visible width
 * (300+800+100=1200px in a 200px-wide container)</li>
 * <li>Slotted cell content from light DOM — the element Selenium interacts with
 * is in light DOM, but its visual position is determined by the Shadow DOM
 * layout</li>
 * </ul>
 * WebDriver's {@code Actions.moveToElement} sees the cell's
 * {@code getBoundingClientRect().x} beyond the viewport width and throws
 * {@code MoveTargetOutOfBoundsException}.
 *
 * @see <a href="https://github.com/vaadin/testbench/issues/2156">#2156</a>
 */
@Route("MoveTargetOutOfBoundsView")
public class MoveTargetOutOfBoundsView extends Div {

    @Tag("grid-like-container")
    @JsModule("grid-like-container.ts")
    public static class GridLikeContainer extends Component {

        public void add(Component component, String slot) {
            component.getElement().setAttribute("slot", slot);
            getElement().appendChild(component.getElement());
        }
    }

    public MoveTargetOutOfBoundsView() {
        setId("move-target-test");

        GridLikeContainer container = new GridLikeContainer();
        container.getElement().getStyle().set("width", "200px");

        Div first = new Div();
        first.setText("First");
        container.add(first, "first");

        Div spacer = new Div();
        spacer.setText("Spacer");
        container.add(spacer, "spacer");

        Div target = new Div();
        target.setId("target-element");
        target.setText("Target");
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
        container.add(target, "target");

        add(container);
    }
}
