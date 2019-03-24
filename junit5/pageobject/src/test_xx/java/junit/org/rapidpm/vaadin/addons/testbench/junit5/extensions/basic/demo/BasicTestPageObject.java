package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.vaadin.testbench.addons.testbench.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.testbench.junit5.pageobject.AbstractPageObject;

public class BasicTestPageObject extends AbstractPageObject {

  public BasicTestPageObject(WebDriver webDriver, ContainerInfo containerInfo) {
    super(webDriver, containerInfo);
  }

  @FindBy(id = DemoUI.COMPONENT_ID)
  private MyComponentTestComponent component;

  public MyComponentTestComponent getComponent() {
    return component;
  }
}
