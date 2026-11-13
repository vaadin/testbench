/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal;

import com.vaadin.flow.component.ClickNotifier.ClickFiltersJs;
import com.vaadin.flow.dom.DomEventFilter;

/**
 * Simulates the click filters of {@code ClickNotifier}, used by its single
 * click and double click listeners.
 * <p>
 * For internal use only.
 */
final class ClickFilterSimulation extends DeclaredFunctionSimulation
        implements ClickFiltersJs {

    ClickFilterSimulation(ClientDomEvent event) {
        super(event);
    }

    @Override
    public DomEventFilter isClickCountBetween(int min, int max) {
        // event.detail >= min && event.detail <= max, where a comparison with
        // undefined is false
        return answer(event().property("detail") instanceof Double detail
                && detail >= min && detail <= max);
    }
}
