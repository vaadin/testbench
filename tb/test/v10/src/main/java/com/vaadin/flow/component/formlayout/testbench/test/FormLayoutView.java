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
package com.vaadin.flow.component.formlayout.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(FormLayoutView.NAV)
@Theme(Lumo.class)
public class FormLayoutView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String NAV = "FormLayout";

  public FormLayoutView() {
    add(new TextField("First name"));
    FormLayout layout = new FormLayout();
    layout.setId(DEFAULT);
    layout.add(new TextField("First name"));
    layout.add(new TextField("Last name"));

    add(layout);
  }

}
