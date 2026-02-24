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
package com.vaadin.flow.component.details;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for Details components.
 *
 * @param <T>
 *            component type
 */
@Tests(Details.class)
public class DetailsTester<T extends Details> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DetailsTester(T component) {
        super(component);
    }

    /**
     * Shows contents as if the summary is clicked on the browser.
     *
     * An exception will be thrown if the details are already open.
     *
     * @throws IllegalStateException
     *             if the component is not usable or if the details are already
     *             open.
     */
    public void openDetails() {
        ensureComponentIsUsable();
        setOpened(true);
    }

    /**
     * Hides contents as if the summary is clicked on the browser.
     *
     * An exception will be thrown if the details are not open.
     *
     * @throws IllegalStateException
     *             if the component is not usable or if the details are not
     *             open.
     */
    public void closeDetails() {
        ensureComponentIsUsable();
        setOpened(false);
    }

    /**
     * Toggles details visibility, as if the summary is clicked on the browser.
     */
    public void toggleDetails() {
        ensureComponentIsUsable();
        setOpened(!getComponent().isOpened());
    }

    /**
     * Checks if the details are open.
     *
     * @return {@literal true} if the details are open, otherwise
     *         {@literal false}.
     */
    public boolean isOpen() {
        ensureComponentIsUsable();
        return getComponent().isOpened();
    }

    private void setOpened(boolean opened) {
        T component = getComponent();
        if (opened == component.isOpened()) {
            throw new IllegalStateException(
                    "Details are already " + (opened ? "open" : "close"));
        }
        component.setOpened(opened);
    }

}
