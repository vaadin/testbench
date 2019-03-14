/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.textfield.testbench.test;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(PasswordFieldView.NAV)
@Theme(Lumo.class)
public class PasswordFieldView extends AbstractView {

  public static final String LABEL_EAGER = "text";
  public static final String NOLABEL = "notext";
  public static final String INITIAL_VALUE = "initialvalue";
  public static final String PLACEHOLDER = "placeholder";
  public static final String NAV = "PasswordField";

  public PasswordFieldView() {

    PasswordField passwordFieldNoLabel = new PasswordField();
    passwordFieldNoLabel.setId(NOLABEL);
    passwordFieldNoLabel.addValueChangeListener(this::onValueChange);
    add(passwordFieldNoLabel);

    PasswordField passwordFieldLabel = new PasswordField("Label (eager)");
    passwordFieldLabel.setValueChangeMode(ValueChangeMode.EAGER);
    passwordFieldLabel.setId(LABEL_EAGER);
    passwordFieldLabel.addValueChangeListener(this::onValueChange);
    add(passwordFieldLabel);

    PasswordField passwordFieldInitialValue = new PasswordField(
        "Has an initial value");
    passwordFieldInitialValue.setId(INITIAL_VALUE);
    passwordFieldInitialValue.setValue("Initial");
    passwordFieldInitialValue.addValueChangeListener(this::onValueChange);
    add(passwordFieldInitialValue);

    PasswordField passwordFieldPlaceholder = new PasswordField(
        "Has a placeholder");
    passwordFieldPlaceholder.setId(PLACEHOLDER);
    passwordFieldPlaceholder.setPlaceholder("Text goes here");
    passwordFieldPlaceholder.addValueChangeListener(this::onValueChange);
    add(passwordFieldPlaceholder);
  }

  protected void onValueChange(ComponentValueChangeEvent<PasswordField, String> e) {
    String label = e.getSource().getLabel();
    if (label == null) {
      label = "";
    }
    log("Value of '" + label + "' is now " + e.getValue());
  }

}
