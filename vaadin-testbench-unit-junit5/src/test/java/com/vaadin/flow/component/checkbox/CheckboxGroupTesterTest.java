/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
class CheckboxGroupTesterTest extends UIUnitTest {

    CheckboxView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CheckboxView.class);
        view = navigate(CheckboxView.class);
    }

    @Test
    void selectItem_selectCorrectItem() {
        test(view.checkboxGroup).selectItem("test-bar");
        assertContainsExactlyInAnyOrder(Set.of(view.items.get("bar")),
                test(view.checkboxGroup).getSelected());

        test(view.checkboxGroup).selectItem("test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                test(view.checkboxGroup).getSelected());
    }

    @Test
    void selectItems_multipleItems_itemsSelected() {
        test(view.checkboxGroup).selectItems("test-bar", "test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                test(view.checkboxGroup).getSelected());
    }

    @Test
    void selectItems_collectionOfItems_itemsSelected() {
        test(view.checkboxGroup).selectItems(List.of("test-bar", "test-jay"));
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                test(view.checkboxGroup).getSelected());
    }

    @Test
    void selectAll_allItemSelected() {
        test(view.checkboxGroup).selectAll();
        assertContainsExactlyInAnyOrder(view.items.values(),
                test(view.checkboxGroup).getSelected());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void deselectItem_deselectCorrectItem() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        test(view.checkboxGroup).deselectItem("test-bar");
        Assertions.assertEquals(
                Set.of(view.items.get("foo"), view.items.get("baz"),
                        view.items.get("jay")),
                test(view.checkboxGroup).getSelected());

        test(view.checkboxGroup).deselectItem("test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                test(view.checkboxGroup).getSelected());

    }

    @Test
    void deselectItems_multipleItems_itemsDeselected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        test(view.checkboxGroup).deselectItems("test-jay", "test-bar");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                test(view.checkboxGroup).getSelected());
    }

    @Test
    void deselectItems_collectionOfItems_itemsDeselected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        test(view.checkboxGroup).deselectItems(List.of("test-jay", "test-bar"));
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                test(view.checkboxGroup).getSelected());
    }

    @Test
    void deselectAll_noItemsSelected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        test(view.checkboxGroup).deselectAll();
        Set<CheckboxView.Name> selectedItems = test(view.checkboxGroup)
                .getSelected();
        Assertions.assertTrue(selectedItems.isEmpty(),
                "Expecting no elements to be selected, but got "
                        + selectedItems);
    }

    @Test
    void selectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> test(view.checkboxGroup).selectItem("jay"));
    }

    @Test
    void deselectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> test(view.checkboxGroup).deselectItem("jay"));
    }

    @Test
    void selectItem_itemDisabled_throws() {
        view.checkboxGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        test(view.checkboxGroup).selectItem("test-bar");
        test(view.checkboxGroup).selectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).selectItem("test-jay"));
    }

    @Test
    void deselectItem_itemDisabled_throws() {
        view.checkboxGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        test(view.checkboxGroup).deselectItem("test-bar");
        test(view.checkboxGroup).deselectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).deselectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).deselectItem("test-jay"));
    }

    @Test
    void readOnly_isNotUsable() {
        view.checkboxGroup.setReadOnly(true);

        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).deselectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).selectAll());
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.checkboxGroup).deselectAll());

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
