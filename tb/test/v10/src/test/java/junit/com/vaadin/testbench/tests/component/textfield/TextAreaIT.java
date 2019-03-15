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
package junit.com.vaadin.testbench.tests.component.textfield;

import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.INITIAL_VALUE;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.LABEL_EAGER;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.NAV;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.NOLABEL;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.PLACEHOLDER;

import org.junit.jupiter.api.Assertions;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class TextAreaIT extends AbstractIT {


  @VaadinTest(navigateAsString = NAV)
  public void getSetValue(GenericTestPageObject po) throws Exception {

    final TextAreaElement labelEager = po.textArea().id(LABEL_EAGER);
    final TextAreaElement nolabel = po.textArea().id(NOLABEL);
    final TextAreaElement initialValue = po.textArea().id(INITIAL_VALUE);
    final TextAreaElement placeholder = po.textArea().id(PLACEHOLDER);

    Assertions.assertEquals("" , labelEager.getText());
    Assertions.assertEquals("" , nolabel.getText());
    Assertions.assertEquals("Initial" , initialValue.getText());
    Assertions.assertEquals("" , placeholder.getText());

    labelEager.setValue("Foo");
    assertStringValue(po,labelEager , "Foo");

    nolabel.setValue("Foo");
    assertStringValue(po,nolabel , "Foo");

    initialValue.setValue("Foo");
    assertStringValue(po,initialValue , "Foo");

    placeholder.setValue("Foo");
    assertStringValue(po,placeholder , "Foo");
  }

  @VaadinTest(navigateAsString = NAV)
  public void getLabelEager(GenericTestPageObject po) throws Exception {

    final TextAreaElement labelEager = po.textArea().id(LABEL_EAGER);
    final TextAreaElement nolabel = po.textArea().id(NOLABEL);
    final TextAreaElement initialValue = po.textArea().id(INITIAL_VALUE);
    final TextAreaElement placeholder = po.textArea().id(PLACEHOLDER);

    Assertions.assertEquals("Label (eager)" , labelEager.getLabel());
    Assertions.assertEquals("" , nolabel.getLabel());
    Assertions.assertEquals("Has an initial value" , initialValue.getLabel());
    Assertions.assertEquals("Has a placeholder" , placeholder.getLabel());
  }

  @VaadinTest(navigateAsString = NAV)
  public void getPlaceholder(GenericTestPageObject po) throws Exception {

    final TextAreaElement labelEager = po.textArea().id(LABEL_EAGER);
    final TextAreaElement nolabel = po.textArea().id(NOLABEL);
    final TextAreaElement initialValue = po.textArea().id(INITIAL_VALUE);
    final TextAreaElement placeholder = po.textArea().id(PLACEHOLDER);

    Assertions.assertEquals("" , labelEager.getPlaceholder());
    Assertions.assertEquals("" , nolabel.getPlaceholder());
    Assertions.assertEquals("" , initialValue.getPlaceholder());
    Assertions.assertEquals("Text goes here" , placeholder.getPlaceholder());
  }

}
