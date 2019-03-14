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

import static com.vaadin.flow.component.tabs.testbench.test.TabsView.DEFAULT;
import static com.vaadin.flow.component.tabs.testbench.test.TabsView.NAV;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.common.testbench.test.AbstractIT;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testbench.parallel.BrowserUtil;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinWebUnitTest
public class TabsIT extends AbstractIT {

  public static final String TEXT = "Text";
  public static final String DISABLED = "Disabled";

  @VaadinWebUnitTest
  public void selectTabByIndex(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);
    final TabsElement def = po.tabs().id(DEFAULT);

    Assertions.assertEquals(0 , def.getSelectedTabIndex());
    def.setSelectedTabIndex(2);
    Assertions.assertEquals(2 , def.getSelectedTabIndex());
    def.setSelectedTabIndex(0);
    Assertions.assertEquals(0 , def.getSelectedTabIndex());
  }

  @VaadinWebUnitTest
  public void getSelectedTabElement(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    // https://github.com/vaadin/vaadin-tabs-flow/issues/27
//    if (BrowserUtil.isEdge(getDesiredCapabilities())
//        || BrowserUtil.isFirefox(getDesiredCapabilities())
//        || BrowserUtil.isIE(getDesiredCapabilities())) {
//      return;
//    }
    final TabsElement def = po.tabs().id(DEFAULT);

    def.getSelectedTabElement().$(ButtonElement.class).first().click();
    Assertions.assertEquals("2. Hello clicked" , getLogRow(po,0));
    def.setSelectedTabIndex(2);
    Assertions.assertEquals(TEXT , def.getSelectedTabElement().getText());
  }

  @VaadinWebUnitTest
  public void getTab(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final TabsElement def = po.tabs().id(DEFAULT);

    Assertions.assertEquals(1 , def.getTab(DISABLED));
    Assertions.assertEquals(2 , def.getTab(TEXT));
  }

  @VaadinWebUnitTest
  public void isEnabled(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final TabsElement def = po.tabs().id(DEFAULT);

    Assertions.assertTrue(def.getTabElement(TEXT).isEnabled());
    Assertions.assertFalse(def.getTabElement(DISABLED).isEnabled());

  }
}
