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
package junit.com.vaadin.testbench.tests.component.ironlist;

import static com.vaadin.flow.component.ironlist.testbench.test.IronListView.HUNDRED_THOUSAND;
import static com.vaadin.flow.component.ironlist.testbench.test.IronListView.NAV;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.ironlist.testbench.IronListElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class IronListIT extends AbstractIT {

  @VaadinTest(navigateAsString = NAV)
  public void scrollTo(GenericTestPageObject po) throws Exception {
    final IronListElement def = po.ironList().id(HUNDRED_THOUSAND);

    def.scrollToRow(1000);
    Assertions.assertEquals(1000.0 , def.getFirstVisibleRowIndex() , 2);
  }

  @VaadinTest(navigateAsString = NAV)
  public void rowCount(GenericTestPageObject po) {
    final IronListElement def = po.ironList().id(HUNDRED_THOUSAND);

    Assertions.assertEquals(100000 , def.getRowCount());
  }

  @VaadinTest(navigateAsString = NAV)
  public void firstLastVisibleRow(GenericTestPageObject po) throws Exception {
    final IronListElement def = po.ironList().id(HUNDRED_THOUSAND);

    Assertions.assertEquals(0 , def.getFirstVisibleRowIndex());
    Assertions.assertEquals(15 , def.getLastVisibleRowIndex());
    Assertions.assertTrue(def.isRowInView(0));
    Assertions.assertTrue(def.isRowInView(5));
    Assertions.assertTrue(def.isRowInView(15));
    Assertions.assertFalse(def.isRowInView(105));

    def.scrollToRow(105);
    Assertions.assertEquals(105 , def.getFirstVisibleRowIndex() , 2);
    Assertions.assertEquals(105 + 15 , def.getLastVisibleRowIndex() , 2);
    Assertions.assertTrue(def.isRowInView(105));
    Assertions.assertFalse(def.isRowInView(0));
    Assertions.assertFalse(def.isRowInView(1000));
  }

}
