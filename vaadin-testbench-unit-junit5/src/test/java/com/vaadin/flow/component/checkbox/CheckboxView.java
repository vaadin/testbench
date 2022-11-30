/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "checkbox", registerAtStartup = false)
public class CheckboxView extends Component implements HasComponents {

    Checkbox checkbox = new Checkbox();

    Map<String, Name> items = Stream.of("foo", "bar", "baz", "jay")
            .collect(Collectors.toMap(Function.identity(), Name::new,
                    (a, b) -> a, LinkedHashMap::new));

    CheckboxGroup<Name> checkboxGroup = new CheckboxGroup<>();

    public CheckboxView() {
        add(checkbox);

        checkboxGroup.setItems(items.values());
        checkboxGroup.setItemLabelGenerator(item -> "test-" + item);
        add(checkboxGroup);
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
