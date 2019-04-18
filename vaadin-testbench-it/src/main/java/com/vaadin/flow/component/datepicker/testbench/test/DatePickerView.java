package com.vaadin.flow.component.datepicker.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.time.LocalDate;

@Route(DatePickerView.NAV)
@Theme(Lumo.class)
public class DatePickerView extends AbstractView {

    public static final String TEXT = "text";
    public static final String NOTEXT = "notext";
    public static final String PRESELECTED = "preselected";
    public static final String NAV = "DatePicker";

    public DatePickerView() {
        DatePicker datePickerWithoutText = new DatePicker();
        datePickerWithoutText.setId(NOTEXT);
        datePickerWithoutText.addValueChangeListener(e -> {
            log("DatePicker '" + e.getSource().getLabel()
                    + "' value changed to " + e.getValue());
        });
        add(datePickerWithoutText);

        DatePicker datePickerWithText = new DatePicker("Text");
        datePickerWithText.setId(TEXT);
        datePickerWithText.addValueChangeListener(e -> {
            log("DatePicker '" + e.getSource().getLabel()
                    + "' value changed to " + e.getValue());
        });
        add(datePickerWithText);

        DatePicker datePickerWithPreSelectedDate = new DatePicker(
                "Pre selected date (2015-07-13)");
        datePickerWithPreSelectedDate.setValue(LocalDate.of(2015, 7, 13));
        datePickerWithPreSelectedDate.setId(PRESELECTED);
        datePickerWithPreSelectedDate.addValueChangeListener(e -> {
            log("DatePicker '" + e.getSource().getLabel()
                    + "' value changed to " + e.getValue());
        });
        add(datePickerWithPreSelectedDate);
    }

}
