/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.listbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.TestBenchUnit;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ViewPackages
class ListBoxWrapTest extends TestBenchUnit {
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
                wrap(view.listBox).getSuggestionItems());
    }

    @Test
    void stringSelect_getSuggestions_valuesEqualItems() {
        assertIterableEquals(view.selection,
                wrap(view.listBox).getSuggestions());
    }

    @Test
    void stringSelect_selectItem_selectsCorrectItem() {
        Assertions.assertNull(wrap(view.listBox).getSelected());

        wrap(view.listBox).selectItem("two");

        Assertions.assertSame(view.selection.get(1),
                wrap(view.listBox).getSelected());

        wrap(view.listBox).selectItem(null);

        Assertions.assertNull(wrap(view.listBox).getSelected(),
                "Selecting null should clear selection");
    }
}
