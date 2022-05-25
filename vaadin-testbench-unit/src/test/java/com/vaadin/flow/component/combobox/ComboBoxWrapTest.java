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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

public class ComboBoxWrapTest extends UIUnitTest {
    @Override
    protected String scanPackage() {
        return this.getClass().getPackageName();
    }

    ComboBoxView view;
    ComboBoxWrap<ComboBox<ComboBoxView.Name>, ComboBoxView.Name> combo_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ComboBoxView.class);
        view = navigate(ComboBoxView.class);
        combo_ = wrap(view.combo);
    }

    @Test
    void getSuggestionItems_noFilter_allItemsReturned() {
        final List<ComboBoxView.Name> suggestions = combo_.getSuggestionItems();
        Assertions.assertIterableEquals(view.items,suggestions);
    }

    @Test
    void getSuggestions_noFilter_allItemsReturned() {
        final List<ComboBoxView.Name> suggestions = combo_.getSuggestionItems();
        Assertions.assertIterableEquals(view.items,suggestions);
    }

    @Test
    void setFilter_getSuggestions_filterIsApplied() {
        combo_.setFilter("fo");
        final List<String> suggestions = combo_.getSuggestions();
        Assertions.assertEquals(1,suggestions.size());
        Assertions.assertEquals("test-foo",suggestions.get(0));
    }

    @Test
    void selectItem_selectsCorrectItem() {
        Assertions.assertNull(combo_.getSelected());

        combo_.selectItem("test-foo");

        Assertions.assertSame(view.items.get(0), combo_.getSelected());
    }
}
