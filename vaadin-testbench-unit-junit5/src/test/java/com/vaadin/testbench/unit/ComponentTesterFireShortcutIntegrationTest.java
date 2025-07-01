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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.DomListenerRegistration;

@ViewPackages(packages = "com.example")
@ExtendWith(TreeOnFailureExtension.class)
class ComponentTesterFireShortcutIntegrationTest extends UIUnitTest {

    @Test
    void fireShortcut_componentWithDomKeyDownListener_listenerInvoked() {
        AtomicInteger eventsCounter = new AtomicInteger();
        TestComponent component = new TestComponent();
        
        // Add a DOM-level keydown listener to simulate KeyNotifier behavior
        component.getElement().addEventListener("keydown", domEvent -> {
            String key = domEvent.getEventData().getString("event.key");
            if ("g".equals(key)) {
                eventsCounter.incrementAndGet();
            }
        }).addEventData("event.key");

        getCurrentView().add(component);
        ComponentTester<TestComponent> tester = test(component);

        // Fire shortcut - this should trigger the keydown listener
        tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        Assertions.assertEquals(1, eventsCounter.get());

        // Fire different shortcut
        tester.fireShortcut(Key.KEY_A, KeyModifier.CONTROL);
        Assertions.assertEquals(1, eventsCounter.get());

        // Fire the same shortcut again
        tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        Assertions.assertEquals(2, eventsCounter.get());
    }

    @Test
    void fireShortcut_componentWithMultipleListeners_allListenersInvoked() {
        AtomicInteger gKeyCounter = new AtomicInteger();
        AtomicInteger aKeyCounter = new AtomicInteger();
        TestComponent component = new TestComponent();
        
        // Add listeners for different keys
        component.getElement().addEventListener("keydown", domEvent -> {
            String key = domEvent.getEventData().getString("event.key");
            if ("g".equals(key)) {
                gKeyCounter.incrementAndGet();
            } else if ("a".equals(key)) {
                aKeyCounter.incrementAndGet();
            }
        }).addEventData("event.key");

        getCurrentView().add(component);
        ComponentTester<TestComponent> tester = test(component);

        // Fire G key shortcut
        tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        Assertions.assertEquals(1, gKeyCounter.get());
        Assertions.assertEquals(0, aKeyCounter.get());

        // Fire A key shortcut  
        tester.fireShortcut(Key.KEY_A, KeyModifier.ALT);
        Assertions.assertEquals(1, gKeyCounter.get());
        Assertions.assertEquals(1, aKeyCounter.get());
    }

    @Test
    void fireShortcut_comparedToDirectDomEvent_behaviorIsConsistent() {
        AtomicInteger shortcutCounter = new AtomicInteger();
        AtomicInteger domEventCounter = new AtomicInteger();
        TestComponent component = new TestComponent();
        
        // Add keydown listener that counts both
        component.getElement().addEventListener("keydown", domEvent -> {
            String key = domEvent.getEventData().getString("event.key");
            if ("g".equals(key)) {
                shortcutCounter.incrementAndGet();
                domEventCounter.incrementAndGet();
            }
        }).addEventData("event.key");

        getCurrentView().add(component);
        ComponentTester<TestComponent> tester = test(component);

        // Use our fireShortcut method
        tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        int afterShortcut = shortcutCounter.get();

        // Use direct DOM event (for comparison)
        tester.fireDomEvent("keydown", elemental.json.Json.create("{\"event.key\": \"g\"}"));
        int afterDomEvent = domEventCounter.get();

        // Both should have triggered the listener
        Assertions.assertTrue(afterShortcut > 0, "Shortcut should have triggered listener");
        Assertions.assertTrue(afterDomEvent > afterShortcut, "DOM event should also trigger listener");
    }

    @Tag("test-component")
    private static class TestComponent extends Component {
        // Simple component for testing
    }
}