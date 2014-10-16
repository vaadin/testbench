package com.vaadin.tests.testbenchapi.components.listselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class ListSelectOptionClickIT extends MultiBrowserTest {
    ListSelectElement select;
    LabelElement counterLbl;
    int oneBasedIndex = 2;

    @Before
    public void init() {
        openTestURL();
        select = $(ListSelectElement.class).first();
        counterLbl = $(LabelElement.class).id("counterLbl");
    }

    @Test
    public void testOptionClick() {
        List<WebElement> options = select.findElements(By.tagName("option"));
        WebElement option = options.get(1);
        option.click();
        checkValueChanged();
    }

    @Test
    public void testSelectByText() {
        select.selectByText("item2");
        checkValueChanged();
    }

    /*
     * Checks that value has changed. Checks that the change event was fired
     * once.
     */
    private void checkValueChanged() {
        String actual = select.getValue();
        String actualCounter = counterLbl.getText();
        Assert.assertEquals("The value of the ListSelect has not changed",
                "item2", actual);
        Assert.assertEquals(
                "The number of list select valueChange events is not one.",
                "1", actualCounter);
    }
}
