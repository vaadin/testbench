/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.component.ShortcutRegistration.ShortcutFilterJs;
import com.vaadin.flow.dom.DomEventFilter;

/**
 * Simulates the keydown filter of shortcuts, which runs
 * {@code window.Vaadin.Flow.shortcut.matchKeydown()} of the Flow client, see
 * {@code Shortcut.ts}.
 * <p>
 * For internal use only.
 */
final class KeydownFilterSimulation extends DeclaredFunctionSimulation
        implements ShortcutFilterJs {

    KeydownFilterSimulation(ClientDomEvent event) {
        super(event);
    }

    @Override
    public DomEventFilter matchKeydown(String requiredModifiers,
            String forbiddenModifiers, boolean checkOwnerScope,
            String ownerSelector, boolean preventDefault,
            boolean stopPropagation, boolean resetFocus, String... keys) {
        ClientDomEvent event = event();
        List<String> keyValues = Arrays.asList(keys);
        boolean matched = (keyValues.contains(event.property("code"))
                || keyValues.contains(event.property("key")))
                && modifiers(requiredModifiers)
                        .allMatch(event::isModifierPressed)
                && modifiers(forbiddenModifiers)
                        .noneMatch(event::isModifierPressed);
        // The popover/modal scope of the owner is not checked: the event is
        // dispatched on the element that listens for it, which has no nested
        // overlay to originate from. Resetting the focus and stopping the
        // propagation have no effect on the server.
        if (matched && preventDefault) {
            event.preventDefault();
        }
        return answer(matched);
    }

    private static Stream<String> modifiers(String names) {
        return Arrays.stream(names.split(" ")).filter(name -> !name.isEmpty());
    }
}
