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
 * A position in CSS pixels, such as an element's top-left corner:
 *
 * <pre>{@code
 * Point location = element.getLocation();
 * location.getX(); // or location.x
 * }</pre>
 *
 * Selenium's {@code Point}, field for field, so a test ports by changing its
 * import. Immutable.
 */
public final class Point {

    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /** @return a new point, offset by the given pixels */
    public Point moveBy(int xOffset, int yOffset) {
        return new Point(x + xOffset, y + yOffset);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point other && other.x == x && other.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /** {@code "(10, 20)"}, as Selenium's. */
    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
