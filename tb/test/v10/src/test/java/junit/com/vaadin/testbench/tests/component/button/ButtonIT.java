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
package junit.com.vaadin.testbench.tests.component.button;

import static com.vaadin.flow.component.button.testbench.test.ButtonView.NAV;
import static com.vaadin.flow.component.button.testbench.test.ButtonView.NOTEXT;
import static com.vaadin.flow.component.button.testbench.test.ButtonView.TEXT;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
class ButtonIT extends AbstractIT {

  @VaadinTest(preLoad = false)
  void click(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final ButtonElement buttonWithText = po.btn().id(TEXT);
    final ButtonElement buttonWithNoText = po.btn().id(NOTEXT);

    buttonWithNoText.click();
    Assertions.assertEquals("1. Button without text clicked" , getLogRow(po , 0));
    buttonWithText.click();
    Assertions.assertEquals("2. Button with text clicked" , getLogRow(po , 0));
  }

  @VaadinTest
  void getText(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final ButtonElement buttonWithText = po.btn().id(TEXT);
    final ButtonElement buttonWithNoText = po.btn().id(NOTEXT);
    Assertions.assertEquals("" , buttonWithNoText.getText());
    Assertions.assertEquals("Text" , buttonWithText.getText());
  }

}
