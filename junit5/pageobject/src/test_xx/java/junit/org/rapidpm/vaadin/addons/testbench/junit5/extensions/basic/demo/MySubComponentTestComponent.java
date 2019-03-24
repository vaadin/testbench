package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import xxx.com.github.webdriverextensions.WebComponent;

public class MySubComponentTestComponent extends WebComponent {
  @FindBy(className = "caption")
  private WebElement caption;

  @FindBy(className = "counter")
  private WebElement counter;

  public WebElement getCaption() {
    return caption;
  }

  public WebElement getCounter() {
    return counter;
  }


}
