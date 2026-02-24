/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
