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
package com.vaadin.flow.component.tabs.testbench.test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(TabsView.NAV)
@Theme(Lumo.class)
public class TabsView extends AbstractView {

  public static final String DEFAULT = "default";
  public static final String NAV = "Tabs";

  public TabsView() {

    Tabs tabs = new Tabs();
    tabs.setId(DEFAULT);

    Tab componentTab = new Tab(
        new Button("Hello" , e -> log("Hello clicked")));
    Tab textTab = new Tab("Text");
    tabs.addSelectedChangeListener(e -> log(
        "Tab '" + e.getSource().getSelectedIndex() + "' selected"));
    Tab disabledTab = new Tab("Disabled");
    disabledTab.setEnabled(false);

    tabs.add(componentTab , disabledTab , textTab);
    add(tabs);
  }

}
