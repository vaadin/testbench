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
package com.vaadin.flow.component.listbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ViewPackages
class ListBoxTesterTest extends BrowserlessTest {
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
