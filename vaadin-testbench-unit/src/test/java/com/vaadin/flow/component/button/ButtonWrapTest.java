/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.button;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.UIUnitTest;

public class ButtonWrapTest extends UIUnitTest implements ButtonWrap.Mixin {

    @Override
    protected String scanPackage() {
        return "com.example";
    }

    @Test
    public void buttonWithDisableOnClick_notUsableAfterClick() {
        Button button = new Button();
        button.setDisableOnClick(true);
        getCurrentView().getElement().appendChild(button.getElement());

        final ButtonWrap button_ = wrap(button);

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
        Button button = new Button();
        AtomicInteger mouseButton = new AtomicInteger(-1);
        button.addClickListener(event -> mouseButton.set(event.getButton()));

        getCurrentView().getElement().appendChild(button.getElement());

        wrap(button).middleClick();

        Assertions.assertEquals(1, mouseButton.get(),
                "Click event should have sent with middle click");
    }

    @Test
    public void clickWithRightButton_rightButtonClickShouldBeRegistered() {
        Button button = new Button();
        AtomicInteger mouseButton = new AtomicInteger(-1);
        button.addClickListener(event -> mouseButton.set(event.getButton()));

        getCurrentView().getElement().appendChild(button.getElement());

        wrap(button).rightClick();

        Assertions.assertEquals(2, mouseButton.get(),
                "Click event should have sent with right click");
    }

    @Test
    public void normalClick_noMetaKeysMarkedAsUsed() {
        Button button = new Button();
        AtomicReference<ClickEvent> event = new AtomicReference<>(null);
        button.addClickListener(
                clickEvent -> event.compareAndSet(null, clickEvent));
        getCurrentView().getElement().appendChild(button.getElement());

        wrap(button).click();

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
        Button button = new Button();
        AtomicReference<ClickEvent> event = new AtomicReference<>(null);
        button.addClickListener(clickEvent -> event.set(clickEvent));
        getCurrentView().getElement().appendChild(button.getElement());

        ButtonWrap<Button> button_ = wrap(button);
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
