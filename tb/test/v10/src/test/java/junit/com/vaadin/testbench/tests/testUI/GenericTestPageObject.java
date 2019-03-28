package junit.com.vaadin.testbench.tests.testUI;

import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;

public class GenericTestPageObject
    extends AbstractVaadinPageObject {

  public GenericTestPageObject(WebDriver webdriver ,
                               ContainerInfo containerInfo) {
    super(webdriver , containerInfo);
  }
}
