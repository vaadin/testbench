package junit.com.vaadin.testbench.tests.demo;

import static com.vaadin.testbench.tests.demo.VaadinApp.BTN_CLICK_ME;
import static com.vaadin.testbench.tests.demo.VaadinApp.LB_CLICK_COUNT;
import static java.lang.Integer.valueOf;

import org.openqa.selenium.WebDriver;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;

public class VaadinAppPageObject extends AbstractVaadinPageObject implements HasLogger {


  public VaadinAppPageObject(WebDriver webdriver, ContainerInfo containerInfo) {
    super(webdriver, containerInfo);
    logger().info("VaadinAppPageObject was created..");
  }

  public ButtonElement btnClickMe() {
    return btn().id(BTN_CLICK_ME);
  }

  public SpanElement lbClickCount() {
    return span().id(LB_CLICK_COUNT);
  }

  public void click() {
    btnClickMe().click();
  }

  public String clickCountAsString() {
    return lbClickCount().getText();
  }

  // no exception handling
  public int clickCount() {
    return valueOf(clickCountAsString());
  }

}
