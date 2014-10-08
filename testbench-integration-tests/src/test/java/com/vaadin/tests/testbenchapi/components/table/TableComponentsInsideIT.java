package com.vaadin.tests.testbenchapi.components.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractFieldElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class TableComponentsInsideIT extends MultiBrowserTest {

    private TableElement table;

    @Before
    public void init() {
        openTestURL();
        table = $(TableElement.class).first();
    }

    @Test
    public void getButtonFromTable() {
        final int INDEX = 0;
        TableRowElement row = table.getRow(INDEX);
        ButtonElement expected = $(ButtonElement.class).id("btn" + INDEX);
        ButtonElement actual = row.getElementInCell(3, ButtonElement.class);

        expected.click();
        NotificationElement not = $(NotificationElement.class).first();
        String expectedString = not.getText();
        not.closeNotification();
        actual.click();
        not = $(NotificationElement.class).first();
        String actualString = not.getText();
        Assert.assertEquals(
                "TableRowElement.getElementInCell() produces different result from searching by ElementQuery $(TestBenchElement.class)",
                expectedString, actualString);
    }

    @Test
    public void getCheckBoxTable() {
        final int INDEX = 3;
        TableRowElement row = table.getRow(INDEX);
        CheckBoxElement expected = $(CheckBoxElement.class).get(INDEX);
        CheckBoxElement actual = row.getElementInCell(1, CheckBoxElement.class);
        checkComponentValues(expected, actual);
    }

    @Test
    public void getTextFieldFromTable() {
        final int INDEX = 3;
        TableRowElement row = table.getRow(INDEX);

        TextFieldElement expected = row.getElementInCell(2,
                TextFieldElement.class);
        TextFieldElement actual = $(TextFieldElement.class).id("tf" + INDEX);
        checkComponentValues(expected, actual);
    }

    /*
     * We can't compare objects, because when searching a webelement, selenium
     * returns a new object all the the time for new search. That's why we are
     * checking the values.
     */
    private void checkComponentValues(AbstractFieldElement expected,
            AbstractFieldElement actual) {
        String expectedString = expected.getValue();
        String actualString = actual.getValue();
        Assert.assertEquals(
                "TableRowElement.getElementInCell() produces different result from searching by ElementQuery $(TestBenchElement.class)",
                expectedString, actualString);
    }
}
