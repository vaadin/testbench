package junit.com.vaadin.testbench.tests;

import org.openqa.selenium.WebDriver;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ContainerInfo;
import org.rapidpm.vaadin.addons.testbench.junit5.pageobject.AbstractVaadinPageObject;

public class GenericTestPageObject
    extends AbstractVaadinPageObject
    implements HasLogger {

  public GenericTestPageObject(WebDriver webdriver ,
                               ContainerInfo containerInfo) {
    super(webdriver , containerInfo);
  }
}
