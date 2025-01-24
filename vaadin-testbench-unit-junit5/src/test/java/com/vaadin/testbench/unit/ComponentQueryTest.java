/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.dom.Element;
import com.vaadin.testbench.unit.ComponentTesterTest.Span;
import com.vaadin.testbench.unit.ElementConditionsTest.TextComponent;

import static java.util.Arrays.asList;

class ComponentQueryTest extends UIUnitTest {

    @Test
    void find_invisibleComponents_noResults() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new Div().getElement(), new Div().getElement(),
                new Div().getElement());
        rootElement.getChildren().filter(el -> !el.isTextNode())
                .forEach(el -> el.setVisible(false));

        Assertions.assertTrue($(Div.class).all().isEmpty());
    }

    @Test
    void first_exactMatch_getsComponent() {
        Element root = getCurrentView().getElement();

        TextField textField = new TextField();
        root.appendChild(textField.getElement());
        Button button = new Button();
        root.appendChild(button.getElement());

        ComponentQuery<TextField> textFieldQuery = $(TextField.class);
        Assertions.assertSame(textField, textFieldQuery.first(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = $(Button.class);
        Assertions.assertSame(button, buttonQuery.first(),
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
        Assertions.assertSame(first, query.first(),
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
        Assertions.assertSame(textField, textFieldQuery.last(),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = $(Button.class);
        Assertions.assertSame(button, buttonQuery.last(),
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
        Assertions.assertSame(last, query.last(),
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
        Assertions.assertSame(textField, textFieldQuery.atIndex(1),
                "Expecting query to find TextField component, but got different instance");

        ComponentQuery<Button> buttonQuery = $(Button.class);
        Assertions.assertSame(button, buttonQuery.atIndex(1),
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
        Assertions.assertSame(last, query.atIndex(4),
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
        List<TextField> result = query.all();
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
        List<TextField> result = query.all();
        Assertions.assertNotNull(result);
        Assertions.assertIterableEquals(expectedComponents, result);

        Assertions.assertIterableEquals(
                Collections
                        .singleton(getCurrentView().getElement().getChild(0)),
                $(Text.class).all().stream().map(Component::getElement)
                        .collect(Collectors.toList()));
    }

    @Test
    void allComponents_noMatching_getsEmptyList() {
        ComponentQuery<TextField> query = $(TextField.class);
        List<TextField> result = query.all();
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
        List<TextField> result = query.all();
        Assertions.assertIterableEquals(expectedComponents, result);

        Assertions.assertIterableEquals(
                Collections
                        .singleton(getCurrentView().getElement().getChild(0)),
                $(Text.class).all().stream().map(Component::getElement)
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

        List<TextField> result = $(TextField.class).from(context).all();
        Assertions.assertIterableEquals(List.of(textField1, textField2),
                result);

        result = $view(TextField.class).all();
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

        List<TextField> result = $(TextField.class).from(context).all();
        Assertions.assertTrue(result.isEmpty());

        // shorthand for from
        result = $(TextField.class, context).all();
        Assertions.assertTrue(result.isEmpty());

    }

    @Test
    void from_noMatchingChildren_getsEmptyList() {
        Div context = new Div();
        context.add(new Div(new Button()), new Button());
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new Div(context, new TextField()).getElement(),
                new TextField().getElement());

        List<TextField> result = $(TextField.class).from(context).all();
        Assertions.assertTrue(result.isEmpty());

        // shorthand for from
        result = $(TextField.class, context).all();
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

        List<TextField> result = $(TextField.class).from(context).all();
        Assertions.assertIterableEquals(Collections.singleton(inViewTextField),
                result);

        TextField foundTextField = $(TextField.class).from(context).id("myId");
        Assertions.assertSame(inViewTextField, foundTextField);

        // shorthand for from
        result = $(TextField.class, context).all();
        Assertions.assertIterableEquals(Collections.singleton(inViewTextField),
                result);

        foundTextField = $(TextField.class, context).id("myId");
        Assertions.assertSame(inViewTextField, foundTextField);
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
                query.id(field.getId().orElse(""))));
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
                    .all();
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
        Assertions.assertTrue(query.withId("wrongId").all().isEmpty());
    }

    @Test
    void withId_matchingDifferentComponentType_emptyList() {
        Element rootElement = getCurrentView().getElement();
        rootElement.appendChild(new TextField().getElement());
        Button button = new Button();
        button.setId("myId");
        rootElement.appendChild(button.getElement());

        ComponentQuery<TextField> query = $view(TextField.class);
        Assertions.assertTrue(query.withId("myId").all().isEmpty());
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

        Assertions.assertSame(textField, $(TextField.class)
                .withPropertyValue(TextField::getLabel, label).first());
    }

    @Test
    void withPropertyValue_expectedNull_findsComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField textField = new TextField();
        rootElement.appendChild(textField.getElement());
        TextField textField2 = new TextField();
        textField2.setLabel("Another label");
        rootElement.appendChild(textField2.getElement());

        Assertions.assertSame(textField, $(TextField.class)
                .withPropertyValue(TextField::getLabel, null).first());
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
    void withValue_matchingValue_findsComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField targetField = new TextField();
        String targetValue = "expected value";
        targetField.setValue(targetValue);
        rootElement.appendChild(targetField.getElement());
        TextField otherField = new TextField();
        otherField.setValue("Another value");
        int targetNumericValue = 33;
        IntegerField numericField = new IntegerField();
        numericField.setValue(targetNumericValue);

        rootElement.appendChild(otherField.getElement());
        rootElement.appendChild(numericField.getElement());
        rootElement.appendChild(new TextField().getElement());

        Assertions.assertSame(targetField,
                $(Component.class).withValue(targetValue).first());
        Assertions.assertSame(numericField,
                $(Component.class).withValue(targetNumericValue).first());

    }

    @Test
    void withValue_expectedNull_findsAllComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField targetField = new TextField();
        rootElement.appendChild(targetField.getElement());
        TextField otherField = new TextField();
        otherField.setValue("A value");
        rootElement.appendChild(otherField.getElement());

        Assertions.assertIterableEquals(List.of(targetField, otherField),
                $(TextFieldBase.class).withValue(null).all());
    }

    @Test
    void withValue_noMatchingValue_doesNotFindComponent() {
        Element rootElement = getCurrentView().getElement();
        TextField textField = new TextField();
        rootElement.appendChild(textField.getElement());
        TextField textField2 = new TextField();
        textField2.setLabel("Another value");
        rootElement.appendChild(textField2.getElement());

        Assertions.assertTrue(
                $(TextField.class).withValue("The value").all().isEmpty());

        Assertions.assertTrue($(TextField.class).withValue(35).all().isEmpty());
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
                .all();
        Assertions.assertIterableEquals(Collections.singleton(div2), result);

        result = $(Div.class).withCondition(div -> {
            double value = div.getElement().getProperty("numeric-prop", 0.0);
            return value > 1 && value < 3;
        }).all();
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

        Assertions.assertSame(hasLabelCmp,
                $(TestComponent.class).withCaption("has-label").single());
        Assertions.assertSame(propertyCmp,
                $(TestComponent.class).withCaption("property-label").single());

        Assertions.assertTrue(
                $(TestComponent.class).withCaption("label").all().isEmpty());
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
                $(TestComponent.class).withCaption(null).all());
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
                $(TestComponent.class).withCaptionContaining("-lab").all());
        Assertions.assertIterableEquals(List.of(hasLabelCmp, propertyCmp),
                $(TestComponent.class).withCaptionContaining("-label").all());
        Assertions.assertIterableEquals(List.of(hasLabelCmp, propertyCmp),
                $(TestComponent.class).withCaptionContaining("cmp-").all());
        Assertions.assertIterableEquals(
                List.of(hasLabelCmp, propertyCmp, noLabel),
                $(TestComponent.class).withCaptionContaining("").all());
        Assertions.assertTrue($(TestComponent.class)
                .withCaptionContaining("sometext").all().isEmpty());
    }

    @Test
    void withCaptionContaining_null_throws() {
        ComponentQuery<TestComponent> query = $(TestComponent.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withCaptionContaining(null));
    }

    @Test
    void withText_exactMatch_getsCorrectComponent() {
        TextComponent span1 = new TextComponent("sample text");
        TextComponent span2 = new TextComponent("other text");
        TextComponent span3 = new TextComponent(null);

        UI.getCurrent().getElement().appendChild(span1.getElement(),
                span2.getElement(), span3.getElement());

        Assertions.assertSame(span1,
                $(TextComponent.class).withText("sample text").single());
        Assertions.assertSame(span2,
                $(TextComponent.class).withText("other text").single());

        Assertions.assertTrue(
                $(TextComponent.class).withText("text").all().isEmpty());
        Assertions.assertTrue(
                $(TextComponent.class).withText("SAMPLE TEXT").all().isEmpty());
        Assertions.assertTrue(
                $(TextComponent.class).withText("other TEXT").all().isEmpty());
    }

    @Test
    void withTextContaining_getsCorrectComponent() {
        TextComponent span1 = new TextComponent(
                "this is sample text for first span");
        TextComponent span2 = new TextComponent(
                "this is other text second span");
        TextComponent span3 = new TextComponent(null);

        UI.getCurrent().getElement().appendChild(span1.getElement(),
                span2.getElement(), span3.getElement());

        Assertions.assertIterableEquals(List.of(span1, span2),
                $(TextComponent.class).withTextContaining("text").all());
        Assertions.assertIterableEquals(List.of(span1, span2),
                $(TextComponent.class).withTextContaining("span").all());
        Assertions.assertIterableEquals(List.of(span1, span2),
                $(TextComponent.class).withTextContaining("this").all());
        Assertions.assertIterableEquals(List.of(span1, span2, span3),
                $(TextComponent.class).withTextContaining("").all());
        Assertions.assertTrue($(TextComponent.class)
                .withTextContaining("textual").all().isEmpty());
    }

    @Test
    void withTextContaining_null_throws() {
        ComponentQuery<Span> query = $(Span.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withTextContaining(null));
    }

    @Test
    void withResultsSize_zeroMatchingResultsSize_emptyResult() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        Assertions.assertTrue(
                $(TestComponent.class).withResultsSize(0).all().isEmpty());
    }

    @Test
    void withResultsSize_matchingResultsSize_getsComponents() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withResultsSize(4).all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
    }

    @Test
    void withResultsSize_notMatchingResultsSize_throws() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        IntStream.rangeClosed(0, 10).filter(i -> i != 4).forEach(i -> {
            ComponentQuery<Div> query = $(Div.class).withResultsSize(i);
            Assertions.assertThrows(AssertionError.class, query::all);
        });
    }

    @Test
    void withResultsSize_negative_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withResultsSize(-1));
    }

    @Test
    void withResultsSize_afterWithMaxResults_overwritesRange() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withMaxResults(2).withResultsSize(4)
                .all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
    }

    @Test
    void withResultsSize_afterWithMinResults_overwritesRange() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withMinResults(10).withResultsSize(4)
                .all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
    }

    @Test
    void withResultsSize_afterWithResultSizeRange_overwritesRange() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withResultsSize(2, 3).withResultsSize(4)
                .all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
    }

    @Test
    void withMaxResults_resultsSizeWithinUpperBound_getsComponents() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        List<Div> result = $(Div.class).withMaxResults(10).all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
        result = $(Div.class).withMaxResults(4).all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
    }

    @Test
    void withMaxResults_resultsSizeExceededUpperBound_throws() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        IntStream.rangeClosed(1, 3).forEach(count -> {
            ComponentQuery<Div> query = $(Div.class).withMaxResults(count);
            Assertions.assertThrows(AssertionError.class, query::all);
        });
    }

    @Test
    void withMaxResults_negative_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withMaxResults(-1));
    }

    @Test
    void withMaxResults_lowerThanMin_throws() {
        ComponentQuery<Div> query = $(Div.class).withMinResults(4);
        query.withMaxResults(4); // same as min is OK, must not throw
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withMaxResults(2));
    }

    @Test
    void withMaxResults_afterResultsSize_overwritesRange() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());
        List<Div> result = $(Div.class).withResultsSize(1).withMaxResults(4)
                .all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
    }

    @Test
    void withMinResults_resultsSizeWithinLowerBound_getsComponents() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        IntStream.rangeClosed(0, 4).forEach(count -> {
            List<Div> result = $(Div.class).withMinResults(count).all();
            Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                    result);
        });
    }

    @Test
    void withMinResults_resultsSizeLessThanLowerBound_throws() {
        Div div1 = new Div();
        Div div2 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement());

        IntStream.rangeClosed(3, 10).forEach(count -> {
            ComponentQuery<Div> query = $(Div.class).withMinResults(count);
            Assertions.assertThrows(AssertionError.class, query::all);
        });
    }

    @Test
    void withMinResults_negative_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withMinResults(-1));
    }

    @Test
    void withMinResults_greaterThanMax_throws() {
        ComponentQuery<Div> query = $(Div.class).withMaxResults(2);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withMinResults(10));
    }

    @Test
    void withMinResults_afterResultsSize_overwritesLowerRange() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement());

        List<Div> result = $(Div.class).withResultsSize(4).withMinResults(1)
                .all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3), result);
    }

    @Test
    void withResults_resultsSizeOutOfBounds_throws() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        ComponentQuery<Div> query = $(Div.class).withResultsSize(5, 10);
        Assertions.assertThrows(AssertionError.class, query::all);

        query = $(Div.class).withResultsSize(1, 3);
        Assertions.assertThrows(AssertionError.class, query::all);

    }

    @Test
    void withResultsSize_minNegative_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withResultsSize(-1, 10));
    }

    @Test
    void withResultsSize_maxNegative_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withResultsSize(2, -1));
    }

    @Test
    void withResultsSize_maxLowerThanMin_throws() {
        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> query.withResultsSize(5, 2));
    }

    @Test
    void withResultsSize_afterResultsSize_overwritesRange() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());
        List<Div> result = $(Div.class).withResultsSize(5).withResultsSize(2, 5)
                .all();
        Assertions.assertIterableEquals(List.of(div1, div2, div3, div4),
                result);
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
                .thenOnFirst(TextField.class).all();
        Assertions.assertIterableEquals(List.of(deepNested, nested1, nested2),
                result);

        result = $(Div.class).withId("myId").thenOnFirst(Div.class)
                .withId("nestedDiv").thenOnFirst(TextField.class).all();
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

        List<TextField> result = $(Div.class).thenOn(3, TextField.class).all();
        Assertions.assertIterableEquals(List.of(nested), result);
    }

    @Test
    void withTheme_getsCorrectComponent() {
        Span target = new Span();
        target.getElement().getThemeList().add("my-theme");
        UI.getCurrent().getElement().appendChild(new Span().getElement(),
                target.getElement(), new Span().getElement());

        Assertions.assertEquals(target,
                $(Span.class).withTheme("my-theme").single());
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
                .withTheme("custom-theme").single());
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
                $(Span.class).withoutTheme("custom-theme").single());
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
                .withoutTheme("custom-theme").single());
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

        List<Div> result = $(Div.class).withClassName("test-class").all();
        Assertions.assertIterableEquals(List.of(div1, div2), result);

        result = $(Div.class).withClassName("other-class").all();
        Assertions.assertIterableEquals(List.of(div2, div3), result);

        result = $(Div.class).withClassName("different-class").all();
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
                .withClassName("test-class", "other-class").all();
        Assertions.assertIterableEquals(List.of(div2), result);

        result = $(Div.class).withClassName("test-class")
                .withClassName("other-class").all();
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

        List<Div> result = $(Div.class).withClassName("test-class other-class")
                .all();
        Assertions.assertIterableEquals(List.of(div2), result);
        // order doesn't matter
        result = $(Div.class).withClassName("other-class test-class").all();
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
                .withClassName("test-class", "different-class").all();
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

        List<Div> result = $(Div.class).withoutClassName("test-class").all();
        Assertions.assertIterableEquals(List.of(divWithotClasses, div3, div4),
                result);

        result = $(Div.class).withoutClassName("other-class").all();
        Assertions.assertIterableEquals(List.of(div1, divWithotClasses, div4),
                result);

        result = $(Div.class).withoutClassName("different-class").all();
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
                .withoutClassName("test-class", "other-class").all();
        Assertions.assertIterableEquals(List.of(divWithoutClasses, div4),
                result);

        result = $(Div.class).withoutClassName("test-class")
                .withoutClassName("other-class").all();
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
                .withoutClassName("test-class other-class").all();
        Assertions.assertIterableEquals(List.of(divWithoutClasses, div4),
                result);
        result = $(Div.class).withoutClassName("other-class test-class").all();
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
                "other-class", "different-class").all();
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

    @Test
    void withAttribute_present_getsCorrectComponents() {
        Span target = new Span();
        target.getElement().setAttribute("my-attr", "value");
        Span other = new Span();
        other.getElement().setAttribute("other-attr", "value");
        UI.getCurrent().getElement().appendChild(new Span().getElement(),
                target.getElement(), new Span().getElement(),
                other.getElement());

        Assertions.assertSame(target,
                $(Span.class).withAttribute("my-attr").single());
        Assertions.assertSame(other,
                $(Span.class).withAttribute("other-attr").single());
        Assertions.assertTrue(
                $(Span.class).withAttribute("nope").all().isEmpty());

    }

    @Test
    void withAttribute_value_getsCorrectComponents() {
        Span target = new Span();
        target.getElement().setAttribute("my-attr", "value");
        Span other = new Span();
        other.getElement().setAttribute("my-attr", "something else");
        UI.getCurrent().getElement().appendChild(new Span().getElement(),
                target.getElement(), new Span().getElement(),
                other.getElement());

        Assertions.assertSame(target,
                $(Span.class).withAttribute("my-attr", "value").single());
        Assertions.assertSame(other, $(Span.class)
                .withAttribute("my-attr", "something else").single());
        Assertions.assertTrue(
                $(Span.class).withAttribute("my-attr", "nope").all().isEmpty());
    }

    @Test
    void withAttribute_multipleAttributes_getsCorrectComponents() {
        Span target = new Span();
        target.getElement().setAttribute("role", "tooltip");
        target.getElement().setAttribute("aria-label", "some text");
        Span other = new Span();
        other.getElement().setAttribute("role", "something");
        other.getElement().setAttribute("aria-label", "some other text");

        UI.getCurrent().getElement().appendChild(new Span().getElement(),
                target.getElement(), new Span().getElement(),
                other.getElement());

        Assertions.assertSame(target, $(Span.class).withAttribute("aria-label")
                .withAttribute("role", "tooltip").single());
    }

    @Test
    void withoutAttribute_absent_getsCorrectComponents() {
        Span target = new Span();
        target.getElement().setAttribute("my-attr", "value");
        Span other = new Span();
        other.getElement().setAttribute("other-attr", "value");
        Span noAttributes = new Span();
        UI.getCurrent().getElement().appendChild(target.getElement(),
                noAttributes.getElement(), other.getElement());

        Assertions.assertIterableEquals(List.of(noAttributes, other),
                $(Span.class).withoutAttribute("my-attr").all());
        Assertions.assertIterableEquals(List.of(target, noAttributes),
                $(Span.class).withoutAttribute("other-attr").all());

        Assertions.assertIterableEquals(List.of(target, noAttributes, other),
                $(Span.class).withoutAttribute("role").all());
    }

    @Test
    void withoutAttribute_value_getsCorrectComponents() {
        Span target = new Span();
        target.getElement().setAttribute("my-attr", "value");
        Span other = new Span();
        other.getElement().setAttribute("my-attr", "something else");
        Span noAttributes = new Span();
        UI.getCurrent().getElement().appendChild(target.getElement(),
                noAttributes.getElement(), other.getElement());

        Assertions.assertIterableEquals(List.of(noAttributes, other),
                $(Span.class).withoutAttribute("my-attr", "value").all());
        Assertions.assertIterableEquals(List.of(target, noAttributes),
                $(Span.class).withoutAttribute("my-attr", "something else")
                        .all());
        Assertions.assertIterableEquals(List.of(target, noAttributes, other),
                $(Span.class).withoutAttribute("my-attr", "nope").all());
    }

    @Test
    void withoutAttribute_multipleAttributes_getsCorrectComponents() {
        Span span1 = new Span();
        span1.getElement().setAttribute("role", "tooltip");
        Span span2 = new Span();
        span2.getElement().setAttribute("role", "something");
        span2.getElement().setAttribute("aria-label", "some other text");
        Span target = new Span();
        target.getElement().setAttribute("role", "something");

        UI.getCurrent().getElement().appendChild(span1.getElement(),
                target.getElement(), span2.getElement());

        Assertions.assertSame(target,
                $(Span.class).withoutAttribute("aria-label")
                        .withoutAttribute("role", "tooltip").single());
    }

    @Test
    void exists_matchingComponents_true() {
        Element root = getCurrentView().getElement();

        root.appendChild(new TextField().getElement());
        root.appendChild(new TextField().getElement());
        root.appendChild(new TextField().getElement());
        root.appendChild(new TextField().getElement());
        root.appendChild(new Button().getElement());

        Assertions.assertTrue($(TextField.class).exists(),
                "Expecting components to be found, but exists is false");

        Assertions.assertTrue($(Button.class).exists(),
                "Expecting components to be found, but exists is false");
    }

    @Test
    void exists_noMatching_false() {
        ComponentQuery<TextField> query = $(TextField.class);
        Assertions.assertFalse(query.exists(),
                "Expecting no components to be found, but exists is true");
    }

    @Test
    void single_matchingExactlyOne_getsComponent() {
        Span span = new Span();
        Div div1 = new Div();
        Div target = new Div();
        target.setClassName("my-test");
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                target.getElement(), span.getElement(), div3.getElement(),
                div4.getElement());

        Assertions.assertSame(span, $(Span.class).single());
        Assertions.assertSame(target,
                $(Div.class).withClassName("my-test").single());
    }

    @Test
    void single_noMatching_throws() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        ComponentQuery<Span> queryNonExistent = $(Span.class);
        Assertions.assertThrows(NoSuchElementException.class,
                queryNonExistent::single);

        ComponentQuery<Div> query = $(Div.class).withClassName("my-test");
        Assertions.assertThrows(NoSuchElementException.class, query::single);
    }

    @Test
    void single_matchingMultipleComponents_throws() {
        Div div1 = new Div();
        Div div2 = new Div();
        Div div3 = new Div();
        Div div4 = new Div();
        UI.getCurrent().getElement().appendChild(div1.getElement(),
                div2.getElement(), div3.getElement(), div4.getElement());

        ComponentQuery<Div> query = $(Div.class);
        Assertions.assertThrows(NoSuchElementException.class, query::single);
    }

    @Tag("span")
    private static class ComponentWithLabel extends TestComponent
            implements HasLabel {
    }
}
