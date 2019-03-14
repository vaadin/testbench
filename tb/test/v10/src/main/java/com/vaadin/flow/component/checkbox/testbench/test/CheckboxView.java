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
package com.vaadin.flow.component.checkbox.testbench.test;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(CheckboxView.NAV)
@Theme(Lumo.class)
public class CheckboxView extends AbstractView {

  public static final String TEXT = "text";
  public static final String NOTEXT = "notext";
  public static final String CHECKED = "checked";
  public static final String NAV = "Checkbox";

  public CheckboxView() {

    Checkbox checkBoxWithoutText = new Checkbox();
    checkBoxWithoutText.setId(NOTEXT);
    checkBoxWithoutText.addClickListener(e -> {
      log("CheckBox '" + e.getSource().getLabel() + "' clicked");
    });
    add(checkBoxWithoutText);

    Checkbox checkBoxWithText = new Checkbox("Text");
    checkBoxWithText.setId(TEXT);
    checkBoxWithText.addClickListener(e -> {
      log("CheckBox '" + e.getSource().getLabel() + "' clicked");
    });
    add(checkBoxWithText);

    Checkbox initiallyChecked = new Checkbox("Checked initially");
    initiallyChecked.setId(CHECKED);
    initiallyChecked.addClickListener(e -> {
      log("CheckBox '" + e.getSource().getLabel() + "' clicked");
    });
    initiallyChecked.setValue(true);
    add(initiallyChecked);
  }

}
