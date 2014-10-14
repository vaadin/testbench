package com.vaadin.tests.testbenchapi.components.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class TableElementContextMenuIT extends MultiBrowserTest {

    @Test
    public void testTableContextClick() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        table.contextClick();
        List<WebElement> contextMenu = getDriver().findElements(
                By.className("v-contextmenu"));
        Assert.assertFalse(
                "There is no context menu open by tableElement.contextClick()",
                contextMenu.isEmpty());
    }
}
