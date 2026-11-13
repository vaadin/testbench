/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.JsonNode;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.ClickNotifier.ClickFiltersJs;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ShortcutRegistration.ShortcutFilterJs;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.DomEventData;
import com.vaadin.flow.dom.DomEventFilter;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.js.JsDefinition;
import com.vaadin.flow.js.JsExpression;
import com.vaadin.flow.js.JsSnippets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientDomEventTest {

    private static final Map<String, Object> EVENT = Map.of("button", 0,
            "ctrlKey", false, "key", "Enter", "pressure", 0.5);

    private static final ShortcutFilterJs SHORTCUT_FILTERS = JsSnippets
            .of(ShortcutFilterJs.class);

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
    void dispatch_unsupportedPreventDefaultExpression_isDefaultPreventedThrows() {
        element.addEventListener("click", fired::add)
                .setFilter("event.target.id === 'x'").preventDefault();

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> ClientDomEvent.dispatch(element, "click", EVENT)
                        .isDefaultPrevented());
        assertTrue(e.getMessage().contains("without a browser"),
                e.getMessage());
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { "KEY_S;CONTROL;true",
            "KEY_S;CONTROL SHIFT;false", "KEY_S;'';false",
            "KEY_A;CONTROL;false" })
    void dispatchKeyDown_shortcutFilter_matchesKeyAndModifiers(String key,
            String modifiers, boolean matches) {
        element.addEventListener("keydown", fired::add)
                .setFilterExpression(SHORTCUT_FILTERS.matchKeydown("Control",
                        "Shift Alt AltGraph Meta", true, null, true, true,
                        false, "KeyS"));
        List<KeyModifier> pressed = Arrays.stream(modifiers.split(" "))
                .filter(name -> !name.isEmpty()).map(KeyModifier::valueOf)
                .toList();
        Key pressedKey = "KEY_S".equals(key) ? Key.KEY_S : Key.KEY_A;

        ClientDomEvent event = ClientDomEvent.dispatchKeyDown(element,
                pressedKey, pressed);
        event.fire();

        assertEquals(matches, fired.size() == 1);
        assertEquals(matches, event.isDefaultPrevented());
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { "CONTROL;Control;true",
            "CONTROL;Shift;false", "ALT_GRAPH;AltGraph;true" })
    void dispatchKeyDown_getModifierState_reportsPressedModifiers(
            KeyModifier pressed, String modifier, boolean expected) {
        String expression = "event.getModifierState('" + modifier + "')";
        element.addEventListener("keydown", fired::add)
                .addEventData(expression);

        ClientDomEvent.dispatchKeyDown(element, Key.ENTER, List.of(pressed))
                .fire();

        assertEquals(expected, eventData().get(expression).booleanValue());
    }

    @Test
    void dispatch_getModifierState_readsModifierProperties() {
        String expression = "event.getModifierState('Control')";
        element.addEventListener("click", fired::add).addEventData(expression);

        ClientDomEvent.dispatch(element, "click", Map.of("ctrlKey", true))
                .fire();

        assertTrue(eventData().get(expression).booleanValue());
    }

    @ParameterizedTest
    @CsvSource({ "CONTROL,true", "SHIFT,false" })
    void dispatchKeyDown_modifierStateFilterWithPreventDefault_evaluated(
            KeyModifier pressed, boolean matches) {
        element.addEventListener("keydown", fired::add).setFilter(
                "event.getModifierState('Control') && event.key === 's'")
                .preventDefault();

        ClientDomEvent event = ClientDomEvent.dispatchKeyDown(element,
                Key.of("s"), List.of(pressed));
        event.fire();

        assertEquals(matches, event.isDefaultPrevented());
        assertEquals(matches, fired.size() == 1);
    }

    @Test
    void dispatchKeyDown_eventDataOfKeyAndModifiers() {
        element.addEventListener("keydown", fired::add)
                .addEventData("event.key").addEventData("event.ctrlKey")
                .addEventData("event.shiftKey");

        ClientDomEvent
                .dispatchKeyDown(element, Key.ENTER, List.of(KeyModifier.SHIFT))
                .fire();

        assertEquals("\"Enter\"", eventData().get("event.key").toString());
        assertFalse(eventData().get("event.ctrlKey").booleanValue());
        assertTrue(eventData().get("event.shiftKey").booleanValue());
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { "KeyS;s;KeyS", "s;s;KeyS",
            "Digit1;1;Digit1", "Numpad1;1;Numpad1", "Comma;,;Comma",
            "' ';' ';Space", "Enter;Enter;Enter",
            "Control;Control;ControlLeft" })
    void dispatchKeyDown_eventHasKeyAndCode(String pressed, String key,
            String code) {
        element.addEventListener("keydown", fired::add)
                .addEventData("event.key").addEventData("event.code");

        ClientDomEvent.dispatchKeyDown(element, Key.of(pressed), List.of())
                .fire();

        assertEquals(key, eventData().get("event.key").asString());
        assertEquals(code, eventData().get("event.code").asString());
    }

    @Test
    void dispatch_unknownDeclaredFilter_notNotifiedAndIsDefaultPreventedThrows() {
        element.addEventListener("click", fired::add).setFilterExpression(
                JsSnippets.of(CustomFilterJs.class).custom());

        ClientDomEvent event = ClientDomEvent.dispatch(element, "click", EVENT);
        event.fire();

        assertTrue(fired.isEmpty());
        IllegalStateException e = assertThrows(IllegalStateException.class,
                event::isDefaultPrevented);
        assertTrue(e.getMessage().contains("without a browser"),
                e.getMessage());
    }

    @Test
    void dispatch_unknownDeclaredEventData_leftOut() {
        element.addEventListener("click", fired::add)
                .addEventData(JsSnippets.of(CustomFilterJs.class).width());

        ClientDomEvent event = ClientDomEvent.dispatch(element, "click", EVENT);
        event.fire();

        assertTrue(eventData().isEmpty());
        assertFalse(event.isDefaultPrevented());
    }

    @ParameterizedTest
    @CsvSource({ "1,true", "2,false" })
    void dispatch_simulatedFilterWithPreventDefault_onlyWhenFilterMatches(
            int detail, boolean prevented) {
        element.addEventListener("click", fired::add).setFilterExpression(
                JsSnippets.of(ClickFiltersJs.class).isClickCountBetween(1, 1))
                .preventDefault();

        ClientDomEvent event = ClientDomEvent.dispatch(element, "click",
                Map.of("detail", detail));
        event.fire();

        assertEquals(prevented, event.isDefaultPrevented());
        assertEquals(prevented, fired.size() == 1);
    }

    @ParameterizedTest
    @CsvSource({ "1,single", "2,double", "3,''" })
    void dispatch_clickCountFilters_onlyMatchingListenerRuns(int detail,
            String expected) {
        ClickableDiv div = new ClickableDiv();
        List<String> runs = new ArrayList<>();
        div.addSingleClickListener(event -> runs.add("single"));
        div.addDoubleClickListener(event -> runs.add("double"));

        ClientDomEvent.dispatch(div.getElement(), "click",
                Map.of("button", 0, "detail", detail)).fire();

        assertEquals(expected.isEmpty() ? List.of() : List.of(expected), runs);
    }

    @Test
    void dispatch_clickCountFilter_undefinedDetail_noMatch() {
        ClickableDiv div = new ClickableDiv();
        List<String> runs = new ArrayList<>();
        div.addSingleClickListener(event -> runs.add("single"));

        ClientDomEvent.dispatch(div.getElement(), "click", Map.of()).fire();

        assertTrue(runs.isEmpty());
    }

    @Tag("div")
    private static class ClickableDiv extends Component
            implements ClickNotifier<ClickableDiv> {
    }

    @JsDefinition
    public interface CustomFilterJs extends Serializable {
        // The helper may prevent the default action without the body saying so
        @JsExpression("return window.app.isPrimaryClick(event);")
        DomEventFilter custom();

        @JsExpression("return element.offsetWidth;")
        DomEventData<Integer> width();
    }

    private JsonNode eventData() {
        assertEquals(1, fired.size());
        return fired.get(0).getEventData();
    }
}
