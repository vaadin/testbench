package xxx.com.github.webdriverextensions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

public abstract class WebRepository {

  public void initElements(WebDriver driver) {
    PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
  }

  public void initElements(FieldDecorator decorator) {
    PageFactory.initElements(decorator, this);
  }
}
