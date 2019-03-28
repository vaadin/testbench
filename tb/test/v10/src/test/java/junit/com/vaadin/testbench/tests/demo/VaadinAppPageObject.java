package junit.com.vaadin.testbench.tests.demo;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;

import static java.lang.Integer.valueOf;
import static com.vaadin.testbench.tests.demo.VaadinApp.BTN_CLICK_ME;
import static com.vaadin.testbench.tests.demo.VaadinApp.LB_CLICK_COUNT;

public class VaadinAppPageObject extends AbstractVaadinPageObject {


  public VaadinAppPageObject(WebDriver webdriver, ContainerInfo containerInfo) {
    super(webdriver, containerInfo);
  }

  public ButtonElement btnClickMe() {
    return $(ButtonElement.class).id(BTN_CLICK_ME);
  }

  public SpanElement lbClickCount() {
    return $(SpanElement.class).id(LB_CLICK_COUNT);
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
