/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

public final class SerializationDebugUtil {

    private SerializationDebugUtil() {
    }

    /**
     * Asserts that the given object graph is fully serializable. If not, throws
     * an AssertionError with a detailed report of non-serializable fields
     * found.
     *
     * @param root
     *            the root object to test for serializability
     */
    public static void assertSerializable(Object root) {
        try {
            serialize(root);
        } catch (NotSerializableException e) {
            throw new AssertionError(
                    "Serialization failed: " + e.getMessage() + "\n", e);
        } catch (IOException ioe) {
            throw new AssertionError(
                    "Unexpected IO failure during serialization: "
                            + ioe.getMessage(),
                    ioe);
        }
    }

    private static void serialize(Object o) throws IOException {
        var propertyName = "sun.io.serialization.extendedDebugInfo";
        var ori = System.getProperty(propertyName);
        System.setProperty(propertyName, "true");
        if (o == null)
            return;
        try (var bos = new ByteArrayOutputStream();
                var oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
        } finally {
            if (ori == null) {
                System.clearProperty(propertyName);
            } else {
                System.setProperty(propertyName, ori);
            }
        }
    }
}
