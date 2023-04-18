/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Provides a workaround to avoid "cyclic object value" when returning an
 * element list (webdrivers use JSON.stringify([element1, ..., elementN])
 * internally).
 */
public class CyclicObjectWorkaround {

    private static final String TEMPLATE;
    static {
        try {
            TEMPLATE = readInputStream(
                    CyclicObjectWorkaround.class
                            .getResourceAsStream("cyclic-object-workaround.js"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String readInputStream(InputStream is) throws IOException {
        try (final InputStream inputStream = is) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }
    }

    /**
    /**
     * Produces Javascript which modifies a given object (list) so that it can
     * be returned to the test.
     *
     * @param jsObjectName
     *            the name of the Javascript object to manipulate
     * @return JS code which manipulates the object
     */
    public static String get(String jsObjectName) {
        return TEMPLATE.replace("jsObject", jsObjectName);
    }
}
