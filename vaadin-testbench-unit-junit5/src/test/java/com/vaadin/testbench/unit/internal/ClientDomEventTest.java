/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.JsonNode;

import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.Element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientDomEventTest {

    private static final Map<String, Object> EVENT = Map.of("button", 0,
            "ctrlKey", false, "key", "Enter", "pressure", 0.5);

    private final Element element = new Element("div");
    private final List<DomEvent> fired = new ArrayList<>();

    @ParameterizedTest
    @CsvSource(delimiter = ';', quoteCharacter = '`', value = {
            "event.button;0", "event.ctrlKey;false", "event.pressure;0.5",
            "event.key;\"Enter\"", "event.type;\"click\"",
            "event.button === 0;true", "event.button !== 0;false",
            "event.button == 0;true", "event.button != 0;false",
            "event.button < 1;true", "event.button <= 0;true",
            "event.button > 0;false", "event.button >= 1;false",
            "event.key === 'Enter';true", "event.key === \"Escape\";false",
            "!event.ctrlKey;true", "!!event.button;false",
            "event.ctrlKey || event.key;\"Enter\"",
            "event.button && event.key;0",
            "(event.button === 0 || event.ctrlKey) && event.key === 'Enter';true",
            "event.missing === undefined;true", "event.missing === null;true",
            "event.defaultPrevented;false" })
    void dispatch_evaluatesExpressionAgainstEvent(String expression,
            String json) {
        element.addEventListener("click", fired::add).addEventData(expression);

        ClientDomEvent.dispatch(element, "click", EVENT).fire();

        assertEquals(json, eventData().get(expression).toString());
    }

    @Test
    void dispatch_undefinedOrUnsupportedEventData_leftOut() {
        element.addEventListener("click", fired::add)
                .addEventData("event.missing").addEventData("element.value")
                .addEventData("event.target.id").synchronizeProperty("value")
                .mapEventTargetElement();

        ClientDomEvent.dispatch(element, "click", EVENT).fire();

        assertEquals(1, fired.size());
        assertFalse(eventData().has("event.missing"));
        assertFalse(eventData().has("element.value"));
        assertFalse(eventData().has("event.target.id"));
    }

    @Test
    void dispatch_filter_onlyMatchingListenerRuns() {
        List<String> runs = new ArrayList<>();
        element.addEventListener("click", event -> runs.add("left"))
                .setFilter("event.button === 0");
        element.addEventListener("click", event -> runs.add("right"))
                .setFilter("event.button === 2");

        ClientDomEvent.dispatch(element, "click", EVENT).fire();

        assertEquals(List.of("left"), runs);
    }

    @Test
    void dispatch_preventDefault_detectedBeforeFiring() {
        element.addEventListener("click", fired::add).preventDefault();

        ClientDomEvent event = ClientDomEvent.dispatch(element, "click", EVENT);

        assertTrue(event.isDefaultPrevented());
        assertTrue(fired.isEmpty());
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', quoteCharacter = '`', value = {
            "event.ctrlKey;false", "event.button === 0;true" })
    void dispatch_filteredPreventDefault_onlyWhenFilterMatches(String filter,
            boolean prevented) {
        element.addEventListener("click", fired::add).setFilter(filter)
                .preventDefault();

        assertEquals(prevented, ClientDomEvent.dispatch(element, "click", EVENT)
                .isDefaultPrevented());
    }

    @Test
    void dispatch_shortCircuitedCall_hasNoEffect() {
        element.addEventListener("click", fired::add)
                .addEventData("event.button === 0 || event.preventDefault()");

        assertFalse(ClientDomEvent.dispatch(element, "click", EVENT)
                .isDefaultPrevented());
    }

    @Test
    void dispatch_stopPropagation_doesNotPreventDefault() {
        element.addEventListener("click", fired::add).stopPropagation();

        ClientDomEvent event = ClientDomEvent.dispatch(element, "click", EVENT);
        event.fire();

        assertFalse(event.isDefaultPrevented());
        assertEquals(1, fired.size());
    }

    @Test
    void dispatch_unsupportedPreventDefaultExpression_throws() {
        element.addEventListener("click", fired::add)
                .setFilter("event.target.id === 'x'").preventDefault();

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> ClientDomEvent.dispatch(element, "click", EVENT));
        assertTrue(e.getMessage().contains("without a browser"),
                e.getMessage());
    }

    private JsonNode eventData() {
        assertEquals(1, fired.size());
        return fired.get(0).getEventData();
    }
}
