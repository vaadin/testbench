package com.vaadin.testbench;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

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
            return result.toString(UTF_8.name());
        }
    }

    /**
     * Produces Javascript which modifies a given object (list) so that it can
     * be returned to the test.
     *
     * @param jsObjectName the name of the Javascript object to manipulate
     * @return JS code which manipulates the object
     */
    public static String get(String jsObjectName) {
        return TEMPLATE.replace("jsObject", jsObjectName);
    }
}
