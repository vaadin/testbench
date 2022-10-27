/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.radiobutton;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;

class RadioButtonGroupWrapTest extends UIUnitTest {

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
