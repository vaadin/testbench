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
package com.vaadin.flow.component.menubar;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;
import com.vaadin.browserless.internal.PrettyPrintTreeKt;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.contextmenu.MenuItem;

/**
 * Tester for MenuBar components.
 *
 * @param <T>
 *            component type
 */
@Tests(MenuBar.class)
public class MenuBarTester<T extends MenuBar> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MenuBarTester(T component) {
        super(component);
    }

    /**
     * Simulates a click on the item that matches the given text.
     *
     * For nested menu item provide the text of each menu item in the hierarchy.
     *
     * The path to the menu item must reflect what is seen in the browser,
     * meaning that hidden items are ignored.
     *
     * <pre>
     * {@code
     *
     * menu.addItem("Preview", event -> {
     * });
     * var subMenu = menu.addItem("Share").getSubMenu();
     * subMenu.addItem("Copy link", event -> {
     * });
     * subMenu.addItem("Email", event -> {
     * });
     *
     * // clicks top level menu item with text Preview
     * wrapper.clickItem("Preview");
     *
     * // clicks nested menu item with text Email
     * wrapper.clickItem("Share", "Email");
     * }
     * </pre>
     *
     * @param topLevelText
     *            the text content of the top level menu item, not
     *            {@literal null}.
     * @param nestedItemsText
     *            text content of the nested menu items
     * @throws IllegalArgumentException
     *             if the provided text does not identify a menu item.
     * @throws IllegalStateException
     *             if the item at given path is not usable.
     */
    public void clickItem(String topLevelText, String... nestedItemsText) {
        ensureComponentIsUsable();
        MenuItem menuItem = findMenuItemByPath(topLevelText, nestedItemsText);
        clickMenuItem(menuItem);
    }

    /**
     * Simulates a click on the item at the given position in the menu.
     *
     * For nested menu item provide the position of each sub menu that should be
     * navigated to reach the request item.
     *
     * The position reflects what is seen in the browser, so hidden items are
     * ignored.
     *
     * <pre>
     * {@code
     *
     * menu.addItem("Preview", event -> {
     * });
     * var subMenu = menu.addItem("Share").getSubMenu();
     * subMenu.addItem("Copy link", event -> {
     * });
     * subMenu.addItem("Email", event -> {
     * });
     *
     * // clicks top level "Preview" menu item at position 0
     * wrapper.clickItem(0);
     *
     * // clicks then nested menu item at position 1 "Email" through the
     * // item "Share" at position 1
     * wrapper.clickItem(1, 1);
     * }
     * </pre>
     *
     * @param topLevelPosition
     *            the zero-based position of the item in the menu, as it will be
     *            seen in the browser.
     * @param nestedItemsPositions
     *            the zero-based position of the nested items, relative to the
     *            parent menu
     * @throws IllegalArgumentException
     *             if the provided position does not identify a menu item.
     * @throws IllegalStateException
     *             if the item at given position is not usable.
     */
    public void clickItem(int topLevelPosition, int... nestedItemsPositions) {
        ensureComponentIsUsable();
        MenuItem menuItem = findMenuItemByPath(topLevelPosition,
                nestedItemsPositions);
        clickMenuItem(menuItem);
    }

    /**
     * Checks if the checkable menu item matching given text is checked.
     *
     * For nested menu item provide the text of each menu item in the hierarchy.
     *
     * The path to the menu item must reflect what is seen in the browser,
     * meaning that hidden items are ignored.
     *
     * <pre>
     * {@code
     *
     * menu.addItem("Preview", event -> {
     * }).setCheckable(true);
     * var subMenu = menu.addItem("Share").getSubMenu();
     * subMenu.addItem("Copy link", event -> {
     * }).setCheckable(true);
     * subMenu.addItem("Email", event -> {
     * }).setCheckable(true);
     *
     * wrapper.isItemChecked("Preview");
     *
     * wrapper.isItemChecked("Share", "Email");
     * }
     * </pre>
     *
     * @param topLevelText
     *            the text content of the top level menu item, not
     *            {@literal null}.
     * @param nestedItemsText
     *            text content of the nested menu items
     * @return {@literal true} if the item at given path is checked, otherwise
     *         {@literal false}.
     * @throws IllegalArgumentException
     *             if the provided text does not identify a menu item or if the
     *             menu item is not checkable.
     * @throws IllegalStateException
     *             if the item at given path is not usable.
     */
    public boolean isItemChecked(String topLevelText,
            String... nestedItemsText) {
        ensureComponentIsUsable();
        MenuItem menuItem = findMenuItemByPath(topLevelText, nestedItemsText);
        if (!menuItem.isCheckable()) {
            String fullPath = topLevelText + ((nestedItemsText.length > 0)
                    ? " / " + String.join(" / ", nestedItemsText)
                    : "");
            throw new IllegalArgumentException("Menu item at position "
                    + fullPath + " is not a checkable menu item");
        }
        return menuItem.isChecked();
    }

    /**
     * Checks if the checkable menu item at given position is checked.
     *
     * For nested menu item provide the position of each sub menu that should be
     * navigated to reach the requested item.
     *
     * The position reflects what is seen in the browser, so hidden items are
     * ignored.
     *
     * <pre>
     * {@code
     *
     * menu.addItem("Preview", event -> {
     * }).setCheckable(true);
     * var subMenu = menu.addItem("Share").getSubMenu();
     * subMenu.addItem("Copy link", event -> {
     * }).setCheckable(true);
     * subMenu.addItem("Email", event -> {
     * }).setCheckable(true);
     *
     * // checks top level "Preview" menu item at position 0
     * wrapper.isItemChecked(0);
     *
     * // checks nested menu item at position 1 "Email" through the
     * // item "Share" at position 1
     * wrapper.isItemChecked(1, 1);
     * }
     * </pre>
     *
     * @param topLevelPosition
     *            the zero-based position of the item in the menu, as it will be
     *            seen in the browser.
     * @param nestedItemsPositions
     *            the zero-based position of the nested items, relative to the
     *            parent menu
     * @throws IllegalArgumentException
     *             if the provided position does not identify a menu item or if
     *             the menu item is not checkable.
     * @throws IllegalStateException
     *             if the item at given position is not usable.
     */
    public boolean isItemChecked(int topLevelPosition,
            int... nestedItemsPositions) {
        ensureComponentIsUsable();
        MenuItem menuItem = findMenuItemByPath(topLevelPosition,
                nestedItemsPositions);
        if (!menuItem.isCheckable()) {
            String fullPath = IntStream
                    .concat(IntStream.of(topLevelPosition),
                            IntStream.of(nestedItemsPositions))
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(" / "));
            throw new IllegalArgumentException("Menu item at position "
                    + fullPath + " is not a checkable menu item");
        }
        return menuItem.isChecked();
    }

    private MenuItem findMenuItemByPath(String topLevelText,
            String... nestedItemsText) {
        MenuItem menuItem = findMenuItem(getComponent().getItems(),
                topLevelText, null);
        if (nestedItemsText.length > 0) {
            String path = topLevelText + " / "
                    + String.join(" / ", nestedItemsText);
            for (String text : nestedItemsText) {
                if (menuItem.isParentItem()) {
                    menuItem = findMenuItem(menuItem.getSubMenu().getItems(),
                            text, path);
                } else {
                    throw new IllegalArgumentException("Menu item with text "
                            + menuItem.getText()
                            + " has no children. Make sure that the path is correct: "
                            + path);
                }
            }
        }
        return menuItem;
    }

    private MenuItem findMenuItem(List<MenuItem> allItems, String text,
            String fullPath) {
        List<MenuItem> items = allItems.stream()
                .filter(item -> text.equals(item.getText()))
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot find menu item with text " + text
                            + (fullPath != null ? " on path " + fullPath : ""));
        } else if (items.size() > 1) {
            throw new IllegalStateException(
                    "Expecting a single menu item with text " + text
                            + " but found " + items.size()
                            + (fullPath != null ? " on path " + fullPath : ""));
        }
        MenuItem menuItem = items.get(0);
        ensureMenuItemIsUsable(menuItem, fullPath);
        return menuItem;
    }

    private MenuItem findMenuItemByPath(int topLevelPosition,
            int... nestedItemsPositions) {

        MenuItem menuItem = findMenuItemByPosition(getComponent().getItems(),
                topLevelPosition, null);
        if (nestedItemsPositions.length > 0) {
            StringBuilder path = new StringBuilder().append(topLevelPosition);
            for (int position : nestedItemsPositions) {
                if (menuItem.isParentItem()) {
                    path.append(" / ").append(position);
                    menuItem = findMenuItemByPosition(
                            menuItem.getSubMenu().getItems(), position,
                            path.toString());
                } else {
                    throw new IllegalArgumentException("Menu item with text "
                            + menuItem.getText()
                            + " has no children. Make sure that the path is correct: "
                            + path);
                }
            }
        }
        return menuItem;
    }

    private MenuItem findMenuItemByPosition(List<MenuItem> allItems,
            int position, String fullPath) {
        MenuItem menuItem = allItems.stream().filter(Component::isVisible)
                .skip(position).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find menu item at position " + fullPath));
        ensureMenuItemIsUsable(menuItem, fullPath);
        return menuItem;
    }

    private void ensureMenuItemIsUsable(MenuItem menuItem, String fullPath) {
        if (!menuItem.isEnabled() || !menuItem.isVisible()) {
            throw new IllegalStateException(
                    "Menu item " + fullPath + " is not usable. "
                            + PrettyPrintTreeKt.toPrettyTree(menuItem));
        }
    }

    private void clickMenuItem(MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            menuItem.setChecked(!menuItem.isChecked());
        }
        ComponentUtil.fireEvent(menuItem, new ClickEvent<>(menuItem, true, 0, 0,
                0, 0, 1, 0, false, false, false, false));
    }

}
