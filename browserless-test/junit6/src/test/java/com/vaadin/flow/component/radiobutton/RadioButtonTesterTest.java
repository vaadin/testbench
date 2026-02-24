/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
