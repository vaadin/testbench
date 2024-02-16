/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.commands;

import org.openqa.selenium.Keys;

import com.vaadin.testbench.elementsbase.AbstractElement;

public interface TestBenchElementCommands {

    /**
     * Set focus to this element.
     */
    void focus();

    /**
     * Shows the tool tip of the specified element.
     */
    void showTooltip();

    /**
     * Scrolls the element down to the specified top value.
     *
     * @param scrollTop
     *            the new value for scrollTop.
     */
    void scroll(int scrollTop);

    /**
     * Scrolls the element left to the specified left value.
     *
     * @param scrollLeft
     *            the new value for scrollLeft.
     */
    void scrollLeft(int scrollLeft);

    /**
     * Clicks at the specified coordinates on an element while pressing possible
     * modifier keys. The coordinates are relative to top left on the element.
     *
     * @param x
     *            the offset from the left position of the element
     * @param y
     *            the offset from the top position of the element
     * @param modifiers
     *            any modifier keys to press while clicking the element
     *            (optional).
     */
    void click(int x, int y, Keys... modifiers);

    /**
     * Decorates the element with the specified Element type, making it possible
     * to use Vaadin component-specific API on elements found using standard
     * selenium API.
     * <p>
     * Example: <code>
     *     WebElement e = driver.findElement(By.id("my-table"));
     *     TableElement table = testBenchElement(e).wrap(TableElement.class);
     *     assertEquals("Foo", table.getHeaderCell(1).getText());
     * </code>
     *
     * @param elementType
     *            The type (class) containing the API to decorate with. Must
     *            extend
     *            {@link com.vaadin.testbench.elementsbase.AbstractElement}.
     * @return The element wrapped in an instance of the specified element type.
     */
    <T extends AbstractElement> T wrap(Class<T> elementType);
}
