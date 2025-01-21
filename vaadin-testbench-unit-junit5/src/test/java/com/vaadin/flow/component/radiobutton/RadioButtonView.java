/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.radiobutton;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "checkbox", registerAtStartup = false)
public class RadioButtonView extends Component implements HasComponents {

    RadioButton<String> radioButton = new RadioButton<>("test", "item");

    List<Name> items = Stream.of("foo", "bar", "baz", "jay").map(Name::new)
            .collect(Collectors.toList());

    RadioButtonGroup<Name> radioButtonGroup = new RadioButtonGroup<>();

    public RadioButtonView() {
        add(radioButton);

        radioButtonGroup.setItems(items);
        radioButtonGroup.setItemLabelGenerator(item -> "test-" + item);
        add(radioButtonGroup);
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
