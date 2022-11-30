/**
 * Copyright (C) 2000-${year} Vaadin Ltd
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
@Route(value = "basic-grid", registerAtStartup = false)
public class BeanGridView extends Component implements HasComponents {

    Grid<Person> beanGrid = new Grid<>(Person.class);
    Person first = Person.createTestPerson1();
    Person second = Person.createTestPerson2();

    public BeanGridView() {
        beanGrid.setItems(first, second);

        add(beanGrid);
    }

}
