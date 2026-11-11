/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * The special keys of the keyboard, to type among ordinary characters:
 *
 * <pre>{@code
 * textField.sendKeys("Jo", Keys.BACK_SPACE, "ane", Keys.ENTER);
 * grid.sendKeys(Keys.chord(Keys.SHIFT, Keys.ARROW_DOWN));
 * }</pre>
 *
 * The constants and their characters are Selenium's {@code Keys}' (selenium-api
 * 4.52.0), so a test ports by changing its import. Each is one character, a W3C
 * WebDriver key code from U+E000 to U+E05D; aliases such as {@link #ARROW_LEFT}
 * share their key's character.
 */
public enum Keys implements CharSequence {
    NULL(''),
    CANCEL(''),
    HELP(''),
    BACK_SPACE(''),
    TAB(''),
    CLEAR(''),
    RETURN(''),
    ENTER(''),
    SHIFT(''),
    LEFT_SHIFT(''),
    CONTROL(''),
    LEFT_CONTROL(''),
    ALT(''),
    LEFT_ALT(''),
    PAUSE(''),
    ESCAPE(''),
    SPACE(''),
    PAGE_UP(''),
    PAGE_DOWN(''),
    END(''),
    HOME(''),
    LEFT(''),
    ARROW_LEFT(''),
    UP(''),
    ARROW_UP(''),
    RIGHT(''),
    ARROW_RIGHT(''),
    DOWN(''),
    ARROW_DOWN(''),
    INSERT(''),
    DELETE(''),
    SEMICOLON(''),
    EQUALS(''),

    NUMPAD0(''),
    NUMPAD1(''),
    NUMPAD2(''),
    NUMPAD3(''),
    NUMPAD4(''),
    NUMPAD5(''),
    NUMPAD6(''),
    NUMPAD7(''),
    NUMPAD8(''),
    NUMPAD9(''),
    MULTIPLY(''),
    ADD(''),
    SEPARATOR(''),
    SUBTRACT(''),
    DECIMAL(''),
    DIVIDE(''),

    F1(''),
    F2(''),
    F3(''),
    F4(''),
    F5(''),
    F6(''),
    F7(''),
    F8(''),
    F9(''),
    F10(''),
    F11(''),
    F12(''),

    META(''),
    COMMAND(''),

    RIGHT_SHIFT(''),
    RIGHT_CONTROL(''),
    RIGHT_ALT(''),
    RIGHT_COMMAND(''),

    OPTION(''),

    /**
     * @deprecated Not a W3C WebDriver key: it types {@link #RIGHT_CONTROL}, as
     *             Selenium's does.
     */
    @Deprecated
    FN(''),

    ZENKAKU_HANKAKU('');

    private final char keyCode;

    Keys(char keyCode) {
        this.keyCode = keyCode;
    }

    public int getCodePoint() {
        return keyCode;
    }

    @Override
    public char charAt(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Length: 1");
        }
        return keyCode;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start == 0 && end == 1) {
            return String.valueOf(keyCode);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return String.valueOf(keyCode);
    }

    /**
     * Presses the keys together and releases them:
     *
     * <pre>{@code
     * Keys.chord(Keys.CONTROL, "a"); // => "a"
     * }</pre>
     *
     * @return the keys followed by {@link #NULL}, which releases the modifiers
     */
    public static String chord(CharSequence... keys) {
        return chord(List.of(keys));
    }

    /** As {@link #chord(CharSequence...)}. */
    public static String chord(Iterable<CharSequence> keys) {
        StringBuilder builder = new StringBuilder();
        for (CharSequence key : keys) {
            builder.append(key);
        }
        builder.append(NULL);
        return builder.toString();
    }

    /**
     * @return the first constant typing {@code key}, an alias's key rather than
     *         the alias, or {@code null} for an ordinary character
     */
    public static @Nullable Keys getKeyFromUnicode(char key) {
        for (Keys unicodeKey : values()) {
            if (unicodeKey.keyCode == key) {
                return unicodeKey;
            }
        }
        return null;
    }
}
