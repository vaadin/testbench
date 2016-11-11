package com.vaadin.tests.testbenchapi.components.listselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class ListSelectOptionClickIT extends MultiBrowserTest {
    ListSelectElement select;
    ListSelectElement multiSelect;
    LabelElement counterLbl;
    LabelElement multiCounterLbl;
    int oneBasedIndex = 2;

    @Before
    public void init() {
        openTestURL();
        select = $(ListSelectElement.class).first();
        multiSelect = $(ListSelectElement.class).get(1);
        counterLbl = $(LabelElement.class).id("counterLbl");
        multiCounterLbl = $(LabelElement.class).id("multiCounterLbl");
    }

    @Test
    @Ignore("needs to be fixed for TB5")
    public void testOptionClick() {
        List<WebElement> options = select.findElements(By.tagName("option"));
        WebElement option = options.get(1);
        option.click();
        checkValueChanged();
    }

    @Test
    @Ignore("needs to be fixed for TB5")
    public void testSelectByText() {
        select.selectByText("item2");
        checkValueChanged();
    }

    @Test
    public void testMultiSelectDeselectByText() {
        multiSelect.selectByText("item2");
        Assert.assertEquals("1: [item1, item2]", multiCounterLbl.getText());
        multiSelect.selectByText("item3");
        Assert.assertEquals("2: [item1, item2, item3]",
                multiCounterLbl.getText());
        multiSelect.deselectByText("item2");
        Assert.assertEquals("3: [item1, item3]", multiCounterLbl.getText());
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
                "1: item2", actualCounter);
    }
}
