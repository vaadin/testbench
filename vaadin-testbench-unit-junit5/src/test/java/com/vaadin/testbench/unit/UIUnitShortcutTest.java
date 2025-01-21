/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;

@ViewPackages(packages = "com.example")
@ExtendWith(TreeOnFailureExtension.class)
class UIUnitShortcutTest extends UIUnitTest {

    @Test
    void fireShortcut_UIListener_invokedForExactMatch() {
        AtomicInteger eventsCounter = new AtomicInteger();
        UI.getCurrent().addShortcutListener(eventsCounter::incrementAndGet,
                Key.KEY_W, KeyModifier.ALT, KeyModifier.SHIFT);

        fireShortcut(Key.KEY_W);
        Assertions.assertEquals(0, eventsCounter.get());

        fireShortcut(Key.KEY_W, KeyModifier.ALT);
        Assertions.assertEquals(0, eventsCounter.get());

        fireShortcut(Key.KEY_W, KeyModifier.ALT, KeyModifier.SHIFT);
        Assertions.assertEquals(1, eventsCounter.get());

        fireShortcut(Key.KEY_W, KeyModifier.ALT, KeyModifier.SHIFT,
                KeyModifier.CONTROL);
        Assertions.assertEquals(1, eventsCounter.get());

        fireShortcut(Key.KEY_W, KeyModifier.ALT, KeyModifier.SHIFT);
        Assertions.assertEquals(2, eventsCounter.get());
    }

    @Test
    void fireShortcut_nestedComponents_listenersInvoked() {
        AtomicInteger buttonEvents = new AtomicInteger();
        AtomicInteger nestedButtonEvents = new AtomicInteger();

        var button = new Button();
        button.addClickListener(event -> buttonEvents.incrementAndGet());
        button.addClickShortcut(Key.ENTER);
        Shortcuts.addShortcutListener(button, buttonEvents::incrementAndGet,
                Key.KEY_G, KeyModifier.CONTROL);

        var nested = new Button();
        nested.addClickListener(event -> nestedButtonEvents.incrementAndGet());
        nested.addClickShortcut(Key.KEY_S, KeyModifier.ALT);
        Shortcuts.addShortcutListener(nested,
                nestedButtonEvents::incrementAndGet, Key.KEY_G,
                KeyModifier.CONTROL);

        getCurrentView().getElement().appendChild(button.getElement(),
                new Div(new Div(nested)).getElement());

        fireShortcut(Key.ENTER);
        Assertions.assertEquals(1, buttonEvents.get());
        Assertions.assertEquals(0, nestedButtonEvents.get());

        fireShortcut(Key.KEY_S, KeyModifier.ALT);
        Assertions.assertEquals(1, buttonEvents.get());
        Assertions.assertEquals(1, nestedButtonEvents.get());

        fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        Assertions.assertEquals(2, buttonEvents.get());
        Assertions.assertEquals(2, nestedButtonEvents.get());
    }

    @Test
    void fireShortcut_modal_listenersInvokedOnModalChildren() {
        AtomicInteger buttonEvents = new AtomicInteger();
        AtomicInteger modalButtonEvents = new AtomicInteger();

        var button = new Button();
        button.addClickListener(event -> buttonEvents.incrementAndGet());
        button.addClickShortcut(Key.ENTER);

        var nested = new Button();
        nested.addClickListener(event -> modalButtonEvents.incrementAndGet());
        nested.addClickShortcut(Key.ENTER);

        Div modal = new Div(new Div(nested));
        getCurrentView().getElement().appendChild(button.getElement());

        fireShortcut(Key.ENTER);
        Assertions.assertEquals(1, buttonEvents.get());
        Assertions.assertEquals(0, modalButtonEvents.get());

        UI.getCurrent().addModal(modal);

        fireShortcut(Key.ENTER);
        Assertions.assertEquals(1, buttonEvents.get());
        Assertions.assertEquals(1, modalButtonEvents.get());

        UI.getCurrent().setChildComponentModal(modal, false);

        fireShortcut(Key.ENTER);
        Assertions.assertEquals(2, buttonEvents.get());
        Assertions.assertEquals(1, modalButtonEvents.get());
    }

    @Tag("button")
    private static class Button extends Component
            implements ClickNotifier<Button> {

    }

    @Tag("div")
    private static class Div extends Component implements HasComponents {
        public Div(Component... children) {
            add(children);
        }
    }

}
