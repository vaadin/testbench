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
package com.vaadin.flow.component.contextmenu;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "contextmenu", registerAtStartup = false)
public class ContextMenuView extends Component implements HasComponents {

    final MenuItem checkableItem;
    final MenuItem nestedCheckableItem;
    final List<String> clickedItems = new ArrayList<>();
    final ContextMenu menu;

    public ContextMenuView() {
        Span assignee = new Span();
        menu = new ContextMenu();
        menu.setTarget(assignee);
        menu.addItem("Foo", ev -> clickedItems.add("Foo"));
        menu.addItem("Bar", ev -> clickedItems.add("Bar"));
        menu.addItem("Text", ev -> clickedItems.add("Text"));
        menu.addItem("Duplicated", ev -> clickedItems.add("Duplicated 1"));
        menu.addItem("Duplicated", ev -> clickedItems.add("Duplicated 2"));
        menu.addItem(new VerticalLayout(new Div("Component Item")),
                click -> clickedItems.add("Component Item"));
        checkableItem = menu.addItem("Checkable",
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

        add(assignee);
    }

    @Tag("span")
    private static class Span extends Component {

    }
}
