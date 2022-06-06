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
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ViewPackages
class ListBoxWrapTest extends UIUnitTest {
    ListBoxView view;
    ListBoxWrap<ListBox<String>, String> list_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ListBoxView.class);
        view = navigate(ListBoxView.class);
        list_ = wrap(view.listBox);
    }

    @Test
    void getSuggestionItems_returnsAllItems() {
        assertIterableEquals(view.selection, list_.getSuggestionItems());
    }

    @Test
    void stringSelect_getSuggestions_valuesEqualItems() {
        assertIterableEquals(view.selection, list_.getSuggestions());
    }

    @Test
    void stringSelect_selectItem_selectsCorrectItem() {
        Assertions.assertNull(list_.getSelected());

        list_.selectItem("two");

        Assertions.assertSame(view.selection.get(1), list_.getSelected());

        list_.selectItem(null);

        Assertions.assertNull(list_.getSelected(),
                "Selecting null should clear selection");
    }
}
