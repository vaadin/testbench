/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.checkbox;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class CheckboxWrapTest extends UIUnitTest {

    CheckboxView view;
    CheckboxWrap<Checkbox> checkbox_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CheckboxView.class);
        view = navigate(CheckboxView.class);
        checkbox_ = wrap(view.checkbox);
    }

    @Test
    void readOnlyCheckbox_isNotUsable() {
        view.checkbox.setReadOnly(true);
        Assertions.assertFalse(checkbox_.isUsable(),
                "Readonly checkbox should not be usable");
    }

    @Test
    void click_usable_valueChanges() {
        Assertions.assertFalse(view.checkbox.getValue(),
                "Expecting checkbox initial state not to be checked");

        checkbox_.click();
        Assertions.assertTrue(view.checkbox.getValue(),
                "Expecting checkbox to be checked, but was not");

        checkbox_.click();
        Assertions.assertFalse(view.checkbox.getValue(),
                "Expecting checkbox not to be checked, but was");

    }

    @Test
    void click_usable_checkedChangeFired() {
        AtomicBoolean checkedChange = new AtomicBoolean();
        view.checkbox.addCheckedChangeListener(ev -> checkedChange.set(true));
        Assertions.assertFalse(view.checkbox.getValue(),
                "Expecting checkbox not to be checked, but was");

        checkbox_.click();
        Assertions.assertTrue(checkedChange.get(),
                "Expected checked change event to be fired, but was not");
        Assertions.assertTrue(view.checkbox.getValue(),
                "Expecting checkbox not to be checked, but was");
    }

    @Test
    void click_disabled_throws() {
        view.checkbox.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class, checkbox_::click);
    }

    @Test
    void click_disabledByProperty_throws() {
        view.checkbox.setDisabled(true);
        Assertions.assertThrows(IllegalStateException.class, checkbox_::click);
    }

    @Test
    void click_invisible_throws() {
        view.checkbox.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class, checkbox_::click);
    }

    @Test
    void click_readOnly_throws() {
        view.checkbox.setReadOnly(true);
        Assertions.assertThrows(IllegalStateException.class, checkbox_::click);
    }

}
