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

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class MultiSelectListBoxWrapTest extends UIUnitTest {
    ListBoxView view;
    MultiSelectListBoxWrap<MultiSelectListBox<String>, String> list_;

    @Override
    protected String scanPackage() {
        return getClass().getPackageName();
    }

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ListBoxView.class);
        view = navigate(ListBoxView.class);
        list_ = wrap(view.multiSelectListBox);
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
    void stringSelect_selectItemsDeselectItems_selectsCorrectItem() {
        Assertions.assertTrue(list_.getSelected().isEmpty());

        list_.selectItems("two");

        Assertions.assertIterableEquals(view.selection.subList(1, 2),
                list_.getSelected());

        list_.deselectItems("two");

        Assertions.assertTrue(list_.getSelected().isEmpty(),
                "Deselecting item should clear selection");
    }

    @Test
    void stringSelect_selectItems_addsToSelection() {
        Assertions.assertTrue(list_.getSelected().isEmpty());

        list_.selectItems("two");

        Assertions.assertIterableEquals(view.selection.subList(1, 2),
                list_.getSelected());

        list_.selectItems("one");

        Assertions.assertIterableEquals(view.selection, list_.getSelected());
    }

    @Test
    void clearSelection_selectItems_addsToSelection() {
        Assertions.assertTrue(list_.getSelected().isEmpty());

        list_.selectItems("two", "one");

        Assertions.assertIterableEquals(view.selection, list_.getSelected());

        list_.clearSelection();

        Assertions.assertTrue(list_.getSelected().isEmpty());
    }

}
