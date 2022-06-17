/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
