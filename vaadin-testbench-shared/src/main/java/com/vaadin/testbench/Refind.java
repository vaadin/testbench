/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A script given an element whose node is gone runs once more, on what that
 * element's query finds now ({@code D_element_resolution}). Only the stale
 * elements are found again, and only those that came from a query.
 */
final class Refind {

    private Refind() {
    }

    /**
     * @param args
     *            the script's arguments: elements, also inside lists, arrays
     *            and maps
     */
    static <R> R onStale(Supplier<R> call, Object... args) {
        try {
            return call.get();
        } catch (StaleElementReferenceException stale) {
            boolean refound = false;
            for (TestBenchElement element : elementsIn(args)) {
                if (element.canRefind() && element.isStale()
                        && element.refind()) {
                    refound = true;
                }
            }
            if (!refound) {
                throw stale;
            }
            return call.get();
        }
    }

    private static List<TestBenchElement> elementsIn(Object value) {
        List<TestBenchElement> elements = new ArrayList<>();
        collect(value, elements);
        return elements;
    }

    private static void collect(Object value, List<TestBenchElement> into) {
        if (value instanceof TestBenchElement element) {
            into.add(element);
        } else if (value instanceof WebElementAdapter adapter) {
            into.add(adapter.getElement());
        } else if (value instanceof Object[] array) {
            for (Object item : array) {
                collect(item, into);
            }
        } else if (value instanceof Collection<?> collection) {
            collection.forEach(item -> collect(item, into));
        } else if (value instanceof Map<?, ?> map) {
            map.values().forEach(item -> collect(item, into));
        }
    }
}
