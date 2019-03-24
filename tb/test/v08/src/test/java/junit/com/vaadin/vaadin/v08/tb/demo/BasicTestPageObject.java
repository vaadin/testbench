package junit.com.vaadin.vaadin.v08.tb.demo;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;
import com.vaadin.testbench.v08.tb.demo.BasicTestUI;


/**
 *
 */
public class BasicTestPageObject extends AbstractVaadinPageObject {

  public BasicTestPageObject(WebDriver webDriver, ContainerInfo containerInfo) {
    super(webDriver, containerInfo);
  }

  public ButtonElement button() {
    return btn().id(BasicTestUI.BUTTON_ID);
  }

  public LabelElement counterLabel() {
    return label().id(BasicTestUI.LABEL_ID);
  }


}
