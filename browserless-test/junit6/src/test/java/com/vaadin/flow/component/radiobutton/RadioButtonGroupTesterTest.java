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
package com.vaadin.flow.component.radiobutton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.flow.router.RouteConfiguration;

class RadioButtonGroupTesterTest extends BrowserlessTest {

    RadioButtonView view;
    RadioButtonGroupTester<RadioButtonGroup<RadioButtonView.Name>, RadioButtonView.Name> buttonGroup_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RadioButtonView.class);
        view = navigate(RadioButtonView.class);
        buttonGroup_ = test(view.radioButtonGroup);
    }

    @Test
    void selectItem_selectCorrectItem() {
        buttonGroup_.selectItem("test-bar");
        Assertions.assertEquals(view.items.get(1), buttonGroup_.getSelected());

        buttonGroup_.selectItem("test-jay");
        Assertions.assertEquals(view.items.get(3), buttonGroup_.getSelected());
    }

    @Test
    void deselectAll_noItemsSelected() {
        view.radioButtonGroup.setValue(view.items.get(0));

        buttonGroup_.deselectItem();
        RadioButtonView.Name selectedItem = buttonGroup_.getSelected();
        Assertions.assertNull(selectedItem,
                "Expecting no selection, but got " + selectedItem);
    }

    @Test
    void selectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> buttonGroup_.selectItem("jay"));
    }

    @Test
    void selectItem_itemDisabled_throws() {
        view.radioButtonGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        buttonGroup_.selectItem("test-bar");
        buttonGroup_.selectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> buttonGroup_.selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> buttonGroup_.selectItem("test-jay"));
    }

    @Test
    void readOnly_isNotUsable() {
        view.radioButtonGroup.setReadOnly(true);

        Assertions.assertThrows(IllegalStateException.class,
                () -> buttonGroup_.selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> buttonGroup_.deselectItem());

    }

}
