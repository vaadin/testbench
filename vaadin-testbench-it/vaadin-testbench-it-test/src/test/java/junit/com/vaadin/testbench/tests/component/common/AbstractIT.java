package junit.com.vaadin.testbench.tests.component.common;

import com.vaadin.flow.component.common.testbench.HasLabel;
import com.vaadin.testbench.HasStringValueProperty;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

public abstract class AbstractIT {

    protected String getLogRow(AbstractVaadinPageObject po, int i) {
        return po.findElement(By.id("log")).findElements(By.tagName("div"))
                .get(i)
                .getText();
    }

    protected String getLogRowWithoutNumber(AbstractVaadinPageObject po, int i) {
        return getLogRow(po, i).replaceFirst(".*\\. ", "");
    }

    protected <T extends HasStringValueProperty & HasLabel> void assertStringValue(
            AbstractVaadinPageObject po,
            T element,
            String expectedValue) {

        Assertions.assertEquals(expectedValue, element.getValue());
        Assertions.assertEquals(
                "Value of '" + element.getLabel() + "' is now " + expectedValue,
                getLogRowWithoutNumber(po, 0));
    }

}
