/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.combobox;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class ComboBoxCustomFilteringTesterTest extends UIUnitTest {

    ComboBoxCustomFilteringView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ComboBoxCustomFilteringView.class);
        view = navigate(ComboBoxCustomFilteringView.class);
    }

    @Test
    void selectItem_withCustomFiltering_respectsNativeFilter() {
        // Test that selectItem respects the custom filtering
        // "John" should be selectable as it matches the first name
        test(view.combo).selectItem("John");
        Assertions.assertEquals(view.items.get(0),
                test(view.combo).getSelected());

        // Clear selection
        test(view.combo).selectItem(null);
        Assertions.assertNull(test(view.combo).getSelected());

        // "Jane" should also be selectable
        test(view.combo).selectItem("Jane");
        Assertions.assertEquals(view.items.get(1),
                test(view.combo).getSelected());
    }

    @Test
    void setFilter_withCustomFiltering_filtersCorrectly() {
        // Test filtering by last name (which is part of custom filter logic)
        test(view.combo).setFilter("Smith");
        List<String> suggestions = test(view.combo).getSuggestions();

        // Should find John Smith even though we're filtering by last name
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("John", suggestions.get(0));

        // Test filtering by "son" which should match "Johnson"
        test(view.combo).setFilter("son");
        suggestions = test(view.combo).getSuggestions();
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("Bob", suggestions.get(0));

        // Test filtering by "o" which should match John, Doe (Jane), Johnson
        // (Bob)
        test(view.combo).setFilter("o");
        suggestions = test(view.combo).getSuggestions();
        Assertions.assertEquals(3, suggestions.size());
    }

    @Test
    void selectItem_afterManualFilter_selectsCorrectly() {
        // Manually set a filter
        test(view.combo).setFilter("Jane");
        List<String> suggestions = test(view.combo).getSuggestions();
        Assertions.assertEquals(1, suggestions.size());

        // Clear the filter
        test(view.combo).setFilter("");

        // Select an item using selectItem
        test(view.combo).selectItem("Jane");

        // Verify the item was selected correctly
        Assertions.assertEquals(view.items.get(1),
                test(view.combo).getSelected(), "Jane should be selected");
    }
}