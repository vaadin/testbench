/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.combobox;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "multicombo", registerAtStartup = false)
public class MultiSelectComboBoxView extends Component implements HasComponents {

    MultiSelectComboBox<Name> combo;
    List<Name> items = Arrays.asList(new Name("foo"), new Name("bar"));

    public MultiSelectComboBoxView() {
        combo = new MultiSelectComboBox<>("TestBox");
        combo.setItems(items);
        combo.setItemLabelGenerator(item -> "test-" + item.toString());
        add(combo);
    }

    public static class Name {
        String name;

        public Name(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
