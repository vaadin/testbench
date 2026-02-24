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
package com.vaadin.flow.component.html.testbench;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.testbench.unit.Tests;

@Tests(UnorderedList.class)
public class UnorderedListTester extends HtmlClickContainer<UnorderedList> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public UnorderedListTester(UnorderedList component) {
        super(component);
    }

    /**
     * Get all ListItems for unordered list.
     *
     * @return list of items
     */
    public List<ListItem> getItems() {
        ensureVisible();
        return getComponent().getChildren().filter(ListItem.class::isInstance)
                .map(ListItem.class::cast).collect(Collectors.toList());
    }

    /**
     * Get all ListItems for unordered list already wrapped for testing.
     *
     * @return list of pre-wrapped items
     */
    public List<ListItemTester> getWrappedItems() {
        ensureVisible();
        return getComponent().getChildren().filter(ListItem.class::isInstance)
                .map(ListItem.class::cast).map(ListItemTester::new)
                .collect(Collectors.toList());
    }
}
