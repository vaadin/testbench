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
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Tests for the Clickable mixin interface functionality.
 */
@ViewPackages(packages = "com.example")
public class ClickableTest extends UIUnitTest {

    private Div container;

    @BeforeEach
    public void init() {
        container = new Div();
        getCurrentView().getElement().appendChild(container.getElement());
    }

    @Test
    public void testClick_firesClickEvent() {
        Div div = new Div("Click me");
        container.add(div);

        AtomicInteger clickCount = new AtomicInteger(0);
        div.addClickListener(e -> clickCount.incrementAndGet());

        ComponentTester<Div> divTester = test(div);
        divTester.click();

        Assertions.assertEquals(1, clickCount.get(),
                "Click event should be fired once");
    }

    @Test
    public void testClick_withMetaKeys() {
        Span span = new Span("Click with meta keys");
        container.add(span);

        AtomicReference<ClickEvent<?>> clickEvent = new AtomicReference<>();
        span.addClickListener(clickEvent::set);

        ComponentTester<Span> spanTester = test(span);
        MetaKeys metaKeys = new MetaKeys().ctrl().shift();
        spanTester.click(metaKeys);

        Assertions.assertNotNull(clickEvent.get(),
                "Click event should be fired");
        Assertions.assertTrue(clickEvent.get().isCtrlKey(),
                "Ctrl key should be pressed");
        Assertions.assertTrue(clickEvent.get().isShiftKey(),
                "Shift key should be pressed");
        Assertions.assertFalse(clickEvent.get().isAltKey(),
                "Alt key should not be pressed");
        Assertions.assertFalse(clickEvent.get().isMetaKey(),
                "Meta key should not be pressed");
    }

    @Test
    public void testMiddleClick() {
        Div div = new Div("Middle click me");
        container.add(div);

        AtomicReference<Integer> buttonPressed = new AtomicReference<>();
        div.addClickListener(e -> buttonPressed.set(e.getButton()));

        ComponentTester<Div> divTester = test(div);
        divTester.middleClick();

        Assertions.assertEquals(1, buttonPressed.get(),
                "Middle button (1) should be pressed");
    }

    @Test
    public void testRightClick() {
        Span span = new Span("Right click me");
        container.add(span);

        AtomicReference<Integer> buttonPressed = new AtomicReference<>();
        span.addClickListener(e -> buttonPressed.set(e.getButton()));

        ComponentTester<Span> spanTester = test(span);
        spanTester.rightClick();

        Assertions.assertEquals(2, buttonPressed.get(),
                "Right button (2) should be pressed");
    }

    @Test
    public void testClick_componentDisabled_throwsException() {
        Button button = new Button("Disabled button");
        button.setEnabled(false);
        container.add(button);

        ComponentTester<Button> buttonTester = test(button);

        Assertions.assertThrows(IllegalStateException.class,
                buttonTester::click,
                "Should throw exception when clicking disabled component");
    }

    @Test
    public void testClick_componentNotVisible_throwsException() {
        Div div = new Div("Hidden div");
        div.setVisible(false);
        container.add(div);

        ComponentTester<Div> divTester = test(div);

        Assertions.assertThrows(IllegalStateException.class, divTester::click,
                "Should throw exception when clicking invisible component");
    }

    @Test
    public void testClick_componentNotAttached_throwsException() {
        Span span = new Span("Detached span");
        // Not adding to container, so it's not attached

        ComponentTester<Span> spanTester = test(span);

        Assertions.assertThrows(IllegalStateException.class, spanTester::click,
                "Should throw exception when clicking detached component");
    }

    @Test
    public void testClick_customComponent() {
        // Custom component that doesn't implement ClickNotifier but handles
        // click events directly
        @Tag("custom-element")
        class CustomComponent extends Component {
            int clickCount = 0;

            public CustomComponent() {
                ComponentUtil.addListener(this, ClickEvent.class,
                        (ComponentEventListener) e -> {
                            clickCount++;
                        });
            }
        }

        CustomComponent custom = new CustomComponent();
        container.add(custom);

        ComponentTester<CustomComponent> customTester = test(custom);
        customTester.click();

        Assertions.assertEquals(1, custom.clickCount,
                "Click should work on custom components that handle click events");
    }

    @Test
    public void testRightClick_withMetaKeys() {
        Div div = new Div("Right click with meta");
        container.add(div);

        AtomicReference<ClickEvent<?>> clickEvent = new AtomicReference<>();
        div.addClickListener(clickEvent::set);

        ComponentTester<Div> divTester = test(div);
        MetaKeys metaKeys = new MetaKeys().alt().meta();
        divTester.rightClick(metaKeys);

        Assertions.assertNotNull(clickEvent.get(),
                "Click event should be fired");
        Assertions.assertEquals(2, clickEvent.get().getButton(),
                "Right button should be pressed");
        Assertions.assertTrue(clickEvent.get().isAltKey(),
                "Alt key should be pressed");
        Assertions.assertTrue(clickEvent.get().isMetaKey(),
                "Meta key should be pressed");
        Assertions.assertFalse(clickEvent.get().isCtrlKey(),
                "Ctrl key should not be pressed");
        Assertions.assertFalse(clickEvent.get().isShiftKey(),
                "Shift key should not be pressed");
    }
}
