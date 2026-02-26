/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;

/**
 * Mixin interface for component testers that support click simulation.
 *
 * This interface provides default implementations for various click operations
 * including left, middle, and right clicks with optional meta keys.
 *
 * @param <T>
 *            the type of component being tested
 * 
 * @deprecated Replace the vaadin-testbench-unit dependency with
 *             browserless-test-junit6 and use the corresponding class from the
 *             com.vaadin.browserless package instead. This class will be
 *             removed in a future version.
 */
@Deprecated(forRemoval = true, since = "10.1")
public interface Clickable<T extends Component> {

    /**
     * Gets the component being tested.
     *
     * @return the component under test
     */
    T getComponent();

    /**
     * Ensures the component is in a usable state before interaction.
     */
    void ensureComponentIsUsable();

    /**
     * If the component is usable send click to component as if it was from the
     * client.
     */
    default void click() {
        click(0, new MetaKeys());
    }

    /**
     * If the component is usable send click to component as if it was from the
     * client with defined meta keys pressed.
     *
     * @param metaKeys
     *            the meta keys pressed during click
     */
    default void click(MetaKeys metaKeys) {
        click(0, metaKeys);
    }

    /**
     * Click with middle button.
     */
    default void middleClick() {
        click(1, new MetaKeys());
    }

    /**
     * Click with middle button and given meta keys.
     *
     * @param metaKeys
     *            the meta keys pressed during click
     */
    default void middleClick(MetaKeys metaKeys) {
        click(1, metaKeys);
    }

    /**
     * Click with right button.
     */
    default void rightClick() {
        click(2, new MetaKeys());
    }

    /**
     * Click with right button and given meta keys.
     *
     * @param metaKeys
     *            the meta keys pressed during click
     */
    default void rightClick(MetaKeys metaKeys) {
        click(2, metaKeys);
    }

    /**
     * Performs a click with the specified button and meta keys.
     *
     * @param button
     *            the mouse button (0=left, 1=middle, 2=right)
     * @param metaKeys
     *            the meta keys pressed during click
     */
    default void click(int button, MetaKeys metaKeys) {
        ensureComponentIsUsable();
        T component = getComponent();
        ComponentUtil.fireEvent(component,
                new ClickEvent<>(component, true, 0, 0, 0, 0, 0, button,
                        metaKeys.isCtrl(), metaKeys.isShift(), metaKeys.isAlt(),
                        metaKeys.isMeta()));
    }
}
