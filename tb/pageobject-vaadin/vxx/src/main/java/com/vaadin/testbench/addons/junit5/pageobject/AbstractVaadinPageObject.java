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
package com.vaadin.testbench.addons.junit5.pageobject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.ironlist.testbench.IronListElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.progressbar.testbench.ProgressBarElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.splitlayout.testbench.SplitLayoutElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.LabelElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.flow.component.orderedlayout.testbench.HorizontalLayoutElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 *
 */
public abstract class AbstractVaadinPageObject
    extends AbstractPageObject
    implements VaadinPageObject {

  private TestBenchTestCase testCase = new TestBenchTestCase() { };

  public AbstractVaadinPageObject(WebDriver webdriver, ContainerInfo containerInfo) {
    super(webdriver, containerInfo);
    //testbench specific init
    testCase.setDriver(webdriver);
    setDriver(testCase.getDriver());
  }

  //compat Method
  public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz) {return testCase.$(clazz);}

  public ElementQuery<TestBenchElement> $(String tagName) {return testCase.$(tagName);}

  public TestBenchCommandExecutor getCommandExecutor() {return testCase.getCommandExecutor();}

  public WebElement findElement(By by) {return testCase.findElement(by);}

  public List<WebElement> findElements(By by) {return testCase.findElements(by);}



  public WithID<TextFieldElement> textField() {
    return id -> testCase.$(TextFieldElement.class).id(id);
  }

  public WithID<TextAreaElement> textArea() {
    return id -> testCase.$(TextAreaElement.class).id(id);
  }

  public WithID<PasswordFieldElement> passwordField() {
    return id -> testCase.$(PasswordFieldElement.class).id(id);
  }

  public WithID<ButtonElement> btn() {
    return id -> testCase.$(ButtonElement.class).id(id);
  }

  public WithID<SpanElement> span() {
    return id -> testCase.$(SpanElement.class).id(id);
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

  public WithID<CheckboxElement> checkBox() {
    return id -> testCase.$(CheckboxElement.class).id(id);
  }

  public WithID<DatePickerElement> datePicker() {
    return id -> testCase.$(DatePickerElement.class).id(id);
  }

  public WithID<DialogElement> dialog() {
    return id -> testCase.$(DialogElement.class).id(id);
  }

  public WithID<IronListElement> ironList() {
    return id -> testCase.$(IronListElement.class).id(id);
  }

  public WithID<NotificationElement> notification() {
    return id -> testCase.$(NotificationElement.class).id(id);
  }

  public WithID<ProgressBarElement> progressBar() {
    return id -> testCase.$(ProgressBarElement.class).id(id);
  }

  public WithID<RadioButtonGroupElement> radioBtnGrp() {
    return id -> testCase.$(RadioButtonGroupElement.class).id(id);
  }

  public WithID<TabsElement> tabs() {
    return id -> testCase.$(TabsElement.class).id(id);
  }

  public WithID<UploadElement> upload() {
    return id -> testCase.$(UploadElement.class).id(id);
  }



  //TODO not available until now
//  public WithID<DateFieldElement> dateField() {
//    return id -> testCase.$(DateFieldElement.class).id(id);
//  }


  //  New inheritence in V10
  public WithID<FormLayoutElement> formLayout() {
    return id -> testCase.$(FormLayoutElement.class).id(id);
  }

  public WithID<HorizontalLayoutElement> horizontalLayout() {
    return id -> testCase.$(HorizontalLayoutElement.class).id(id);
  }

  public WithID<VerticalLayoutElement> verticalLayout() {
    return id -> testCase.$(VerticalLayoutElement.class).id(id);
  }
  public WithID<SplitLayoutElement> splitLayout() {
    return id -> testCase.$(SplitLayoutElement.class).id(id);
  }


}
