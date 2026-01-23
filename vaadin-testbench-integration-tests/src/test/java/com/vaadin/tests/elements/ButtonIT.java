/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testUI.ButtonView;
import com.vaadin.tests.AbstractTB6Test;

public class ButtonIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ButtonView.class;
    }

    @Before
    public void openAndFindElement() {
        openTestURL();
    }

    @Test
    public void buttonClick_singleClick() {
        $(NativeButtonElement.class).id("test-button").click();
        assertEquals("Button single clicked: 1",
                findElement(By.id("single-click")).getText());
        assertEquals("Button focused",
                findElement(By.id("focus-event")).getText());
    }

    @Test
    public void buttonClick_doubleClick() {
        $(NativeButtonElement.class).id("test-button").doubleClick();
        assertEquals("Button double clicked: 2",
                findElement(By.id("double-click")).getText());
        assertEquals("Button focused",
                findElement(By.id("focus-event")).getText());
    }

}
