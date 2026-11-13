/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.playwright;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Selenium's reads as script bodies, element in {@code arguments[0]}: the atoms
 * in {@code atoms/}, whose {@code README.md} says where each comes from.
 */
final class Atoms {

    /** Selenium's {@code getAttribute(name)}, name in {@code arguments[1]}. */
    static final String GET_ATTRIBUTE = call("getAttribute.js");

    static final String IS_DISPLAYED = call("isDisplayed.js");

    /** Selenium's {@code getText()} on Chrome. */
    static final String GET_TEXT = call("get-text.js");

    private Atoms() {
    }

    // As Selenium and chromedriver call their atoms
    private static String call(String file) {
        return "return (" + read(file) + ").apply(null, arguments);";
    }

    private static String read(String file) {
        try (InputStream in = Atoms.class
                .getResourceAsStream("atoms/" + file)) {
            if (in == null) {
                throw new IllegalStateException("No resource atoms/" + file);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
