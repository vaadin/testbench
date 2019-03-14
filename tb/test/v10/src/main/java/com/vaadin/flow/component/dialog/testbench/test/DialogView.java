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
package com.vaadin.flow.component.dialog.testbench.test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(DialogView.NAV)
@Theme(Lumo.class)
public class DialogView extends AbstractView {

  public static final String THE_DIALOG = "the-dialog";
  public static final String NAV = "Dialog";
  private Dialog dialog;

  public DialogView() {
    dialog = new Dialog();
    dialog.setId(THE_DIALOG);
    dialog.add(new Label("This is the contents of the dialog"));
    dialog.add(new Button("Close" , e -> dialog.close()));
    dialog.open();
  }

}
