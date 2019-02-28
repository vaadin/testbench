package junit.com.vaadin.testbench.tests;

import org.openqa.selenium.WebDriver;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.vaadin.addons.testbench.junit5.extensions.container.ContainerInfo;
import com.vaadin.vaadin.addons.testbench.junit5.pageobject.AbstractVaadinPageObject;

public class GenericTestPageObject
    extends AbstractVaadinPageObject
    implements HasLogger {

  public GenericTestPageObject(WebDriver webdriver ,
                               ContainerInfo containerInfo) {
    super(webdriver , containerInfo);
  }
}
