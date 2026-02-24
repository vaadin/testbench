/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.radiobutton;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class RadioButtonTesterTest extends UIUnitTest {

    RadioButtonView view;
    RadioButtonTester<RadioButton<String>, String> radioButton_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RadioButtonView.class);
        view = navigate(RadioButtonView.class);
        radioButton_ = test(view.radioButton);
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
        view.radioButton.getElement().addPropertyChangeListener("checked",
                (ev -> checkedChange.set(true)));
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
