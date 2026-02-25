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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "basic-grid", registerAtStartup = false)
public class BasicGridView extends Component implements HasComponents {

    static final String FIRST_NAME_KEY = "First Name";
    static final String LAST_NAME_KEY = "Last Name";
    static final String AGE_KEY = "Age";
    static final String SUBSCRIBER_KEY = "Subscriber";
    static final String DECEASED_KEY = "Deceased";
    static final String BUTTON_KEY = "Button";

    final Grid<Person> basicGrid;
    final Person person1;
    final Person person2;

    public BasicGridView() {
        basicGrid = new Grid<>();

        basicGrid.addColumn(Person::getFirstName).setKey(FIRST_NAME_KEY)
                .setHeader("First Name");
        basicGrid.addColumn(Person::getLastName).setKey(LAST_NAME_KEY)
                .setHeader("Last Name").setVisible(false);
        basicGrid.addColumn(Person::getAge).setKey(AGE_KEY).setHeader("Age");
        basicGrid
                .addColumn(new ComponentRenderer<>(
                        person -> new CheckBox(person.isSubscriber())))
                .setKey(SUBSCRIBER_KEY).setHeader("Subscriber");
        basicGrid.addColumn(deceasedRenderer()).setKey(DECEASED_KEY)
                .setHeader("Deceased");
        basicGrid
                .addComponentColumn(person -> new Button("Click",
                        e -> Notification.show("Clicked!")))
                .setKey(BUTTON_KEY).setHeader("Button");

        add(basicGrid);

        person1 = Person.createTestPerson1();
        person1.setDeceased(true);
        person2 = Person.createTestPerson2();

        basicGrid.setItems(person1, person2);
    }

    private LitRenderer<Person> deceasedRenderer() {
        return LitRenderer.<Person> of(
                "<button @click=${onClick}>${item.deceased ? 'Yes' : 'No'}</button>")
                .withProperty("deceased", Person::getDeceased)
                .withFunction("onClick", person -> {
                    person.setDeceased(!person.getDeceased());
                    basicGrid.getListDataView().refreshItem(person);
                });
    }

}
