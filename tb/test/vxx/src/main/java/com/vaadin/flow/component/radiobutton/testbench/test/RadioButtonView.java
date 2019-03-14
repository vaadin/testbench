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
package com.vaadin.flow.component.radiobutton.testbench.test;

import java.util.stream.IntStream;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(RadioButtonView.NAV)
@Theme(Lumo.class)
public class RadioButtonView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String PRESELECTED = "preselected";
  public static final String NAV = "RadioButton";

  public RadioButtonView() {
    RadioButtonGroup<String> radioButtons = new RadioButtonGroup<>();
    radioButtons.setId(DEFAULT);
    radioButtons
        .setItems(IntStream.range(0 , 5).mapToObj(i -> ("Item " + i)));
    radioButtons.addValueChangeListener(e -> {
      log("RadioButtonGroup 'default' value changed to " + e.getValue());
    });

    RadioButtonGroup<String> preselected = new RadioButtonGroup<>();
    preselected.setId(PRESELECTED);
    preselected
        .setItems(IntStream.range(0 , 5).mapToObj(i -> ("Item " + i)));
    preselected.setValue("Item 3");
    preselected.addValueChangeListener(e -> {
      log("RadioButtonGroup 'preselected' value changed to "
          + e.getValue());
    });

    add(radioButtons , new Hr() , preselected);
  }

}
