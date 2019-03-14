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
package junit.com.vaadin.testbench.tests.component.radiobutton;

import static com.vaadin.flow.component.radiobutton.testbench.test.RadioButtonView.NAV;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.radiobutton.testbench.test.RadioButtonView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class RadioButtonIT extends AbstractIT {

  @VaadinTest(navigateAsString = NAV)
  public void getOptions(GenericTestPageObject po) throws Exception {
    final RadioButtonGroupElement def = po.radioBtnGrp().id(RadioButtonView.DEFAULT);
    final RadioButtonGroupElement preselected = po.radioBtnGrp()
                                                  .id(RadioButtonView.PRESELECTED);

    Assertions.assertArrayEquals(new String[]{"Item 0" , "Item 1" , "Item 2" ,
        "Item 3" , "Item 4"} , def.getOptions().toArray());
    Assertions.assertArrayEquals(new String[]{"Item 0" , "Item 1" , "Item 2" ,
        "Item 3" , "Item 4"} , preselected.getOptions().toArray());
  }

  @VaadinTest(navigateAsString = NAV)
  public void getSetByText(GenericTestPageObject po) throws Exception {
    final RadioButtonGroupElement def = po.radioBtnGrp().id(RadioButtonView.DEFAULT);
    final RadioButtonGroupElement preselected = po.radioBtnGrp()
                                                  .id(RadioButtonView.PRESELECTED);

    Assertions.assertNull(def.getSelectedText());
    Assertions.assertEquals("Item 3" , preselected.getSelectedText());

    def.selectByText("Item 2");
    Assertions.assertEquals("Item 2" , def.getSelectedText());
    preselected.selectByText("Item 2");
    Assertions.assertEquals("Item 2" , preselected.getSelectedText());
  }

}
