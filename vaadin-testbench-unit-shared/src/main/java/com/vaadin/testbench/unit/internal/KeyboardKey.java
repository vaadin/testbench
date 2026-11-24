/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.flow.component.Key;

/**
 * The {@code key} and {@code code} properties of a keyboard event for a
 * {@link Key}, as the browser reports them for a US keyboard layout.
 * <p>
 * A {@link Key} is identified either by the value of the {@code key} property,
 * such as {@code Enter} or {@code s}, or by the physical key, the value of the
 * {@code code} property, such as {@code KeyS} or {@code Digit1}. The browser
 * reports both, so the other one is derived. The key value is the one without
 * Shift, e.g. {@code s} for {@code KeyS}, also when Shift is pressed.
 * <p>
 * For internal use only.
 *
 * @param key
 *            the value of the {@code key} property, not {@code null}
 * @param code
 *            the value of the {@code code} property, not {@code null}
 */
record KeyboardKey(String key, String code) {

    private static final Pattern CODE = Pattern
            .compile("(?:Key([A-Z])|Digit([0-9])|Numpad([0-9]))");

    /**
     * The key values of the physical keys whose code does not tell the key
     * value, for a US keyboard layout.
     */
    private static final Map<String, String> KEY_OF_CODE = Map.ofEntries(
            Map.entry("Backquote", "`"), Map.entry("Minus", "-"),
            Map.entry("Equal", "="), Map.entry("BracketLeft", "["),
            Map.entry("BracketRight", "]"), Map.entry("Backslash", "\\"),
            Map.entry("Semicolon", ";"), Map.entry("Quote", "'"),
            Map.entry("Comma", ","), Map.entry("Period", "."),
            Map.entry("Slash", "/"), Map.entry("Space", " "),
            Map.entry("NumpadAdd", "+"), Map.entry("NumpadSubtract", "-"),
            Map.entry("NumpadMultiply", "*"), Map.entry("NumpadDivide", "/"),
            Map.entry("NumpadDecimal", "."), Map.entry("NumpadComma", ","),
            Map.entry("NumpadEqual", "="), Map.entry("NumpadEnter", "Enter"));

    /**
     * The codes of the key values whose physical key is not named after them. A
     * modifier is reported as its left key, except AltGraph, which is the right
     * Alt key.
     */
    private static final Map<String, String> CODE_OF_KEY = Map.of(" ", "Space",
            "Control", "ControlLeft", "Shift", "ShiftLeft", "Alt", "AltLeft",
            "AltGraph", "AltRight", "Meta", "MetaLeft");

    /**
     * Gets the {@code key} and {@code code} properties that the browser reports
     * when the given key is pressed.
     *
     * @param key
     *            the pressed key, not {@code null}
     * @return the properties of the keyboard event, not {@code null}
     */
    static KeyboardKey of(Key key) {
        String value = key.getKeys().getFirst();
        Matcher code = CODE.matcher(value);
        if (code.matches()) {
            String character = code.group(1) != null
                    ? code.group(1).toLowerCase()
                    : code.group(2) != null ? code.group(2) : code.group(3);
            return new KeyboardKey(character, value);
        }
        if (KEY_OF_CODE.containsKey(value)) {
            return new KeyboardKey(KEY_OF_CODE.get(value), value);
        }
        if (value.matches("[a-zA-Z]")) {
            return new KeyboardKey(value, "Key" + value.toUpperCase());
        }
        if (value.matches("[0-9]")) {
            return new KeyboardKey(value, "Digit" + value);
        }
        // Named keys, such as Enter, Tab or ArrowDown, have the same key value
        // and code
        return new KeyboardKey(value, CODE_OF_KEY.getOrDefault(value, value));
    }
}
