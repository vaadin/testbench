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
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class SerializationDebugUtil {

    private SerializationDebugUtil() {
    }

    /**
     * Asserts that the given object graph is fully serializable. If not,
     * throws an AssertionError with a detailed report of non-serializable
     * fields found.
     *
     * @param root the root object to test for serializability
     */
    public static void assertSerializable(Object root) {
        try {
            serialize(root);
        } catch (NotSerializableException e) {
            String report = buildReport(root, e);
            throw new AssertionError(
                    "Serialization failed: " + e.getMessage() + "\n" + report,
                    e);
        } catch (IOException ioe) {
            throw new AssertionError(
                    "Unexpected IO failure during serialization: "
                            + ioe.getMessage(),
                    ioe);
        }
    }

    private static void serialize(Object o) throws IOException {
        if (o == null)
            return;
        try (var bos = new ByteArrayOutputStream();
                var oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
        }
    }

    private static String buildReport(Object root,
            NotSerializableException original) {
        StringBuilder sb = new StringBuilder();
        sb.append("---- Serialization Debug Report ----\n");
        sb.append("Root type: ").append(root.getClass().getName()).append('\n');
        sb.append("Original exception: ").append(original).append('\n');

        Set<Object> visited = Collections
                .newSetFromMap(new IdentityHashMap<>());
        List<String> offenders = new ArrayList<>();
        inspectObject(root, "root", visited, offenders);

        if (offenders.isEmpty()) {
            sb.append(
                    "No direct non-Serializable fields found (might be writeReplace/readObject or proxy issues).\n");
        } else {
            sb.append("Non-serializable field paths:\n");
            offenders.forEach(p -> sb.append("  - ").append(p).append('\n'));
        }
        sb.append("------------------------------------");
        return sb.toString();
    }

    private static void inspectObject(Object obj, String path,
            Set<Object> visited, List<String> offenders) {
        if (obj == null || visited.contains(obj))
            return;
        visited.add(obj);

        Class<?> cls = obj.getClass();

        // Skip Java core known immutable serializable types quickly
        if (isKnownSerializableLeaf(cls))
            return;

        // If object itself not Serializable, record and do not dive further (to
        // avoid noise)
        if (!(obj instanceof Serializable)) {
            offenders.add(path + " (" + cls.getName() + ")");
            return;
        }

        // Dive into fields
        for (Field f : getAllFields(cls)) {
            if (shouldSkip(f))
                continue;
            f.setAccessible(true);
            Object value;
            try {
                value = f.get(obj);
            } catch (IllegalAccessException ignored) {
                continue;
            }
            if (value == null)
                continue;

            String childPath = path + "." + f.getName();
            if (!(value instanceof Serializable)) {
                offenders.add(
                        childPath + " (" + value.getClass().getName() + ")");
                continue;
            }

            // Try serializing field alone to catch nested problematic graphs
            try {
                serialize(value);
            } catch (NotSerializableException nse) {
                // Dive deeper to isolate
                inspectObject(value, childPath, visited, offenders);
            } catch (IOException ignored) {
                // Ignore other IO issues for this isolated attempt
            }
        }
    }

    private static boolean shouldSkip(Field f) {
        int mod = f.getModifiers();
        return Modifier.isStatic(mod) || Modifier.isTransient(mod);
    }

    private static List<Field> getAllFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        while (cls != null && cls != Object.class) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return fields;
    }

    private static boolean isKnownSerializableLeaf(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class
                || Number.class.isAssignableFrom(cls) || cls == Boolean.class
                || cls == Character.class || cls.isEnum()
                || cls.getName().startsWith("java.time.");
    }
}
