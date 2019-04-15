package com.vaadin.testbench.tests.component.datepicker;

import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.datepicker.testbench.test.DatePickerView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

import static com.vaadin.flow.component.datepicker.testbench.test.DatePickerView.NAV;

@VaadinTest
public class DatePickerIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void setGetDate(GenericTestPageObject po) throws Exception {
        final DatePickerElement datePickerWithText = po.$(DatePickerElement.class).id(DatePickerView.TEXT);
        final DatePickerElement datePickerWithNoText = po.$(DatePickerElement.class).id(DatePickerView.NOTEXT);
        final DatePickerElement datePickerWithPreSelectedDate
                = po.$(DatePickerElement.class).id(DatePickerView.PRESELECTED);

        Assertions.assertNull(datePickerWithText.getDate());
        Assertions.assertNull(datePickerWithNoText.getDate());
        Assertions.assertEquals(LocalDate.of(2015, 7, 13),
                datePickerWithPreSelectedDate.getDate());

        LocalDate newDate = LocalDate.of(2018, 3, 22);
        datePickerWithNoText.setDate(newDate);

        Assertions.assertTrue(getLogRow(po, 0).contains(
                "DatePicker 'null' value changed to 2018-03-22"));

        Assertions.assertEquals(newDate, datePickerWithNoText.getDate());

        datePickerWithText.setDate(newDate);
        Assertions.assertTrue(getLogRow(po, 0).contains(
                "DatePicker 'Text' value changed to 2018-03-22"));
        Assertions.assertEquals(newDate, datePickerWithText.getDate());

        datePickerWithPreSelectedDate.setDate(newDate);
        Assertions.assertTrue(getLogRow(po, 0).contains(
                "DatePicker 'Pre selected date (2015-07-13)' value changed to 2018-03-22"));
        Assertions.assertEquals(newDate, datePickerWithPreSelectedDate.getDate());

    }

    @VaadinTest(navigateTo = NAV)
    public void clear(GenericTestPageObject po) {
        final DatePickerElement datePickerWithText = po.$(DatePickerElement.class).id(DatePickerView.TEXT);
        final DatePickerElement datePickerWithNoText = po.$(DatePickerElement.class).id(DatePickerView.NOTEXT);
        final DatePickerElement datePickerWithPreSelectedDate
                = po.$(DatePickerElement.class).id(DatePickerView.PRESELECTED);

        datePickerWithText.clear();
        Assertions.assertNull(datePickerWithText.getDate());
        datePickerWithNoText.clear();
        Assertions.assertNull(datePickerWithNoText.getDate());
        datePickerWithPreSelectedDate.clear();
        Assertions.assertNull(datePickerWithPreSelectedDate.getDate());

    }

    @VaadinTest(navigateTo = NAV)
    public void getLabel(GenericTestPageObject po) throws Exception {
        final DatePickerElement datePickerWithText = po.$(DatePickerElement.class).id(DatePickerView.TEXT);
        final DatePickerElement datePickerWithNoText = po.$(DatePickerElement.class).id(DatePickerView.NOTEXT);

        Assertions.assertEquals("", datePickerWithNoText.getLabel());
        Assertions.assertEquals("Text", datePickerWithText.getLabel());
    }

}
