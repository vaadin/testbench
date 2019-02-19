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
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import xxx.com.github.webdriverextensions.annotations.ResetSearchContext;

public class WebDriverExtensionElementLocator implements ElementLocator {

  private final SearchContext searchContext;
  private final boolean shouldCache;
  private final By by;
  private WebElement cachedElement;
  private List<WebElement> cachedElementList;

  public WebDriverExtensionElementLocator(SearchContext searchContext, Field field,
      WebDriver driver) {
    WebDriverExtensionAnnotations annotations = new WebDriverExtensionAnnotations(field);
    if (annotations.isSearchContextReset()) {
      this.searchContext = driver;
    } else {
      this.searchContext = searchContext;
    }
    shouldCache = annotations.isLookupCached();
    by = annotations.buildBy();
  }

  @Override
  public WebElement findElement() {
    if (cachedElement != null && shouldCache) {
      return cachedElement;
    }

    WebElement element = searchContext.findElement(by);
    if (shouldCache) {
      cachedElement = element;
    }

    return element;
  }

  @Override
  public List<WebElement> findElements() {
    if (cachedElementList != null && shouldCache) {
      return cachedElementList;
    }

    List<WebElement> elements = searchContext.findElements(by);
    if (shouldCache) {
      cachedElementList = elements;
    }

    return elements;
  }

  private boolean hasAnnotatedResetSearchContext(Field field) {
    ResetSearchContext annotation = field.getAnnotation(ResetSearchContext.class);
    if (annotation != null) {
      return true;
    }
    return false;
  }
}
