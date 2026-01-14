/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.select;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "select", registerAtStartup = false)
public class SelectView extends Component implements HasComponents {
    Select<String> select;
    Select<Person> personSelect;
    List<Person> people = Arrays.asList(new Person("John", "Doe"),
            new Person("Space", "Cat"));
    List<String> items = Arrays.asList("Good", "Omens", "Fantasy", "Drawing");

    public SelectView() {
        select = new Select<>();
        select.setItems(items);

        personSelect = new Select<>();
        personSelect.setItemLabelGenerator(Person::getFirst);
        personSelect.setItems(people);

        add(select, personSelect);
    }

    static class Person {
        String first;
        String last;

        public Person(String first, String last) {
            this.first = first;
            this.last = last;
        }

        public String getFirst() {
            return first;
        }

        public String getLast() {
            return last;
        }
    }
}
