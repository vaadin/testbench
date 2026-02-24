/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.select;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ViewPackages
class SelectTesterTest extends UIUnitTest {
    SelectView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(SelectView.class);
        view = navigate(SelectView.class);
    }

    @Test
    void getSuggestionItems_returnsAllItems() {
        final SelectTester<Select<String>, String> select_ = test(view.select);
        assertIterableEquals(view.items, select_.getSuggestionItems());

        final SelectTester<Select<SelectView.Person>, SelectView.Person> person_ = test(
                view.personSelect);
        assertIterableEquals(view.people, person_.getSuggestionItems());
    }

    @Test
    void stringSelect_getSuggestions_valuesEqualItems() {
        final SelectTester<Select<String>, String> select_ = test(view.select);
        assertIterableEquals(view.items, select_.getSuggestions());
    }

    @Test
    void stringSelect_selectItem_selectsCorrectItem() {
        final SelectTester<Select<String>, String> select_ = test(view.select);
        Assertions.assertNull(select_.getSelected());

        select_.selectItem("Fantasy");

        Assertions.assertSame(view.items.get(2), select_.getSelected());

        select_.selectItem(null);

        Assertions.assertNull(select_.getSelected(),
                "Selecting null should clear selection");
    }

    @Test
    void beanSelect_selectItem_selectsCorrectItem() {
        final SelectTester<Select<SelectView.Person>, SelectView.Person> select_ = test(
                view.personSelect);
        Assertions.assertNull(select_.getSelected());

        select_.selectItem("Space");

        Assertions.assertSame(view.people.get(1), select_.getSelected());

        select_.selectItem(null);

        Assertions.assertNull(select_.getSelected(),
                "Selecting null should clear selection");
    }
}
