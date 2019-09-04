package com.vaadin.testbench.tests.component.common;

import com.vaadin.testbench.HasLabel;
import com.vaadin.testbench.HasStringValueProperty;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

@VaadinTest
public abstract class AbstractIT {

    protected String getLogRow(VaadinPageObject po, int i) {
        return po.findElement(By.id("log")).findElements(By.tagName("div"))
                .get(i)
                .getText();
    }

    protected String getLogRowWithoutNumber(VaadinPageObject po, int i) {
        return getLogRow(po, i).replaceFirst(".*\\. ", "");
    }

    protected <T extends HasStringValueProperty & HasLabel> void assertStringValue(
            VaadinPageObject po,
            T element,
            String expectedValue) {

        Assertions.assertEquals(expectedValue, element.getValue());
        Assertions.assertEquals(
                "Value of '" + element.getLabel() + "' is now " + expectedValue,
                getLogRowWithoutNumber(po, 0));
    }

}
