/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
