/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.contextmenu;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class ContextMenuWrapTest extends UIUnitTest {

    ContextMenuView view;
    ContextMenuWrap<ContextMenu> menu_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ContextMenuView.class);
        view = navigate(ContextMenuView.class);
        menu_ = wrap(view.menu);
    }

    @Test
    void openCloseMenu_menuIsAttachedAndDetached() {
        Assertions.assertFalse(view.menu.isAttached(),
                "closed context menu should not be attached to the UI");
        menu_.open();
        Assertions.assertTrue(view.menu.isAttached(),
                "context menu should be attached to the UI, but was not");

        menu_.close();
        Assertions.assertFalse(view.menu.isAttached(),
                "context menu should be detached from the UI, but was not");
    }

    @Test
    void openMenu_alreadyOpen_throws() {
        menu_.open();
        Assertions.assertThrows(IllegalStateException.class, menu_::open);
    }

    @Test
    void closeMenu_menuNotOpened_throws() {
        Assertions.assertThrows(IllegalStateException.class, menu_::close);
    }

    @Test
    void clickItem_byText_actionExecuted() {
        menu_.open();

        menu_.clickItem("Foo");
        Assertions.assertIterableEquals(List.of("Foo"), view.clickedItems);

        menu_.clickItem("Text");
        menu_.clickItem("Bar");
        Assertions.assertIterableEquals(List.of("Foo", "Text", "Bar"),
                view.clickedItems);
    }

    @Test
    void clickItem_notExisting_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem("XYZ"));
    }

    @Test
    void clickItem_menuNotOpened_throws() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.clickItem("Bar"));
    }

    @Test
    void clickItem_multipleMatches_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.clickItem("Duplicated"));
    }

    @Test
    void clickItem_checkable_checkStatusChanges() {
        menu_.open();

        menu_.clickItem("Checkable");
        Assertions.assertIterableEquals(List.of("Checkable"),
                view.clickedItems);
        Assertions.assertTrue(view.checkableItem.isChecked(),
                "Item should be checked but was not");

        menu_.clickItem("Checkable");
        Assertions.assertIterableEquals(List.of("Checkable", "Checkable"),
                view.clickedItems);
        Assertions.assertFalse(view.checkableItem.isChecked(),
                "Item should be checked but was not");
    }

    @Test
    void clickItem_disabled_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.clickItem("Disabled"));
        Assertions.assertTrue(view.clickedItems.isEmpty(),
                "Listener should not have been notified");
    }

    @Test
    void clickItem_hidden_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.clickItem("Hidden"));
        Assertions.assertTrue(view.clickedItems.isEmpty(),
                "Listener should not have been notified");
    }

    @Test
    void clickItem_nested_executeAction() {
        menu_.open();

        menu_.clickItem("Hierarchical", "Level2");
        Assertions.assertIterableEquals(List.of("Hierarchical / Level2"),
                view.clickedItems);

        view.clickedItems.clear();
        menu_.clickItem("Hierarchical", "NestedSubMenu", "Level3");
        Assertions.assertIterableEquals(
                List.of("Hierarchical / NestedSubMenu / Level3"),
                view.clickedItems);
    }

    @Test
    void clickItem_nestedWrongPath_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem("Foo", "Bar"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem("Hierarchical", "Level3"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> menu_
                .clickItem("Hierarchical", "NestedSubMenu", "Level3Bis"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem("Hierarchical", "NestedSubMenu", "Level3",
                        "Level4"));
    }

    @Test
    void clickItem_nestedNotUsableParent_throws() {
        Assertions.assertThrows(IllegalStateException.class, () -> menu_
                .clickItem("Hierarchical", "NestedDisabled", "Level3"));
        Assertions.assertThrows(IllegalStateException.class, () -> menu_
                .clickItem("Hierarchical", "NestedInvisible", "Level3"));

    }

    @Test
    void clickItem_byIndex_executesAction() {
        menu_.open();

        menu_.clickItem(0);
        Assertions.assertIterableEquals(List.of("Foo"), view.clickedItems);

        menu_.clickItem(2);
        menu_.clickItem(1);
        Assertions.assertIterableEquals(List.of("Foo", "Text", "Bar"),
                view.clickedItems);

    }

    @Test
    void clickItem_byInvalidIndexes_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem(1, 2));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem(7, 5));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem(7, 1, 5));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.clickItem(7, 1, 0, 4));
    }

    @Test
    void clickItem_byNestedIndexNotUsableParent_throws() {
        // Hierarchical / NestedDisabled / Level3
        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.clickItem(7, 2, 0));
    }

    @Test
    void clickItem_byNestedIndexes_executesAction() {
        menu_.open();

        menu_.clickItem(7, 0);
        Assertions.assertIterableEquals(List.of("Hierarchical / Level2"),
                view.clickedItems);

        view.clickedItems.clear();
        menu_.clickItem(7, 1, 0);
        Assertions.assertIterableEquals(
                List.of("Hierarchical / NestedSubMenu / Level3"),
                view.clickedItems);

    }

    @Test
    void isItemChecked_byText_getCheckedStatus() {
        menu_.open();

        Assertions.assertFalse(menu_.isItemChecked("Checkable"),
                "Checkable item should not be checked by default, but result is true");

        view.checkableItem.setChecked(true);
        Assertions.assertTrue(menu_.isItemChecked("Checkable"),
                "Checkable item is checked, but result is false");

        view.checkableItem.setChecked(false);
        Assertions.assertFalse(menu_.isItemChecked("Checkable"),
                "Checkable item is not checked, but result is true");
    }

    @Test
    void isItemChecked_byIndex_getCheckedStatus() {
        menu_.open();

        Assertions.assertFalse(menu_.isItemChecked(5),
                "Checkable item should not be checked by default, but result is true");

        view.checkableItem.setChecked(true);
        Assertions.assertTrue(menu_.isItemChecked(5),
                "Checkable item is checked, but result is false");

        view.checkableItem.setChecked(false);
        Assertions.assertFalse(menu_.isItemChecked(5),
                "Checkable item is not checked, but result is true");
    }

    @Test
    void isItemChecked_nestedByText_getCheckedStatus() {
        menu_.open();

        Assertions.assertTrue(
                menu_.isItemChecked("Hierarchical", "Nested Checkable"),
                "Checkable item should be checked by default, but result is false");

        view.nestedCheckableItem.setChecked(false);
        Assertions.assertFalse(
                menu_.isItemChecked("Hierarchical", "Nested Checkable"),
                "Checkable item is not checked, but result is true");

        view.nestedCheckableItem.setChecked(true);
        Assertions.assertTrue(
                menu_.isItemChecked("Hierarchical", "Nested Checkable"),
                "Checkable item is checked, but result is false");
    }

    @Test
    void isItemChecked_nestedByIndex_getCheckedStatus() {
        menu_.open();

        Assertions.assertTrue(menu_.isItemChecked(7, 2),
                "Checkable item should be checked by default, but result is false");

        view.nestedCheckableItem.setChecked(false);
        Assertions.assertFalse(menu_.isItemChecked(7, 2),
                "Checkable item is not checked, but result is true");

        view.nestedCheckableItem.setChecked(true);
        Assertions.assertTrue(menu_.isItemChecked(7, 2),
                "Checkable item is checked, but result is false");
    }

    @Test
    void isItemChecked_notCheckableItem_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.isItemChecked("Bar"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.isItemChecked("Hierarchical", "Level2"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.isItemChecked(0));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.isItemChecked(7, 1));
    }

    @Test
    void isItemChecked_menuNotOpened_throws() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.isItemChecked("Checkable"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> menu_.isItemChecked(5));
    }

    @Test
    void isItemChecked_notExisting_throws() {
        menu_.open();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.isItemChecked("XYZ"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> menu_.isItemChecked(22));
    }

}
