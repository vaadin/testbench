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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.textfield.GeneratedVaadinTextField;
import com.vaadin.flow.component.textfield.TextFieldWrap;
import com.vaadin.testbench.unit.ComponentQuery;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.Wraps;
import com.vaadin.testbench.unit.internal.PrettyPrintTreeKt;

/**
 *
 * Test wrapper for Button components.
 *
 * @param <T>
 *            component type
 */
@Wraps(Button.class)
public class ButtonWrap<T extends Button> extends ComponentWrap<T> {
    /**
     * Wrap given button for testing.
     *
     * @param component
     *            target button
     */
    public ButtonWrap(T component) {
        super(component);
    }

    /**
     * If the component is usable send click to component as if it was from the
     * client.
     */
    public void click() {
        click(0, new MetaKeys());
    }

    /**
     * If the component is usable send click to component as if it was from the
     * client with defined meta keys pressed.
     */
    public void click(MetaKeys metaKeys) {
        click(0, metaKeys);
    }

    /**
     * Click with middle button.
     */
    public void middleClick() {
        click(1, new MetaKeys());
    }

    /**
     * Click with middle button and given meta keys.
     */
    public void middleClick(MetaKeys metaKeys) {
        click(1, metaKeys);
    }

    /**
     * Click with right button.
     */
    public void rightClick() {
        click(2, new MetaKeys());
    }

    /**
     * Click with right button and given meta keys.
     */
    public void rightClick(MetaKeys metaKeys) {
        click(2, metaKeys);
    }

    private void click(int button, MetaKeys metaKeys) {
        if (!isUsable()) {
            throw new IllegalStateException(
                    PrettyPrintTreeKt.toPrettyString(getComponent())
                            + " is not usable");
        }
        ComponentUtil.fireEvent(getComponent(),
                new ClickEvent(getComponent(), true, 0, 0, 0, 0, 0, button,
                        metaKeys.isCtrl(), metaKeys.isShift(), metaKeys.isAlt(),
                        metaKeys.isMeta()));
    }

    /**
     * Mixin interface to simplify creation of {@link ButtonWrap} wrappers for
     * component instances, avoiding explicit casts.
     *
     * Wrapper creation is based on {@link Mixable} functionality, so this mixin
     * requires to be applied on a class already implementing the
     * {@link Mixable#wrap(Class, Component)} method.
     *
     * Usually used with test classes extending
     * {@link com.vaadin.testbench.unit.UIUnitTest}.
     *
     *
     * <pre>
     * {@code
     * class ViewTest extends UIUnitTest implements ButtonWrap.Mapper {
     *
     *     &#64;Test
     *     void useCaseTest() {
     *         ...
     *         // given view.save is a Button
     *         TheView view = navigate(TheView.class);
     *
     *         // without mapper mixin
     *         ButtonWrap button_ = wrap(ButtonWrap.class, view.save);
     *         button_.click();
     *
     *         // with mixin
     *         wrap(view.save).click();
     *         ...
     *     }
     * }
     * }
     * </pre>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public interface Mixin extends Mixable {

        Kind<Button, ButtonWrap<Button>> BUTTON = new Kind<>(Button.class,
                (Class) ButtonWrap.class);

        default <T extends Button> ButtonWrap<T> wrap(T textField) {
            return wrap(ButtonWrap.class, textField);
        }

        default ComponentQuery<Button, ButtonWrap<Button>> $button() {
            return $(Button.class);
        }

        default <T extends Button> ComponentQuery<T, ButtonWrap<T>> $button(
                Class<T> componentType) {
            return $(componentType);
        }
    }

}
