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
