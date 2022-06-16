/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.radiobutton;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.checkbox.CheckboxView;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.TestBenchUnit;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class RadioButtonWrapTest extends TestBenchUnit {

    RadioButtonView view;
    RadioButtonWrap<RadioButton<String>, String> radioButton_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RadioButtonView.class);
        view = navigate(RadioButtonView.class);
        radioButton_ = wrap(view.radioButton);
    }

    @Test
    void click_usable_valueChanges() {
        Assertions.assertFalse(view.radioButton.isCheckedBoolean(),
                "Expecting radioButton initial state not to be checked");

        radioButton_.click();
        Assertions.assertTrue(view.radioButton.isCheckedBoolean(),
                "Expecting radioButton to be checked, but was not");
    }

    @Test
    void click_usable_checkedChangeFired() {
        AtomicBoolean checkedChange = new AtomicBoolean();
        view.radioButton
                .addCheckedChangeListener(ev -> checkedChange.set(true));
        Assertions.assertFalse(view.radioButton.isCheckedBoolean(),
                "Expecting radioButton not to be checked, but was");

        radioButton_.click();
        Assertions.assertTrue(checkedChange.get(),
                "Expected radioButton change event to be fired, but was not");
        Assertions.assertTrue(view.radioButton.isCheckedBoolean(),
                "Expecting radioButton not to be checked, but was");
    }

    @Test
    void click_disabled_throws() {
        view.radioButton.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                radioButton_::click);
    }

    @Test
    void click_disabledByProperty_throws() {
        view.radioButton.setDisabled(true);
        Assertions.assertThrows(IllegalStateException.class,
                radioButton_::click);
    }

    @Test
    void click_invisible_throws() {
        view.radioButton.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                radioButton_::click);
    }

}
