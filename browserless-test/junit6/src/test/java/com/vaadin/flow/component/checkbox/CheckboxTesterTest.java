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
package com.vaadin.flow.component.checkbox;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class CheckboxTesterTest extends BrowserlessTest {

    CheckboxView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CheckboxView.class);
        view = navigate(CheckboxView.class);
    }

    @Test
    void readOnlyCheckbox_isNotUsable() {
        view.checkbox.setReadOnly(true);
        Assertions.assertFalse(test(view.checkbox).isUsable(),
                "Readonly checkbox should not be usable");
    }

    @Test
    void click_usable_valueChanges() {
        Assertions.assertFalse(view.checkbox.getValue(),
                "Expecting checkbox initial state not to be checked");

        test(view.checkbox).click();
        Assertions.assertTrue(view.checkbox.getValue(),
                "Expecting checkbox to be checked, but was not");

        test(view.checkbox).click();
        Assertions.assertFalse(view.checkbox.getValue(),
                "Expecting checkbox not to be checked, but was");

    }

    @Test
    void click_usable_checkedChangeFired() {
        AtomicBoolean checkedChange = new AtomicBoolean();
        view.checkbox.getElement().addPropertyChangeListener("checked",
                ev -> checkedChange.set(true));

        Assertions.assertFalse(view.checkbox.getValue(),
                "Expecting checkbox not to be checked, but was");

        test(view.checkbox).click();
        Assertions.assertTrue(checkedChange.get(),
                "Expected checked change event to be fired, but was not");
        Assertions.assertTrue(view.checkbox.getValue(),
                "Expecting checkbox not to be checked, but was");
    }

    @Test
    void click_disabled_throws() {
        view.checkbox.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.checkbox)::click);
    }

    @Test
    void click_disabledByProperty_throws() {
        view.checkbox.setDisabled(true);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.checkbox)::click);
    }

    @Test
    void click_invisible_throws() {
        view.checkbox.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.checkbox)::click);
    }

    @Test
    void click_readOnly_throws() {
        view.checkbox.setReadOnly(true);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.checkbox)::click);
    }

}
