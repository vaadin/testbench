/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.testbench.unit;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;

import static java.util.Arrays.asList;

class ComponentQueryTest extends UIUnitTest {

    @Test
    void first_exactMatch_getsComponent() {
        Element root = getCurrentView().getElement();

        TextField textField = new TextField();
        root.appendChild(textField.getElement());
        Button button = new Button();
        root.appendChild(button.getElement());

        ComponentQuery<TextField> textFieldQuery = select(TextField.class);
        Assertions.assertSame(textField, textFieldQuery.first().getComponent(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = select(Button.class);
        Assertions.assertSame(button, buttonQuery.first().getComponent(),
                "Expecting query to find Button component, but got different instance");

    }

    @Test
    void first_multipleMatching_getsFirstComponent() {
        TextField first = new TextField();
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(first.getElement(),
                new TextField().getElement(), new TextField().getElement(),
                new TextField().getElement());

        ComponentQuery<TextField> query = select(TextField.class);
        Assertions.assertSame(first, query.first().getComponent(),
                "Expecting query to find TextField component, but got different instance");
    }

    @Test
    void first_noMatching_throws() {
        ComponentQuery<TextField> query = select(TextField.class);
        Assertions.assertThrows(NoSuchElementException.class, query::first);
    }

    @Test
    void last_exactMatch_getsComponent() {
        Element root = getCurrentView().getElement();

        TextField textField = new TextField();
        root.appendChild(textField.getElement());
        Button button = new Button();
        root.appendChild(button.getElement());

        ComponentQuery<TextField> textFieldQuery = select(TextField.class);
        Assertions.assertSame(textField, textFieldQuery.last().getComponent(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = select(Button.class);
        Assertions.assertSame(button, buttonQuery.last().getComponent(),
                "Expecting query to find Button component, but got different instance");

    }

    @Test
    void last_multipleMatching_getsFirstComponent() {
        TextField last = new TextField();
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement(),
                new TextField().getElement(), new TextField().getElement(),
                last.getElement());

        ComponentQuery<TextField> query = select(TextField.class);
        Assertions.assertSame(last, query.last().getComponent(),
                "Expecting query to find TextField component, but got different instance");
    }

    @Test
    void last_noMatching_throws() {
        ComponentQuery<TextField> query = select(TextField.class);
        Assertions.assertThrows(NoSuchElementException.class, query::last);
    }

    @Test
    void all_noMatching_getsEmptyList() {
        ComponentQuery<TextField> query = select(TextField.class);
        List<ComponentWrap<TextField>> result = query.all();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(),
                "Expecting no results from search, but got " + result);
    }

    @Test
    void all_matching_getsMatchingComponents() {

        List<TextField> expectedComponents = asList(new TextField(),
                new TextField(), new TextField());
        Element rootElement = getCurrentView().getElement();
        expectedComponents
                .forEach(text -> rootElement.appendChild(text.getElement()));

        ComponentQuery<TextField> query = select(TextField.class);
        List<ComponentWrap<TextField>> result = query.all();
        Assertions.assertNotNull(result);
        List<TextField> foundComponents = result.stream()
                .map(ComponentWrap::getComponent).collect(Collectors.toList());
        Assertions.assertIterableEquals(expectedComponents, foundComponents);

        Assertions.assertIterableEquals(
                Collections
                        .singleton(getCurrentView().getElement().getChild(0)),
                select(Text.class).allComponents().stream()
                        .map(Component::getElement)
                        .collect(Collectors.toList()));
    }

    @Test
    void allComponents_noMatching_getsEmptyList() {
        ComponentQuery<TextField> query = select(TextField.class);
        List<TextField> result = query.allComponents();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size(),
                "Expecting zero results from search, but got " + result.size());
    }

    @Test
    void allComponents_matching_getsMatchingComponents() {

        List<TextField> expectedComponents = asList(new TextField(),
                new TextField(), new TextField());
        Element rootElement = getCurrentView().getElement();
        expectedComponents
                .forEach(text -> rootElement.appendChild(text.getElement()));

        ComponentQuery<TextField> query = select(TextField.class);
        List<TextField> result = query.allComponents();
        Assertions.assertIterableEquals(expectedComponents, result);

        Assertions.assertIterableEquals(
                Collections
                        .singleton(getCurrentView().getElement().getChild(0)),
                select(Text.class).allComponents().stream()
                        .map(Component::getElement)
                        .collect(Collectors.toList()));
    }

    @Test
    void from_matchingChildren_getsNestedComponents() {
        Div context = new Div();
        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        TextField textField4 = new TextField();
        TextField textField5 = new TextField();

        context.add(new Div(textField1), textField2);
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(textField5.getElement(),
                new Div(context, textField3).getElement(),
                textField4.getElement());

        List<TextField> result = select(TextField.class).from(context)
                .allComponents();
        Assertions.assertIterableEquals(List.of(textField1, textField2),
                result);

        result = selectFromCurrentView(TextField.class).allComponents();
        Assertions.assertIterableEquals(List.of(textField5, textField1,
                textField2, textField3, textField4), result);

    }

    @Test
    void from_emptyContext_getsEmptyList() {
        Div context = new Div();
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement(),
                new Div(context, new TextField()).getElement(),
                new TextField().getElement());

        List<TextField> result = select(TextField.class).from(context)
                .allComponents();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void from_noMatchingChildren_getsEmptyList() {
        Div context = new Div();
        context.add(new Div(new Button()), new Button());
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new Div(context, new TextField()).getElement(),
                new TextField().getElement());

        List<TextField> result = select(TextField.class).from(context)
                .allComponents();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void id_matchingComponent_getsComponent() {
        Element rootElement = getCurrentView().getElement();
        List<TextField> textFields = IntStream.rangeClosed(1, 5)
                .mapToObj(idx -> {
                    TextField field = new TextField();
                    field.setId("field-" + idx);
                    return field;
                }).peek(field -> rootElement.appendChild(field.getElement()))
                .collect(Collectors.toList());

        ComponentQuery<TextField> query = selectFromCurrentView(
                TextField.class);

        textFields.forEach(field -> Assertions.assertSame(field,
                query.id(field.getId().orElse("")).getComponent()));
    }

    @Test
    void id_noMatchingComponent_throws() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement());
        TextField textField = new TextField();
        textField.setId("myId");
        rootElement.appendChild(textField.getElement());

        ComponentQuery<TextField> query = selectFromCurrentView(
                TextField.class);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> query.id("test"));
    }

    @Test
    void id_matchingDifferentComponentType_throws() {

        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement());
        Button button = new Button();
        button.setId("myId");
        rootElement.appendChild(button.getElement());

        ComponentQuery<TextField> query = selectFromCurrentView(
                TextField.class);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> query.id("myId"));
    }

}
