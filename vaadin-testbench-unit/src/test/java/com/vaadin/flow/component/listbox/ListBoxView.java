/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.listbox;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "list-boxes", registerAtStartup = false)
public class ListBoxView extends Component implements HasComponents {
    ListBox<String> listBox;
    MultiSelectListBox<String> multiSelectListBox;

    List<String> selection = Arrays.asList("one", "two");

    public ListBoxView() {
        listBox = new ListBox<>();
        listBox.setItems(selection);

        multiSelectListBox = new MultiSelectListBox<>();
        multiSelectListBox.setItems(selection);

        add(listBox, multiSelectListBox);
    }
}
