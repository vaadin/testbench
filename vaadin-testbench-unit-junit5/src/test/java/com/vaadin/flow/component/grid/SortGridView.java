/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "sort-grid", registerAtStartup = false)
public class SortGridView extends Component implements HasComponents {

    Grid<Person> grid = new Grid<>();
    Grid<Person> beanGrid = new Grid<>(Person.class);
    Person first = Person.createTestPerson1();
    Person second = Person.createTestPerson2();
    Person third = Person.createTestPerson3();

    final String firstHeader = "Name";
    final String secondHeader = "Age";
    final String thirdHeader = "Email";

    public SortGridView() {

        grid.setItems(first, second, third);

        grid.addColumn(Person::getFirstName).setHeader(firstHeader)
                .setSortable(true);
        grid.addColumn(Person::getAge).setHeader(secondHeader)
                .setSortable(true);
        grid.addColumn(Person::getEmail).setHeader(thirdHeader)
                .setSortable(false);

        beanGrid.setItems(first, second, third);
        beanGrid.getColumnByKey("firstName");
        beanGrid.getColumnByKey("age");
        beanGrid.getColumnByKey("email").setSortable(false);

        add(grid, beanGrid);
    }

}
