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
