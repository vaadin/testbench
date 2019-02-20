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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import xxx.com.github.webdriverextensions.WebComponent;
import xxx.com.github.webdriverextensions.annotations.Delegate;
import xxx.com.github.webdriverextensions.annotations.ResetSearchContext;

public class WebDriverExtensionAnnotations extends Annotations {

  private Field field;

  public WebDriverExtensionAnnotations(Field field) {
    super(field);
    this.field = field;
  }

  boolean isSearchContextReset() {
    return field.getAnnotation(ResetSearchContext.class) != null;
  }

  public static WebElement getDelagate(WebComponent webComponent) {
    Field[] fields =
        ReflectionUtils.getAnnotatedDeclaredFields(webComponent.getClass(), Delegate.class);
    if (fields.length == 0) {
      return null;
    }
    if (fields.length > 1) {
      throw new RuntimeException(
          "More than one @Delagate annotations used. There should only exist one.");
    }
    WebElement delegate;
    try {
      fields[0].setAccessible(true); // Make sure field is accessible if it
                                     // is not declared as public
      delegate = (WebElement) fields[0].get(webComponent);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return delegate;
  }
}
