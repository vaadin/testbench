/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.testbench.unit.ComponentWrapTest.Span;

import static java.util.Arrays.asList;

class ComponentQueryTest extends UIUnitTest {

    @Test
    void find_invisibleComponents_noResults() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new Div().getElement(), new Div().getElement(),
                new Div().getElement());
        rootElement.getChildren().filter(el -> !el.isTextNode())
                .forEach(el -> el.setVisible(false));

        Assertions.assertTrue($(Div.class).allComponents().isEmpty());
    }

    @Test
    void first_exactMatch_getsComponent() {
        Element root = getCurrentView().getElement();

        TextField textField = new TextField();
        root.appendChild(textField.getElement());
        Button button = new Button();
        root.appendChild(button.getElement());

        ComponentQuery<TextField> textFieldQuery = $(TextField.class);
        Assertions.assertSame(textField, textFieldQuery.first().getComponent(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = $(Button.class);
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

        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertSame(first, query.first().getComponent(),
                "Expecting query to find TextField component, but got different instance");
    }

    @Test
    void first_noMatching_throws() {
        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertThrows(NoSuchElementException.class, query::first);
    }

    @Test
    void last_exactMatch_getsComponent() {
        Element root = getCurrentView().getElement();

        TextField textField = new TextField();
        root.appendChild(textField.getElement());
        Button button = new Button();
        root.appendChild(button.getElement());

        ComponentQuery<TextField> textFieldQuery = $(TextField.class);
        Assertions.assertSame(textField, textFieldQuery.last().getComponent(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = $(Button.class);
        Assertions.assertSame(button, buttonQuery.last().getComponent(),
                "Expecting query to find Button component, but got different instance");

    }

    @Test
    void last_multipleMatching_getsLastComponent() {
        TextField last = new TextField();
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement(),
                new TextField().getElement(), new TextField().getElement(),
                last.getElement());

        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertSame(last, query.last().getComponent(),
                "Expecting query to find TextField component, but got different instance");
    }

    @Test
    void last_noMatching_throws() {
        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertThrows(NoSuchElementException.class, query::last);
    }

    @Test
    void atIndex_exactMatch_getsComponent() {
        Element root = getCurrentView().getElement();

        TextField textField = new TextField();
        root.appendChild(textField.getElement());
        Button button = new Button();
        root.appendChild(button.getElement());

        ComponentQuery<TextField> textFieldQuery = $(TextField.class);
        Assertions.assertSame(textField,
                textFieldQuery.atIndex(1).getComponent(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = $(Button.class);
        Assertions.assertSame(button, buttonQuery.atIndex(1).getComponent(),
                "Expecting query to find Button component, but got different instance");

    }

    @Test
    void atIndex_multipleMatching_getsFirstComponent() {
        TextField last = new TextField();
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement(),
                new TextField().getElement(), new TextField().getElement(),
                last.getElement());

        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertSame(last, query.atIndex(4).getComponent(),
                "Expecting query to find TextField component, but got different instance");
    }

    @Test
    void atIndex_negativeOrZeroIndex_throws() {
        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.atIndex(-10));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.atIndex(0));
    }

    @Test
    void atIndex_outOfUpperBound_throws() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement(),
                new TextField().getElement(), new TextField().getElement());
        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> query.atIndex(4));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> query.atIndex(100));
    }

    @Test
    void atIndex_noMatching_throws() {
        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> query.atIndex(1));
    }

    @Test
    void all_noMatching_getsEmptyList() {
        ComponentQuery<TextField> query = $(TextField.class);
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

        ComponentQuery<TextField> query = $(TextField.class);
        List<ComponentWrap<TextField>> result = query.all();
        Assertions.assertNotNull(result);
        List<TextField> foundComponents = result.stream()
                .map(ComponentWrap::getComponent).collect(Collectors.toList());
        Assertions.assertIterableEquals(expectedComponents, foundComponents);

        Assertions.assertIterableEquals(
                Collections
                        .singleton(getCurrentView().getElement().getChild(0)),
                $(Text.class).allComponents().stream()
                        .map(Component::getElement)
                        .collect(Collectors.toList()));
    }

    @Test
    void allComponents_noMatching_getsEmptyList() {
        ComponentQuery<TextField> query = $(TextField.class);
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

        ComponentQuery<TextField> query = $(TextField.class);
        List<TextField> result = query.allComponents();
        Assertions.assertIterableEquals(expectedComponents, result);

        Assertions.assertIterableEquals(
                Collections
                        .singleton(getCurrentView().getElement().getChild(0)),
                $(Text.class).allComponents().stream()
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

        List<TextField> result = $(TextField.class).from(context)
                .allComponents();
        Assertions.assertIterableEquals(List.of(textField1, textField2),
                result);

        result = $view(TextField.class).allComponents();
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

        List<TextField> result = $(TextField.class).from(context)
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

        List<TextField> result = $(TextField.class).from(context)
                .allComponents();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void from_withCriteria_getsComponentInContext() {
        TextField notInViewTextField = new TextField();
        notInViewTextField.setId("myId");
        UI.getCurrent().getElement()
                .appendChild(notInViewTextField.getElement());

        TextField inViewTextField = new TextField();
        inViewTextField.setId("myId");
        Div context = new Div(inViewTextField);
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(context.getElement());

        List<TextField> result = $(TextField.class).from(context)
                .allComponents();
        Assertions.assertIterableEquals(Collections.singleton(inViewTextField),
                result);

        ComponentWrap<TextField> foundTextField = $(TextField.class)
                .from(context).id("myId");
        Assertions.assertSame(inViewTextField, foundTextField.getComponent());

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

        ComponentQuery<TextField> query = $view(TextField.class);

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

        ComponentQuery<TextField> query = $view(TextField.class);
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

        ComponentQuery<TextField> query = $view(TextField.class);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> query.id("myId"));
    }

    @Test
    void withId_matchingComponent_getsComponent() {
        Element rootElement = getCurrentView().getElement();
        List<TextField> textFields = IntStream.rangeClosed(1, 5)
                .mapToObj(idx -> {
                    TextField field = new TextField();
                    field.setId("field-" + idx);
                    return field;
                }).peek(field -> rootElement.appendChild(field.getElement()))
                .collect(Collectors.toList());

        ComponentQuery<TextField> query = $view(TextField.class);

        for (TextField expected : textFields) {
            List<TextField> result = query.withId(expected.getId().orElse(""))
                    .allComponents();
            Assertions.assertIterableEquals(Collections.singleton(expected),
                    result);
        }
    }

    @Test
    void withId_noMatchingComponent_emptyList() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement());
        TextField textField = new TextField();
        textField.setId("myId");
        rootElement.appendChild(textField.getElement());

        ComponentQuery<TextField> query = $view(TextField.class);
        Assertions
                .assertTrue(query.withId("wrongId").allComponents().isEmpty());
    }

    @Test
    void withId_matchingDifferentComponentType_emptyList() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement());
        Button button = new Button();
        button.setId("myId");
        rootElement.appendChild(button.getElement());

        ComponentQuery<TextField> query = $view(TextField.class);
        Assertions.assertTrue(query.withId("myId").allComponents().isEmpty());
    }

    @Test
    void withPropertyValue_matchingValue_findsComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField textField = new TextField();
        String label = "field label";
        textField.setLabel(label);
        rootElement.appendChild(textField.getElement());
        TextField textField2 = new TextField();
        textField2.setLabel("Another label");
        rootElement.appendChild(textField2.getElement());
        rootElement.appendChild(new TextField().getElement());

        Assertions.assertSame(textField,
                $(TextField.class).withPropertyValue(TextField::getLabel, label)
                        .first().getComponent());
    }

    @Test
    void withPropertyValue_expectedNull_findsComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField textField = new TextField();
        rootElement.appendChild(textField.getElement());
        TextField textField2 = new TextField();
        textField2.setLabel("Another label");
        rootElement.appendChild(textField2.getElement());

        Assertions.assertSame(textField,
                $(TextField.class).withPropertyValue(TextField::getLabel, null)
                        .first().getComponent());
    }

    @Test
    void withPropertyValue_noMatchingValue_doesNotFindComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField textField = new TextField();
        rootElement.appendChild(textField.getElement());
        TextField textField2 = new TextField();
        textField2.setLabel("Another label");
        rootElement.appendChild(textField2.getElement());

        Assertions.assertTrue($(TextField.class)
                .withPropertyValue(TextField::getLabel, "The label").all()
                .isEmpty());
    }

    @Test
    void withCondition_predicateMatched_getsComponents() {
        Div div1 = new Div();
        div1.getElement().setProperty("numeric-prop", 4.5);
        Div div2 = new Div(new Div(), new Div());
        div2.getElement().setProperty("numeric-prop", 2.0);
        Div div3 = new Div();
        div3.getElement().setProperty("numeric-prop", 1.5);
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement());

        List<Div> result = $(Div.class)
                .withCondition(div -> div.getChildren().findAny().isPresent())
                .allComponents();
        Assertions.assertIterableEquals(Collections.singleton(div2), result);

        result = $(Div.class).withCondition(div -> {
            double value = div.getElement().getProperty("numeric-prop", 0.0);
            return value > 1 && value < 3;
        }).allComponents();
        Assertions.assertIterableEquals(List.of(div2, div3), result);
    }

    @Test
    void withCaption_exactMatch_getsCorrectComponent() {
        ComponentWithLabel hasLabelCmp = new ComponentWithLabel();
        hasLabelCmp.setLabel("has-label");

        TestComponent propertyCmp = new TestComponent();
        propertyCmp.getElement().setProperty("label", "property-label");

        TestComponent noLabel = new TestComponent();

        UI.getCurrent().getElement().appendChild(hasLabelCmp.getElement(),
                propertyCmp.getElement(), noLabel.getElement());

        Assertions.assertSame(hasLabelCmp, $(TestComponent.class)
                .withCaption("has-label").findComponent());
        Assertions.assertSame(propertyCmp, $(TestComponent.class)
                .withCaption("property-label").findComponent());

        Assertions.assertTrue($(TestComponent.class).withCaption("label")
                .allComponents().isEmpty());
    }

    @Test
    void withCaption_null_getsAllComponent() {
        ComponentWithLabel hasLabelCmp = new ComponentWithLabel();
        hasLabelCmp.setLabel("has-label");

        TestComponent propertyCmp = new TestComponent();
        propertyCmp.getElement().setProperty("label", "property-label");

        TestComponent noLabel = new TestComponent();

        UI.getCurrent().getElement().appendChild(hasLabelCmp.getElement(),
                propertyCmp.getElement(), noLabel.getElement());

        Assertions.assertIterableEquals(
                List.of(hasLabelCmp, propertyCmp, noLabel),
                $(TestComponent.class).withCaption(null).allComponents());
    }

    @Test
    void withCaptionContaining_getsCorrectComponent() {
        ComponentWithLabel hasLabelCmp = new ComponentWithLabel();
        hasLabelCmp.setLabel("cmp-has-label");

        TestComponent propertyCmp = new TestComponent();
        propertyCmp.getElement().setProperty("label", "cmp-property-label");

        TestComponent noLabel = new TestComponent();

        UI.getCurrent().getElement().appendChild(hasLabelCmp.getElement(),
                propertyCmp.getElement(), noLabel.getElement());

        Assertions.assertIterableEquals(List.of(hasLabelCmp, propertyCmp),
                $(TestComponent.class).withCaptionContaining("-lab")
                        .allComponents());
        Assertions.assertIterableEquals(List.of(hasLabelCmp, propertyCmp),
                $(TestComponent.class).withCaptionContaining("-label")
                        .allComponents());
        Assertions.assertIterableEquals(List.of(hasLabelCmp, propertyCmp),
                $(TestComponent.class).withCaptionContaining("cmp-")
                        .allComponents());
        Assertions.assertIterableEquals(
                List.of(hasLabelCmp, propertyCmp, noLabel),
                $(TestComponent.class).withCaptionContaining("")
                        .allComponents());
        Assertions.assertTrue($(TestComponent.class)
                .withCaptionContaining("sometext").allComponents().isEmpty());
    }

    @Test
    void withCaptionContaining_null_throws() {
        ComponentQuery<TestComponent> query = $(TestComponent.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withCaptionContaining(null));
    }

    @Test
    void thenOnFirst_chainedQuery_getsNestedComponents() {
        TextField deepNested = new TextField();
        Div nestedDiv = new Div(deepNested);
        nestedDiv.setId("nestedDiv");
        TextField nested1 = new TextField();
        TextField nested2 = new TextField();
        Div firstMatch = new Div(new Div(nestedDiv), nested1, new Div(nested2));
        firstMatch.setId("myId");
        UI.getCurrent().getElement().appendChild(
                new Div(firstMatch).getElement(), new TextField().getElement());

        List<TextField> result = $(Div.class).withId("myId")
                .thenOnFirst(TextField.class).allComponents();
        Assertions.assertIterableEquals(List.of(deepNested, nested1, nested2),
                result);

        result = $(Div.class).withId("myId").thenOnFirst(Div.class)
                .withId("nestedDiv").thenOnFirst(TextField.class)
                .allComponents();
        Assertions.assertIterableEquals(List.of(deepNested), result);
    }

    @Test
    void thenOnFirst_firstNotFound_throws() {
        Div div = new Div(new TextField());
        div.setVisible(false);
        UI.getCurrent().getElement().appendChild(div.getElement());

        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> query.thenOnFirst(TextField.class));
    }

    @Test
    void thenOn_chainedQuery_getsNestedComponents() {
        TextField nested = new TextField();
        UI.getCurrent().getElement().appendChild(
                new Div(new TextField()).getElement(),
                new Div(new TextField()).getElement(),
                new Div(nested).getElement(),
                new Div(new TextField()).getElement(),
                new Div(new TextField()).getElement());

        List<TextField> result = $(Div.class).thenOn(3, TextField.class)
                .allComponents();
        Assertions.assertIterableEquals(List.of(nested), result);
    }

    @Test
    void withTheme_getsCorrectComponent() {
        Span target = new Span();
        target.getElement().getThemeList().add("my-theme");
        UI.getCurrent().getElement().appendChild(new Span().getElement(),
                target.getElement(), new Span().getElement());

        Assertions.assertEquals(target,
                $(Span.class).withTheme("my-theme").findComponent());
    }

    @Test
    void withThemeMultipleThemes_getsCorrectComponent() {
        Span target = new Span();
        target.getElement().getThemeList().add("my-theme");
        target.getElement().getThemeList().add("custom-theme");

        final Span first = new Span();
        first.getElement().getThemeList().add("my-theme");
        final Span last = new Span();
        last.getElement().getThemeList().add("my-theme");

        UI.getCurrent().getElement().appendChild(first.getElement(),
                target.getElement(), last.getElement());

        Assertions.assertEquals(target, $(Span.class).withTheme("my-theme")
                .withTheme("custom-theme").findComponent());
    }

    @Test
    void withoutTheme_getsCorrectComponent() {
        Span target = new Span();
        target.getElement().getThemeList().add("my-theme");

        final Span first = new Span();
        first.getElement().getThemeList().add("custom-theme");
        final Span last = new Span();
        last.getElement().getThemeList().add("custom-theme");

        UI.getCurrent().getElement().appendChild(first.getElement(),
                target.getElement(), last.getElement());

        Assertions.assertEquals(target,
                $(Span.class).withoutTheme("custom-theme").findComponent());
    }

    @Test
    void withoutThemeMultipleThemes_getsCorrectComponent() {
        Span target = new Span();
        target.getElement().getThemeList().add("selection-theme");

        final Span first = new Span();
        first.getElement().getThemeList().add("selection-theme");
        first.getElement().getThemeList().add("my-theme");
        final Span last = new Span();
        last.getElement().getThemeList().add("selection-theme");
        last.getElement().getThemeList().add("custom-theme");

        UI.getCurrent().getElement().appendChild(first.getElement(),
                target.getElement(), last.getElement());

        Assertions.assertEquals(target, $(Span.class).withoutTheme("my-theme")
                .withoutTheme("custom-theme").findComponent());
    }

    @Test
    void withClass_singleClassName_getsComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                new Div().getElement(), div2.getElement(), div3.getElement(),
                div4.getElement());

        List<Div> result = $(Div.class).withClassName("test-class")
                .allComponents();
        Assertions.assertIterableEquals(List.of(div1, div2), result);

        result = $(Div.class).withClassName("other-class").allComponents();
        Assertions.assertIterableEquals(List.of(div2, div3), result);

        result = $(Div.class).withClassName("different-class")
                .allComponents();
        Assertions.assertIterableEquals(List.of(div4), result);
    }

    @Test
    void withClass_multipleClassNames_getsComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                new Div().getElement(), div2.getElement(), div3.getElement(),
                div4.getElement());

        List<Div> result = $(Div.class)
                .withClassName("test-class", "other-class").allComponents();
        Assertions.assertIterableEquals(List.of(div2), result);

        result = $(Div.class).withClassName("test-class")
                .withClassName("other-class").allComponents();
        Assertions.assertIterableEquals(List.of(div2), result);
    }

    @Test
    void withClass_multipleSpaceSeparatedClassNames_getsComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                new Div().getElement(), div2.getElement(), div3.getElement(),
                div4.getElement());

        List<Div> result = $(Div.class)
                .withClassName("test-class other-class").allComponents();
        Assertions.assertIterableEquals(List.of(div2), result);
        // order doesn't matter
        result = $(Div.class)
                .withClassName("other-class test-class").allComponents();
        Assertions.assertIterableEquals(List.of(div2), result);
    }


    @Test
    void withClass_notAllClassApplied_doesNotFindComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                new Div().getElement(), div2.getElement(), div3.getElement(),
                div4.getElement());

        List<Div> result = $(Div.class)
                .withClassName("test-class", "different-class").allComponents();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void withClass_nullClassNames_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withClassName(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withClassName("c1", (String) null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withClassName("c1", "c2", null, "c3"));
    }

    @Test
    void withoutClass_singleClassName_getsComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        Div divWithotClasses = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                divWithotClasses.getElement(), div2.getElement(),
                div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withoutClassName("test-class")
                .allComponents();
        Assertions.assertIterableEquals(List.of(divWithotClasses, div3, div4),
                result);

        result = $(Div.class).withoutClassName("other-class")
                .allComponents();
        Assertions.assertIterableEquals(List.of(div1, divWithotClasses, div4),
                result);

        result = $(Div.class).withoutClassName("different-class")
                .allComponents();
        Assertions.assertIterableEquals(
                List.of(div1, divWithotClasses, div2, div3), result);
    }

    @Test
    void withoutClass_multipleClassNames_getsComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        Div divWithoutClasses = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                divWithoutClasses.getElement(), div2.getElement(),
                div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class)
                .withoutClassName("test-class", "other-class").allComponents();
        Assertions.assertIterableEquals(List.of(divWithoutClasses, div4),
                result);

        result = $(Div.class).withoutClassName("test-class")
                .withoutClassName("other-class").allComponents();
        Assertions.assertIterableEquals(List.of(divWithoutClasses, div4),
                result);
    }

    @Test
    void withoutClass_multipleSpaceSeparatedClassNames_getsComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        Div divWithoutClasses = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                divWithoutClasses.getElement(), div2.getElement(),
                div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class)
                .withoutClassName("test-class other-class").allComponents();
        Assertions.assertIterableEquals(List.of(divWithoutClasses, div4),
                result);
        result = $(Div.class)
                .withoutClassName("other-class test-class").allComponents();
        Assertions.assertIterableEquals(List.of(divWithoutClasses, div4),
                result);

    }

    @Test
    void withoutClass_allClassApplied_doesNotFindComponents() {
        Div div1 = new Div();
        div1.setClassName("test-class");
        Div div2 = new Div();
        div2.addClassName("test-class");
        div2.addClassName("other-class");
        Div div3 = new Div();
        div3.addClassName("other-class");
        Div div4 = new Div();
        div4.setClassName("different-class");
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withoutClassName("test-class",
                "other-class", "different-class").allComponents();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void withoutClass_nullClassNames_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withoutClassName(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withoutClassName("c1", (String) null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withoutClassName("c1", "c2", null, "c3"));
    }

    @Tag("span")
    private static class ComponentWithLabel extends TestComponent
            implements HasLabel {
    }
}
