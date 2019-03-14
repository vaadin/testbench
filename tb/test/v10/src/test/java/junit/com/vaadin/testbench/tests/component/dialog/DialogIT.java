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
package junit.com.vaadin.testbench.tests.component.dialog;


import static com.vaadin.flow.component.dialog.testbench.test.DialogView.NAV;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.dialog.testbench.test.DialogView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class DialogIT extends AbstractIT {

  @VaadinTest
  public void openClose(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final DialogElement dialog = po.dialog().id(DialogView.THE_DIALOG);
    Assertions.assertTrue(dialog.isOpen());

    dialog.$(ButtonElement.class).first().click();
    Assertions.assertFalse(dialog.isOpen());
  }

}
