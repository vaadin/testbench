package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import xxx.com.github.webdriverextensions.WebComponent;

public class MyComponentTestComponent extends WebComponent {
  @FindBy(className = "my-button")
  private WebElement button;
  @FindBy(className = "my-sub-component")
  private List<MySubComponentTestComponent> subComponents;


  public void clickButton() {
    button.click();
  }

  public List<MySubComponentTestComponent> getSubComponents() {
    return subComponents;
  }
}
