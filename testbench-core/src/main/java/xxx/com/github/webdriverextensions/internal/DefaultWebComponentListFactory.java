package xxx.com.github.webdriverextensions.internal;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import xxx.com.github.webdriverextensions.WebComponent;

public class DefaultWebComponentListFactory implements WebComponentListFactory {

  private WebComponentFactory webComponentFactory;

  public DefaultWebComponentListFactory(WebComponentFactory webComponentFactory) {
    this.webComponentFactory = webComponentFactory;
  }

  @Override
  public <T extends WebComponent> List<T> create(Class<T> webComponentClass,
      List<WebElement> webElements, WebDriver driver, ParameterizedType genericTypeArguments) {
    return new WebComponentList<>(webComponentClass, webElements, webComponentFactory, driver,
        genericTypeArguments);
  }
}
