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
package com.vaadin.flow.component.html;

import com.vaadin.browserless.Tests;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;

@Tests(NativeDetails.class)
public class NativeDetailsTester extends HtmlComponentTester<NativeDetails> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NativeDetailsTester(NativeDetails component) {
        super(component);
    }

    /**
     * Get the summary component of this Details element.
     *
     * @return summary component
     */
    public NativeDetails.Summary getSummary() {
        ensureVisible();
        return getComponent().getSummary();
    }

    /**
     * Get the summary text.
     *
     * @return text in the summary element
     */
    public String getSummaryText() {
        ensureVisible();
        return getComponent().getSummaryText();
    }

    /**
     * Get details content if the details is opened.
     *
     * @return details content
     * @throws IllegalStateException
     *             if content is not displayed
     */
    public Component getContent() {
        ensureVisible();
        if (!getComponent().isOpen()) {
            throw new IllegalStateException("Details are not displayed.");
        }
        return getComponent().getContent();
    }

    /**
     * Toggle the open state of the component.
     */
    public void toggleContent() {
        ensureComponentIsUsable();
        getComponent().setOpen(!getComponent().isOpen());
        ComponentUtil.fireEvent(getComponent(),
                new NativeDetails.ToggleEvent(getComponent(), true));
    }
}
