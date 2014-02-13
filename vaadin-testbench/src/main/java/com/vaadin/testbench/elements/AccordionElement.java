/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3.0>.
 */
package com.vaadin.testbench.elements;

import org.openqa.selenium.By;

@ServerClass("com.vaadin.ui.Accordion")
public class AccordionElement extends TabSheetElement {

    {
        // Only difference in using Accordion vs. TabSheet is CSS class name for
        // items
        byTabCell = By.className("v-accordion-item");
    }

}
