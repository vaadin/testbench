/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.listbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ViewPackages
class ListBoxTesterTest extends UIUnitTest {
    ListBoxView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ListBoxView.class);
        view = navigate(ListBoxView.class);
    }

    @Test
    void getSuggestionItems_returnsAllItems() {
        assertIterableEquals(view.selection,
                test(view.listBox).getSuggestionItems());
    }

    @Test
    void stringSelect_getSuggestions_valuesEqualItems() {
        assertIterableEquals(view.selection,
                test(view.listBox).getSuggestions());
    }

    @Test
    void stringSelect_selectItem_selectsCorrectItem() {
        Assertions.assertNull(test(view.listBox).getSelected());

        test(view.listBox).selectItem("two");

        Assertions.assertSame(view.selection.get(1),
                test(view.listBox).getSelected());

        test(view.listBox).selectItem(null);

        Assertions.assertNull(test(view.listBox).getSelected(),
                "Selecting null should clear selection");
    }
}
