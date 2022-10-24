/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.menubar;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "contextmenu", registerAtStartup = false)
public class MenuBarView extends Component implements HasComponents {

    final MenuItem checkableItem;
    final MenuItem nestedCheckableItem;
    final List<String> clickedItems = new ArrayList<>();
    final MenuBar menu;

    public MenuBarView() {
        Span assignee = new Span();
        menu = new MenuBar();
        menu.addItem("Foo", ev -> clickedItems.add("Foo"));
        menu.addItem("Bar", ev -> clickedItems.add("Bar"));
        menu.addItem("Text", ev -> clickedItems.add("Text"));
        menu.addItem("Duplicated", ev -> clickedItems.add("Duplicated 1"));
        menu.addItem("Duplicated", ev -> clickedItems.add("Duplicated 2"));
        SubMenu checkablesSubMenu = menu.addItem("Checkables").getSubMenu();
        checkableItem = checkablesSubMenu.addItem("Checkable",
                ev -> clickedItems.add("Checkable"));
        checkableItem.setCheckable(true);
        menu.addItem("Disabled", ev -> clickedItems.add("Disabled"))
                .setEnabled(false);
        menu.addItem("Hidden", ev -> clickedItems.add("Hidden"))
                .setVisible(false);

        SubMenu subMenu = menu.addItem("Hierarchical").getSubMenu();
        subMenu.addItem("Level2",
                ev -> clickedItems.add("Hierarchical / Level2"));
        subMenu.addItem("NestedSubMenu").getSubMenu().addItem("Level3",
                ev -> clickedItems
                        .add("Hierarchical / NestedSubMenu / Level3"));
        nestedCheckableItem = subMenu.addItem("Nested Checkable");
        nestedCheckableItem.setCheckable(true);
        nestedCheckableItem.setChecked(true);
        MenuItem nestedDisabled = subMenu.addItem("NestedDisabled");
        nestedDisabled.setEnabled(false);
        nestedDisabled.getSubMenu().addItem("Level3", ev -> clickedItems
                .add("Hierarchical / NestedDisabled / Level3"));
        MenuItem nestedInvisible = subMenu.addItem("NestedInvisible");
        nestedInvisible.setVisible(false);
        nestedInvisible.getSubMenu().addItem("Level3", ev -> clickedItems
                .add("Hierarchical / NestedInvisible / Level3"));

        add(menu);
    }

    @Tag("span")
    private static class Span extends Component {

    }
}
