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
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "renderer-grid", registerAtStartup = false)
public class RendererGridView extends Component implements HasComponents {

    Grid<Person> grid = new Grid<>();
    Person first = Person.createTestPerson1();
    Person second = Person.createTestPerson2();

    public RendererGridView() {
        grid.setItems(first, second);

        grid.addColumn(new ComponentRenderer<>(p -> new Container(
                new Text(p.getFirstName()), new TextNode(p.getLastName()),
                new TextNode(Integer.toString(p.getAge())))))
                .setKey("componentRendered");
        grid.addColumn(new ComponentRenderer<>(p -> null))
                .setKey("nullRendered");
        grid.addColumn(new ComponentRenderer<>(p -> new Icon("USER")));
        add(grid);
    }

    @Tag("a-container")
    public static class Container extends Component implements HasComponents {
        public Container(Component... components) {
            add(components);
        }
    }

    @Tag("icon")
    public static class Icon extends Component {
        public Icon(String name) {
            getElement().setProperty("name", name);
        }
    }

    @Tag("text-node")
    public static class TextNode extends Component {
        public TextNode(String text) {
            super(Element.createText(text));
        }
    }

    @Tag("text")
    public static class Text extends Component implements HasText {
        public Text(String text) {
            setText(text);
        }
    }

}
