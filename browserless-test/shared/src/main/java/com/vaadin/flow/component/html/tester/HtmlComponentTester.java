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
package com.vaadin.flow.component.html.tester;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.testbench.unit.ComponentTester;

public class HtmlComponentTester<T extends HtmlComponent>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public HtmlComponentTester(T component) {
        super(component);
    }

    /**
     * Get the title string set for the html component if available.
     *
     * @return title string
     * @throws IllegalStateException
     *             if not title has been set
     */
    public String getTitle() {
        ensureVisible();
        return getComponent().getTitle()
                .orElseThrow(() -> new IllegalStateException(
                        "No title set for " + getComponent().getClassName()));
    }

    /**
     * Get the recursive text for target element.
     *
     * @return recursive text of component
     * @throws IllegalStateException
     *             if component not visible
     */
    public String getText() {
        ensureVisible();
        return getComponent().getElement().getTextRecursively();
    }

}
