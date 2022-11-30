/**
 * Copyright (C) 2000-2022 Vaadin Ltd
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "basic-grid", registerAtStartup = false)
public class BasicGridView extends Component implements HasComponents {

    Grid<Person> basicGrid = new Grid<>();
    Person first = Person.createTestPerson1();
    Person second = Person.createTestPerson2();

    final String firstHeader = "Name";
    final String secondHeader = "Age";
    final String subscriber = "Subscriber";

    public BasicGridView() {

        basicGrid.setItems(first, second);

        basicGrid.addColumn(Person::getFirstName).setHeader(firstHeader);
        basicGrid.addColumn(Person::getLastName).setHeader(firstHeader)
                .setVisible(false);
        basicGrid.addColumn(Person::getAge).setHeader(secondHeader);
        basicGrid
                .addColumn(new ComponentRenderer<>(
                        person -> new CheckBox(person.isSubscriber())))
                .setHeader(subscriber).setKey(subscriber);
        add(basicGrid);
    }

}
