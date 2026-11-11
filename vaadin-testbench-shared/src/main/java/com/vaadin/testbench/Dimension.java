/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Objects;

/**
 * A size in CSS pixels, such as an element's:
 *
 * <pre>{@code
 * Dimension size = element.getSize();
 * size.getWidth(); // or size.width
 * }</pre>
 *
 * Selenium's {@code Dimension}, field for field, so a test ports by changing
 * its import. Immutable.
 */
public final class Dimension {

    public final int width;
    public final int height;

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Dimension other && other.width == width
                && other.height == height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    /** {@code "(800, 600)"}, width first, as Selenium's. */
    @Override
    public String toString() {
        return String.format("(%d, %d)", width, height);
    }
}
