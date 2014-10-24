package com.vaadin.tests.testbenchapi.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testUI.DateFieldPopupSetValue;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

public class DateFieldPopupSetValueIT extends MultiBrowserTest {

    LabelElement counter;
    PopupDateFieldElement dfPopup;
    DateFieldElement df;

    @Before
    public void init() {
        openTestURL();
        counter = $(LabelElement.class).id("counter");
        df = $(DateFieldElement.class).first();
    }

    @Test
    public void testGetValue() {
        String value = df.getValue();
        Assert.assertEquals("04/12/15", value);
    }

    @Test
    public void testSetValue() {
        Date date = DateFieldPopupSetValue.changedDate;
        String value = (new SimpleDateFormat("MM/dd/yy")).format(date);
        df.setValue(value);
        Assert.assertEquals("06/11/15", df.getValue());
    }

    @Test
    public void testValueChanged() {
        Date date = DateFieldPopupSetValue.changedDate;
        String value = (new SimpleDateFormat("MM/dd/yy")).format(date);
        df.setValue(value);
        counter.waitForVaadin();
        Assert.assertEquals("1", counter.getText());
    }
}
