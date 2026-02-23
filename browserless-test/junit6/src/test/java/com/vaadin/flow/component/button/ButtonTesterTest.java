/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.button;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class ButtonTesterTest extends UIUnitTest {

    private ButtonView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ButtonView.class);
        view = navigate(ButtonView.class);
    }

    @Test
    public void buttonWithDisableOnClick_notUsableAfterClick() {
        view.button.setDisableOnClick(true);

        final ButtonTester button_ = test(ButtonTester.class, view.button);

        Assertions.assertTrue(button_.isUsable(),
                "Button should be usable before click");

        button_.click();

        Assertions.assertFalse(button_.isUsable(),
                "Button should have been disabled after click");

        Assertions.assertThrows(IllegalStateException.class,
                () -> button_.click(),
                "Illegal state should be thrown for disabled button");
    }

    @Test
    public void clickWithMiddleButton_middleButtonClickShouldBeRegistered() {
        AtomicInteger mouseButton = new AtomicInteger(-1);
        view.button
                .addClickListener(event -> mouseButton.set(event.getButton()));

        final ButtonTester button_ = test(ButtonTester.class, view.button);
        button_.middleClick();

        Assertions.assertEquals(1, mouseButton.get(),
                "Click event should have sent with middle click");
    }

    @Test
    public void clickWithRightButton_rightButtonClickShouldBeRegistered() {
        AtomicInteger mouseButton = new AtomicInteger(-1);
        view.button
                .addClickListener(event -> mouseButton.set(event.getButton()));

        final ButtonTester button_ = test(ButtonTester.class, view.button);
        button_.rightClick();

        Assertions.assertEquals(2, mouseButton.get(),
                "Click event should have sent with right click");
    }

    @Test
    public void normalClick_noMetaKeysMarkedAsUsed() {
        AtomicReference<ClickEvent> event = new AtomicReference<>(null);
        view.button.addClickListener(
                clickEvent -> event.compareAndSet(null, clickEvent));

        final ButtonTester button_ = test(ButtonTester.class, view.button);
        button_.click();

        Assertions.assertNotNull(event.get(),
                "event should have fired and recorded");
        Assertions.assertFalse(event.get().isCtrlKey(),
                "Ctrl should not have been used");
        Assertions.assertFalse(event.get().isShiftKey(),
                "Shift should not have been used");
        Assertions.assertFalse(event.get().isAltKey(),
                "Alt should not have been used");
        Assertions.assertFalse(event.get().isMetaKey(),
                "Meta should not have been used");
    }

    @Test
    public void clickWithMeta_metaKeysMarkedAsUsed() {
        AtomicReference<ClickEvent> event = new AtomicReference<>(null);
        view.button.addClickListener(clickEvent -> event.set(clickEvent));

        final ButtonTester button_ = test(ButtonTester.class, view.button);
        button_.click(new MetaKeys(true, true, true, true));

        Assertions.assertNotNull(event.get(),
                "event should have fired and recorded");
        Assertions.assertTrue(event.get().isCtrlKey(),
                "Ctrl should have been used");
        Assertions.assertTrue(event.get().isShiftKey(),
                "Shift should have been used");
        Assertions.assertTrue(event.get().isAltKey(),
                "Alt should have been used");
        Assertions.assertTrue(event.get().isMetaKey(),
                "Meta should have been used");

        button_.click(new MetaKeys(true, true, false, false));
        Assertions.assertTrue(event.get().isCtrlKey(),
                "Ctrl should have been used");
        Assertions.assertTrue(event.get().isShiftKey(),
                "Shift should have been used");
        Assertions.assertFalse(event.get().isAltKey(),
                "Alt should not have been used");
        Assertions.assertFalse(event.get().isMetaKey(),
                "Meta should not have been used");
    }
}
