/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.testbench.unit.Tests;

@Tests(OrderedList.class)
public class OrderedListTester extends HtmlClickContainer<OrderedList> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public OrderedListTester(OrderedList component) {
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
