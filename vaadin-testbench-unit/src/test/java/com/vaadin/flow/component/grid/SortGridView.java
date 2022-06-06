/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
