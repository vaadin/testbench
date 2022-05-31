/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.select;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

import static org.junit.jupiter.api.Assertions.*;

class SelectWrapTest extends UIUnitTest {
    SelectView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(SelectView.class);
        view = navigate(SelectView.class);
    }

    @Override
    protected String scanPackage() {
        return this.getClass().getPackageName();
    }

    @Test
    void getSuggestionItems_returnsAllItems() {
        final SelectWrap<Select<String>, String> select_ = wrap(view.select);
        assertIterableEquals(view.items, select_.getSuggestionItems());

        final SelectWrap<Select<SelectView.Person>, SelectView.Person> person_ = wrap(
                view.personSelect);
        assertIterableEquals(view.people, person_.getSuggestionItems());
    }

    @Test
    void stringSelect_getSuggestions_valuesEqualItems() {
        final SelectWrap<Select<String>, String> select_ = wrap(view.select);
        assertIterableEquals(view.items, select_.getSuggestions());
    }

    @Test
    void stringSelect_selectItem_selectsCorrectItem() {
        final SelectWrap<Select<String>, String> select_ = wrap(view.select);
        Assertions.assertNull(select_.getSelected());

        select_.selectItem("Fantasy");

        Assertions.assertSame(view.items.get(2), select_.getSelected());

        select_.selectItem(null);

        Assertions.assertNull(select_.getSelected(),
                "Selecting null should clear selection");
    }

    @Test
    void beanSelect_selectItem_selectsCorrectItem() {
        final SelectWrap<Select<SelectView.Person>, SelectView.Person> select_ = wrap(
                view.personSelect);
        Assertions.assertNull(select_.getSelected());

        select_.selectItem("Space");

        Assertions.assertSame(view.people.get(1), select_.getSelected());

        select_.selectItem(null);

        Assertions.assertNull(select_.getSelected(),
                "Selecting null should clear selection");
    }
}