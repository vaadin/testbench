package junit.com.vaadin.testbench.tests.component.common;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import com.vaadin.flow.component.common.testbench.HasLabel;
import com.vaadin.testbench.HasStringValueProperty;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;

public abstract class AbstractIT {


  protected String getLogRow(AbstractVaadinPageObject po , int i) {
    return po.findElement(By.id("log")).findElements(By.tagName("div"))
             .get(i)
             .getText();
  }


  protected String getLogRowWithoutNumber(AbstractVaadinPageObject po , int i) {
    return getLogRow(po , i).replaceFirst(".*\\. " , "");
  }


//  @Before
//  public void open() {
//    getDriver().get("http://localhost:8080/" + getTestPath());
//  }

//  private String getTestPath() {
//    return getClass().getSimpleName().replaceAll("IT$" , "");
//  }

  protected <T extends HasStringValueProperty & HasLabel> void assertStringValue(
      AbstractVaadinPageObject po ,
      T element ,
      String expectedValue) {
    Assertions.assertEquals(expectedValue , element.getValue());
    Assertions.assertEquals(
        "Value of '" + element.getLabel() + "' is now " + expectedValue ,
        getLogRowWithoutNumber(po , 0));
  }

}
