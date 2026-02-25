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
package com.vaadin.flow.component.combobox;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
public class ComboBoxTesterTest extends BrowserlessTest {

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
