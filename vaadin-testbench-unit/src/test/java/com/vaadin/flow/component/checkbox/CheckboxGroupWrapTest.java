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

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CheckboxView.class);
        view = navigate(CheckboxView.class);
    }

    @Test
    void selectItem_selectCorrectItem() {
        wrap(view.checkboxGroup).selectItem("test-bar");
        assertContainsExactlyInAnyOrder(Set.of(view.items.get("bar")),
                wrap(view.checkboxGroup).getSelected());

        wrap(view.checkboxGroup).selectItem("test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                wrap(view.checkboxGroup).getSelected());
    }

    @Test
    void selectItems_multipleItems_itemsSelected() {
        wrap(view.checkboxGroup).selectItems("test-bar", "test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                wrap(view.checkboxGroup).getSelected());
    }

    @Test
    void selectItems_collectionOfItems_itemsSelected() {
        wrap(view.checkboxGroup).selectItems(List.of("test-bar", "test-jay"));
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("bar"), view.items.get("jay")),
                wrap(view.checkboxGroup).getSelected());
    }

    @Test
    void selectAll_allItemSelected() {
        wrap(view.checkboxGroup).selectAll();
        assertContainsExactlyInAnyOrder(view.items.values(),
                wrap(view.checkboxGroup).getSelected());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void deselectItem_deselectCorrectItem() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        wrap(view.checkboxGroup).deselectItem("test-bar");
        Assertions.assertEquals(
                Set.of(view.items.get("foo"), view.items.get("baz"),
                        view.items.get("jay")),
                wrap(view.checkboxGroup).getSelected());

        wrap(view.checkboxGroup).deselectItem("test-jay");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                wrap(view.checkboxGroup).getSelected());

    }

    @Test
    void deselectItems_multipleItems_itemsDeselected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        wrap(view.checkboxGroup).deselectItems("test-jay", "test-bar");
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                wrap(view.checkboxGroup).getSelected());
    }

    @Test
    void deselectItems_collectionOfItems_itemsDeselected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        wrap(view.checkboxGroup).deselectItems(List.of("test-jay", "test-bar"));
        assertContainsExactlyInAnyOrder(
                Set.of(view.items.get("foo"), view.items.get("baz")),
                wrap(view.checkboxGroup).getSelected());
    }

    @Test
    void deselectAll_noItemsSelected() {
        view.checkboxGroup.setValue(new HashSet<>(view.items.values()));

        wrap(view.checkboxGroup).deselectAll();
        Set<CheckboxView.Name> selectedItems = wrap(view.checkboxGroup)
                .getSelected();
        Assertions.assertTrue(selectedItems.isEmpty(),
                "Expecting no elements to be selected, but got "
                        + selectedItems);
    }

    @Test
    void selectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> wrap(view.checkboxGroup).selectItem("jay"));
    }

    @Test
    void deselectItem_notExisting_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> wrap(view.checkboxGroup).deselectItem("jay"));
    }

    @Test
    void selectItem_itemDisabled_throws() {
        view.checkboxGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        wrap(view.checkboxGroup).selectItem("test-bar");
        wrap(view.checkboxGroup).selectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).selectItem("test-jay"));
    }

    @Test
    void deselectItem_itemDisabled_throws() {
        view.checkboxGroup
                .setItemEnabledProvider(n -> n.getName().startsWith("b"));

        // Items enabled, should work
        wrap(view.checkboxGroup).deselectItem("test-bar");
        wrap(view.checkboxGroup).deselectItem("test-baz");

        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).deselectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).deselectItem("test-jay"));
    }

    @Test
    void readOnly_isNotUsable() {
        view.checkboxGroup.setReadOnly(true);

        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).selectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).deselectItem("test-foo"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).selectAll());
        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.checkboxGroup).deselectAll());

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
