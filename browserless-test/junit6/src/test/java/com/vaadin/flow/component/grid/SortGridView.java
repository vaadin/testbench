/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
