/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless;

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
 */
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
