/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.combobox;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class MultiSelectComboBoxTesterTest extends UIUnitTest {

    MultiSelectComboBoxView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(MultiSelectComboBoxView.class);
        view = navigate(MultiSelectComboBoxView.class);
    }

    @Test
    void getSuggestionItems_noFilter_allItemsReturned() {
        final List<MultiSelectComboBoxView.Name> suggestions = test(view.combo)
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
        Assertions.assertTrue(test(view.combo).getSelected().isEmpty());

        test(view.combo).selectItem("test-foo");

        Assertions.assertIterableEquals(Set.of(view.items.get(0)),
                test(view.combo).getSelected());

        test(view.combo).selectItem("test-foo", "test-bar");

        Assertions.assertTrue(
                test(view.combo).getSelected().contains(view.items.get(0)));
        Assertions.assertTrue(
                test(view.combo).getSelected().contains(view.items.get(1)));

        test(view.combo).selectItem(null);

        Assertions.assertTrue(test(view.combo).getSelected().isEmpty(),
                "Selecting null should clear selection");
    }
}
