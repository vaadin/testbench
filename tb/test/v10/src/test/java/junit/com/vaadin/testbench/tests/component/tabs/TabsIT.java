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
package junit.com.vaadin.testbench.tests.component.tabs;

import static com.vaadin.flow.component.tabs.testbench.test.TabsView.DEFAULT;
import static com.vaadin.flow.component.tabs.testbench.test.TabsView.NAV;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.FIREFOX;
import static com.vaadin.testbench.addons.webdriver.BrowserTypes.IE;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class TabsIT extends AbstractIT {

  public static final String TEXT = "Text";
  public static final String DISABLED = "Disabled";

  @VaadinTest(navigateAsString = NAV)
  public void selectTabByIndex(GenericTestPageObject po) throws Exception {
    final TabsElement def = po.tabs().id(DEFAULT);
    Assertions.assertEquals(0 , def.getSelectedTabIndex());
    def.setSelectedTabIndex(2);
    Assertions.assertEquals(2 , def.getSelectedTabIndex());
    def.setSelectedTabIndex(0);
    Assertions.assertEquals(0 , def.getSelectedTabIndex());
  }


  /**
   * https://github.com/vaadin/vaadin-tabs-flow/issues/27
   * @param po
   * @throws Exception
   */
  @VaadinTest(navigateAsString = NAV)
  @SkipBrowsers(value = {FIREFOX, IE})
  public void getSelectedTabElement(GenericTestPageObject po) throws Exception {
    final TabsElement def = po.tabs().id(DEFAULT);

    def.getSelectedTabElement().$(ButtonElement.class).first().click();
    Assertions.assertEquals("2. Hello clicked" , getLogRow(po,0));
    def.setSelectedTabIndex(2);
    Assertions.assertEquals(TEXT , def.getSelectedTabElement().getText());
  }

  @VaadinTest(navigateAsString = NAV)
  public void getTab(GenericTestPageObject po) throws Exception {
    final TabsElement def = po.tabs().id(DEFAULT);

    Assertions.assertEquals(1 , def.getTab(DISABLED));
    Assertions.assertEquals(2 , def.getTab(TEXT));
  }

  @VaadinTest(navigateAsString = NAV)
  public void isEnabled(GenericTestPageObject po) throws Exception {
    final TabsElement def = po.tabs().id(DEFAULT);

    Assertions.assertTrue(def.getTabElement(TEXT).isEnabled());
    Assertions.assertFalse(def.getTabElement(DISABLED).isEnabled());

  }
}
