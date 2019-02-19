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

import java.lang.reflect.Field;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class WebDriverExtensionElementLocatorFactory implements ElementLocatorFactory {

  private final WebDriver driver;
  private final SearchContext searchContext;

  /**
   * Creates a new element locator.
   *
   * @param searchContext The context to use when finding the element
   * @param driver The field on the Page Object that will hold the located value
   */
  public WebDriverExtensionElementLocatorFactory(SearchContext searchContext, WebDriver driver) {
    this.searchContext = searchContext;
    this.driver = driver;
  }

  @Override
  public ElementLocator createLocator(Field field) {
    return new WebDriverExtensionElementLocator(searchContext, field, driver);
  }
}
