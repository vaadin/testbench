package com.vaadin.testbench.addons.junit5.pageobject;

import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
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
    //testbench specific init
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
