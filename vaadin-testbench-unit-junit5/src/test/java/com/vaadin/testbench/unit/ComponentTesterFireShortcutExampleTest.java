/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Tag;

/**
 * Example showing how to use ComponentTester.fireShortcut() method
 * to test components with keyboard shortcuts.
 */
@ViewPackages(packages = "com.example")
@ExtendWith(TreeOnFailureExtension.class)
class ComponentTesterFireShortcutExampleTest extends UIUnitTest {

    @Test
    void example_testComponentWithKeyboardShortcut() {
        // Create a component that responds to keyboard shortcuts
        MyCustomComponent component = new MyCustomComponent();
        getCurrentView().add(component);
        
        // Get a tester for the component
        ComponentTester<MyCustomComponent> tester = test(component);
        
        // Test that the shortcut works
        Assertions.assertFalse(component.isShortcutTriggered(), "Shortcut should not be triggered initially");
        
        // Fire the shortcut using ComponentTester
        tester.fireShortcut(Key.KEY_S, KeyModifier.CONTROL);
        
        // Verify the shortcut was handled
        Assertions.assertTrue(component.isShortcutTriggered(), "Shortcut should have been triggered");
    }

    @Test 
    void example_differenceFromUILevelShortcuts() {
        MyCustomComponent component = new MyCustomComponent();
        getCurrentView().add(component);
        ComponentTester<MyCustomComponent> tester = test(component);
        
        // This fires a shortcut on the specific component
        // (for KeyNotifier listeners attached to the component)
        tester.fireShortcut(Key.KEY_S, KeyModifier.CONTROL);
        Assertions.assertTrue(component.isShortcutTriggered());
        
        // Reset for next test
        component.resetShortcutTriggered();
        
        // This fires a shortcut at the UI level  
        // (for shortcuts created with Shortcuts.addShortcutListener on UI)
        fireShortcut(Key.KEY_S, KeyModifier.CONTROL);
        
        // The component-level listener won't be triggered by UI-level shortcut
        // (depends on how the listeners are set up)
        // This demonstrates the difference between the two approaches
    }

    @Tag("my-custom-component")
    private static class MyCustomComponent extends Component {
        private final AtomicBoolean shortcutTriggered = new AtomicBoolean(false);
        
        public MyCustomComponent() {
            // Simulate a component that listens for keyboard shortcuts
            // In a real component this would be done with KeyNotifier interface
            getElement().addEventListener("keydown", domEvent -> {
                String key = domEvent.getEventData().getString("event.key");
                if ("s".equals(key)) {
                    shortcutTriggered.set(true);
                }
            }).addEventData("event.key");
        }
        
        public boolean isShortcutTriggered() {
            return shortcutTriggered.get();
        }
        
        public void resetShortcutTriggered() {
            shortcutTriggered.set(false);
        }
    }
}