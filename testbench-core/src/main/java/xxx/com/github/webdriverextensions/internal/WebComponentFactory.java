package xxx.com.github.webdriverextensions.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import xxx.com.github.webdriverextensions.WebComponent;

public interface WebComponentFactory {

  <T extends WebComponent> T create(Class<T> webComponentClass , WebElement webElement ,
                                    WebDriver webDriver);
}
