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
@Route(value = "basic-grid", registerAtStartup = false)
public class BasicGridView extends Component implements HasComponents {

    Grid<Person> basicGrid = new Grid<>();
    Person first = Person.createTestPerson1();
    Person second = Person.createTestPerson2();

    final String firstHeader = "Name";
    final String secondHeader = "Age";

    public BasicGridView() {

        basicGrid.setItems(first, second);

        basicGrid.addColumn(Person::getFirstName).setHeader(firstHeader);
        basicGrid.addColumn(Person::getAge).setHeader(secondHeader);
        add(basicGrid);
    }

}
