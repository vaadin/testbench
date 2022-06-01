/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.checkbox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class CheckboxGroupWrapTest extends UIUnitTest {

    CheckboxView view;
    CheckboxGroupWrap<CheckboxGroup<CheckboxView.Name>, CheckboxView.Name> checkboxGroup_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CheckboxView.class);
        view = navigate(CheckboxView.class);
        checkboxGroup_ = wrap(view.checkboxGroup);
    }

    @Test
    void selectItem_selectCorrectItem() {
        checkboxGroup_.selectItem("test-bar");
        assertContainsExactlyInAnyOrder(Set.of(view.items.get("bar")),
                checkboxGroup_.getSelected());

        checkboxGroup_.selectItem("test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                checkboxGroup_.getSelected());
    }

    @Test
    void selectItems_multipleItems_itemsSelected() {
        checkboxGroup_.selectItems("test-bar", "test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                checkboxGroup_.getSelected());
    }

    @Test
    void selectItems_collectionOfItems_itemsSelected() {
        checkboxGroup_.selectItems(List.of("test-bar", "test-jay"));
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                checkboxGroup_.getSelected());
    }

    @Test
    void selectAll_allItemSelected() {
        checkboxGroup_.selectAll();
        assertContainsExactlyInAnyOrder(view.items.values(),
                checkboxGroup_.getSelected());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void deselectItem_deselectCorrectItem() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        checkboxGroup_.deselectItem("test-bar");
        Assertions
                .assertEquals(
                        Set.of(view.items.get("foo"), view.items.get("baz"),
                                view.items.get("jay")),
                        checkboxGroup_.getSelected());

        checkboxGroup_.deselectItem("test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                checkboxGroup_.getSelected());

    }

    @Test
    void deselectItems_multipleItems_itemsDeselected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        checkboxGroup_.deselectItems("test-jay", "test-bar");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                checkboxGroup_.getSelected());
    }

    @Test
    void deselectItems_collectionOfItems_itemsDeselected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        checkboxGroup_.deselectItems(List.of("test-jay", "test-bar"));
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                checkboxGroup_.getSelected());
    }

    @Test
    void deselectAll_noItemsSelected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        checkboxGroup_.deselectAll();
        Set<CheckboxView.Name> selectedItems = checkboxGroup_.getSelected();
        Assertions.assertTrue(selectedItems.isEmpty(),
                "Expecting no elements to be selected, but got "
                        + selectedItems);
    }

    @Test
    void selectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> checkboxGroup_.selectItem("jay"));
    }

    @Test
    void deselectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> checkboxGroup_.deselectItem("jay"));
    }

    @Test
    void selectItem_itemDisabled_throws() {
        view.checkboxGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        checkboxGroup_.selectItem("test-bar");
        checkboxGroup_.selectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.selectItem("test-jay"));
    }

    @Test
    void deselectItem_itemDisabled_throws() {
        view.checkboxGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        checkboxGroup_.deselectItem("test-bar");
        checkboxGroup_.deselectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.deselectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.deselectItem("test-jay"));
    }

    @Test
    void readOnly_isNotUsable() {
        view.checkboxGroup.setReadOnly(true);

        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.deselectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.selectAll());
        Assertions.assertThrows(IllegalStateException.class,
                () -> checkboxGroup_.deselectAll());

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void assertContainsExactlyInAnyOrder(Collection expected,
            Collection actual) {
        Assertions.assertEquals(expected.size(), actual.size(), "Expected "
                + expected.size() + " elements, but got " + actual.size());
        Assertions.assertTrue(
                expected.containsAll(actual) && actual.containsAll(expected));
    }

}
