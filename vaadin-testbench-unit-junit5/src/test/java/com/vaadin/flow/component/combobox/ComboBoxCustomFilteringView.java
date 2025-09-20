/**
 * Copyright (C) 2000-2025 Vaadin Ltd
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
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "combo-custom-filter", registerAtStartup = false)
public class ComboBoxCustomFilteringView extends Component
        implements HasComponents {

    ComboBox<Person> combo;
    List<Person> items = Arrays.asList(new Person("John", "Smith"),
            new Person("Jane", "Doe"), new Person("Bob", "Johnson"));

    public ComboBoxCustomFilteringView() {
        combo = new ComboBox<>("Person Selector");

        // Custom filter that matches both first and last name
        combo.setItems((person, filterText) -> {
            if (filterText == null || filterText.isEmpty()) {
                return true;
            }
            String lowerFilter = filterText.toLowerCase();
            // Custom logic: match if filter text is found in either first or
            // last name
            return person.getFirstName().toLowerCase().contains(lowerFilter)
                    || person.getLastName().toLowerCase().contains(lowerFilter);
        }, items);

        // Display only the first name in the dropdown
        combo.setItemLabelGenerator(person -> person.getFirstName());

        add(combo);
    }

    public static class Person {
        private String firstName;
        private String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public String toString() {
            return firstName + " " + lastName;
        }
    }
}