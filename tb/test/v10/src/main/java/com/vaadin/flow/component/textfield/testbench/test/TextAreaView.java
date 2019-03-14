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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(TextAreaView.NAV)
@Theme(Lumo.class)
public class TextAreaView extends AbstractView {

  public static final String LABEL_EAGER = "text";
  public static final String NOLABEL = "notext";
  public static final String INITIAL_VALUE = "initialvalue";
  public static final String PLACEHOLDER = "placeholder";
  public static final String NAV = "TextArea";

  public TextAreaView() {

    TextArea textAreaNoLabel = new TextArea();
    textAreaNoLabel.setId(NOLABEL);
    textAreaNoLabel.addValueChangeListener(this::onValueChange);
    add(textAreaNoLabel);

    TextArea textAreaLabel = new TextArea("Label (eager)");
    textAreaLabel.setValueChangeMode(ValueChangeMode.EAGER);
    textAreaLabel.setId(LABEL_EAGER);
    textAreaLabel.addValueChangeListener(this::onValueChange);
    add(textAreaLabel);

    TextArea textAreaInitialValue = new TextArea("Has an initial value");
    textAreaInitialValue.setId(INITIAL_VALUE);
    textAreaInitialValue.setValue("Initial");
    textAreaInitialValue.addValueChangeListener(this::onValueChange);
    add(textAreaInitialValue);

    TextArea textAreaPlaceholder = new TextArea("Has a placeholder");
    textAreaPlaceholder.setId(PLACEHOLDER);
    textAreaPlaceholder.setPlaceholder("Text goes here");
    textAreaPlaceholder.addValueChangeListener(this::onValueChange);
    add(textAreaPlaceholder);
  }

  protected void onValueChange(ComponentValueChangeEvent<TextArea, String> e) {
    String label = e.getSource().getLabel();
    if (label == null) {
      label = "";
    }
    log("Value of '" + label + "' is now " + e.getValue());
  }

}
