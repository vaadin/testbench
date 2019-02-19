/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.vaadin.addons.testbench.junit5.pageobject;

import org.openqa.selenium.WebDriver;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.HorizontalLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;

/**
 *
 */
public abstract class AbstractVaadinPageObject
    extends AbstractPageObject
    implements VaadinPageObject {

  private TestBenchTestCase testCase = new TestBenchTestCase() { };

  public AbstractVaadinPageObject(WebDriver webdriver, ContainerInfo containerInfo) {
    super(webdriver, containerInfo);
    //vaadin specific init
    testCase.setDriver(webdriver);
    setDriver(testCase.getDriver());
  }

  public WithID<TextFieldElement> textField() {
    return id -> testCase.$(TextFieldElement.class).id(id);
  }

  public WithID<PasswordFieldElement> passwordField() {
    return id -> testCase.$(PasswordFieldElement.class).id(id);
  }

  public WithID<ButtonElement> btn() {
    return id -> testCase.$(ButtonElement.class).id(id);
  }

  public WithID<LabelElement> label() {
    return id -> testCase.$(LabelElement.class).id(id);
  }

  public WithID<GridElement> grid() {
    return id -> testCase.$(GridElement.class).id(id);
  }

  public WithID<ComboBoxElement> comboBox() {
    return id -> testCase.$(ComboBoxElement.class).id(id);
  }

  public WithID<DateFieldElement> dateField() {
    return id -> testCase.$(DateFieldElement.class).id(id);
  }

  public WithID<FormLayoutElement> formLayout() {
    return id -> testCase.$(FormLayoutElement.class).id(id);
  }

  public WithID<CssLayoutElement> cssLayout() {
    return id -> testCase.$(CssLayoutElement.class).id(id);
  }

  public WithID<HorizontalLayoutElement> horizontalLayout() {
    return id -> testCase.$(HorizontalLayoutElement.class).id(id);
  }

  public WithID<VerticalLayoutElement> verticalLayout() {
    return id -> testCase.$(VerticalLayoutElement.class).id(id);
  }


}
