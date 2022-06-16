/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.combobox;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class ComboBoxWrapTest extends UIUnitTest {

    ComboBoxView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ComboBoxView.class);
        view = navigate(ComboBoxView.class);
    }

    @Test
    void getSuggestionItems_noFilter_allItemsReturned() {
        final List<ComboBoxView.Name> suggestions = test(view.combo)
                .getSuggestionItems();
        Assertions.assertIterableEquals(view.items, suggestions);
    }

    @Test
    void getSuggestions_noFilter_allItemsReturned() {
        final List<String> suggestions = test(view.combo).getSuggestions();
        Assertions.assertIterableEquals(Arrays.asList("test-foo", "test-bar"),
                suggestions);
    }

    @Test
    void setFilter_getSuggestions_filterIsApplied() {
        test(view.combo).setFilter("fo");
        final List<String> suggestions = test(view.combo).getSuggestions();
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("test-foo", suggestions.get(0));
    }

    @Test
    void selectItem_selectsCorrectItem() {
        Assertions.assertNull(test(view.combo).getSelected());

        test(view.combo).selectItem("test-foo");

        Assertions.assertSame(view.items.get(0),
                test(view.combo).getSelected());

        test(view.combo).selectItem(null);

        Assertions.assertNull(test(view.combo).getSelected(),
                "Selecting null should clear selection");
    }
}
