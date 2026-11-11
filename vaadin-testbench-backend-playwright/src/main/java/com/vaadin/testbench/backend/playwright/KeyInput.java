/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.options.KeyboardModifier;
import org.jspecify.annotations.Nullable;

/**
 * Selenium's {@code sendKeys} on Playwright's keyboard. Text is typed; a W3C
 * WebDriver key code (U+E000 to U+E05D, as {@code Keys} holds them) presses its
 * key, with the event's {@code key} and modifiers as Selenium sends them. A
 * modifier stays down until pressed again, until {@code Keys.NULL} or until the
 * call ends, and a character typed under one is pressed by its key, so that
 * {@code SHIFT, "ab"} types {@code AB} as on Selenium.
 */
final class KeyInput {

    static final char NULL = '';

    static final Map<Character, String> MODIFIERS = Map.of('', "Shift", '',
            "Control", '', "Alt", '', "Meta");

    // CANCEL, HELP, CLEAR and ZENKAKU_HANKAKU have no Playwright key
    private static final Set<Character> UNSUPPORTED = Set.of('', '', '',
            '');

    private static final Map<Character, String> KEYS = new HashMap<>();

    static {
        String[] names = { null, null, null, "Backspace", "Tab", null, "Enter",
                "Enter", null, null, null, "Pause", "Escape", "Space", "PageUp",
                "PageDown", "End", "Home", "ArrowLeft", "ArrowUp", "ArrowRight",
                "ArrowDown", "Insert", "Delete", "Semicolon", "Equal" };
        for (int i = 0; i < names.length; i++) {
            if (names[i] != null) {
                KEYS.put((char) (0xE000 + i), names[i]);
            }
        }
        // The numpad constants press their digit or sign, as Selenium's do
        String numpad = "0123456789*+,-./";
        for (int i = 0; i < numpad.length(); i++) {
            KEYS.put((char) (0xE01A + i), String.valueOf(numpad.charAt(i)));
        }
        for (int i = 1; i <= 12; i++) {
            KEYS.put((char) (0xE030 + i), "F" + i);
        }
    }

    private final Keyboard keyboard;
    private final List<String> held = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();

    KeyInput(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    /** One {@code sendKeys} call: modifiers left down are released after it. */
    void send(CharSequence... keys) {
        try {
            for (CharSequence sequence : keys) {
                for (int i = 0; i < sequence.length(); i++) {
                    send(sequence.charAt(i));
                }
            }
            flush();
        } finally {
            releaseAll();
        }
    }

    private void send(char c) {
        if (c == NULL) {
            flush();
            releaseAll();
            return;
        }
        String modifier = MODIFIERS.get(c);
        if (modifier != null) {
            flush();
            if (held.remove(modifier)) {
                keyboard.up(modifier);
            } else {
                keyboard.down(modifier);
                held.add(modifier);
            }
            return;
        }
        if (c >= '' && c <= '') {
            flush();
            keyboard.press(keyName(c));
            return;
        }
        if (held.isEmpty() || Character.isSurrogate(c)) {
            text.append(c);
            return;
        }
        // Under a modifier a character is its key, as a keyboard sends it:
        // typed text would ignore Shift
        flush();
        keyboard.press(keyOf(c));
    }

    private void flush() {
        if (!text.isEmpty()) {
            keyboard.type(text.toString());
            text.setLength(0);
        }
    }

    private void releaseAll() {
        for (int i = held.size() - 1; i >= 0; i--) {
            keyboard.up(held.get(i));
        }
        held.clear();
    }

    static String keyName(char code) {
        if (UNSUPPORTED.contains(code)) {
            throw new UnsupportedOperationException(String.format(
                    "Key U+%04X has no Playwright equivalent", (int) code));
        }
        String name = KEYS.get(code);
        if (name == null) {
            throw new IllegalArgumentException(
                    String.format("U+%04X is no WebDriver key", (int) code));
        }
        return name;
    }

    static String keyOf(char c) {
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            return "Key" + Character.toUpperCase(c);
        }
        if (c >= '0' && c <= '9') {
            return "Digit" + c;
        }
        return String.valueOf(c);
    }

    /**
     * The modifiers of a click: Selenium's modifier key codes as Playwright's
     * modifiers.
     *
     * @throws IllegalArgumentException
     *             for a key that is no modifier
     */
    static List<KeyboardModifier> modifiers(CharSequence... keys) {
        List<KeyboardModifier> modifiers = new ArrayList<>();
        for (CharSequence key : keys) {
            KeyboardModifier modifier = key.length() == 1
                    ? modifier(MODIFIERS.get(key.charAt(0)))
                    : null;
            if (modifier == null) {
                throw new IllegalArgumentException(
                        "Not a modifier key: " + key);
            }
            modifiers.add(modifier);
        }
        return modifiers;
    }

    private static @Nullable KeyboardModifier modifier(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
        case "Shift" -> KeyboardModifier.SHIFT;
        case "Control" -> KeyboardModifier.CONTROL;
        case "Alt" -> KeyboardModifier.ALT;
        default -> KeyboardModifier.META;
        };
    }
}
