package com.vaadin.testbench.unit;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Tag;

@ViewPackages(packages = "com.example")
@ExtendWith(TreeOnFailureExtension.class)
class ComponentTesterFireShortcutTest extends UIUnitTest {

    @Test
    void fireShortcut_usableComponent_methodCallsInternalShortcutFunction() {
        TestComponent component = new TestComponent();
        getCurrentView().add(component);
        ComponentTester<TestComponent> tester = test(component);

        // The component is usable, so this should not throw
        Assertions.assertDoesNotThrow(() -> {
            tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        });
        
        // Test with no modifiers
        Assertions.assertDoesNotThrow(() -> {
            tester.fireShortcut(Key.ENTER);
        });
        
        // Test with multiple modifiers
        Assertions.assertDoesNotThrow(() -> {
            tester.fireShortcut(Key.KEY_S, KeyModifier.ALT, KeyModifier.SHIFT);
        });
    }

    @Test
    void fireShortcut_componentNotUsable_throwsException() {
        TestComponent component = new TestComponent();
        getCurrentView().add(component);
        ComponentTester<TestComponent> tester = test(component);
        
        // Make component not usable
        component.setVisible(false);

        // Should throw IllegalStateException because component is not usable
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        });
        
        Assertions.assertTrue(exception.getMessage().contains("not usable"),
                "Exception should mention that component is not usable");
    }

    @Test
    void fireShortcut_componentNotAttached_throwsException() {
        TestComponent component = new TestComponent();
        // Note: component is not added to current view, so it's not attached
        ComponentTester<TestComponent> tester = test(component);

        // Should throw IllegalStateException because component is not attached
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        });
        
        Assertions.assertTrue(exception.getMessage().contains("not usable"),
                "Exception should mention that component is not usable");
    }

    @Test
    void fireShortcut_componentDisabled_throwsException() {
        TestComponent component = new TestComponent();
        getCurrentView().add(component);
        component.getElement().setEnabled(false);
        ComponentTester<TestComponent> tester = test(component);

        // Should throw IllegalStateException because component is disabled
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            tester.fireShortcut(Key.KEY_G, KeyModifier.CONTROL);
        });
        
        Assertions.assertTrue(exception.getMessage().contains("not usable"),
                "Exception should mention that component is not usable");
    }

    @Tag("test-component")
    private static class TestComponent extends Component {
        // Simple component for testing
    }
}