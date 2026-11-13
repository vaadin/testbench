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
 * A box in CSS pixels, such as an element's:
 *
 * <pre>{@code
 * Rectangle rect = element.getRect();
 * rect.getPoint(); // the top-left corner
 * rect.getDimension(); // the size
 * }</pre>
 *
 * Selenium's {@code Rectangle}, field for field, so a test ports by changing
 * its import; its four-number constructor takes the height before the width, as
 * Selenium's does. Immutable.
 */
public final class Rectangle {

    public final int x;
    public final int y;
    public final int height;
    public final int width;

    /** Note the order: height, then width. */
    public Rectangle(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public Rectangle(Point point, Dimension dimension) {
        this(point.x, point.y, dimension.height, dimension.width);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Rectangle other && other.x == x && other.y == y
                && other.height == height && other.width == width;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, height, width);
    }
}
