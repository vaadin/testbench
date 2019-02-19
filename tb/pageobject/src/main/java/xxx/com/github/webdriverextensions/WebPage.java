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
package xxx.com.github.webdriverextensions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import xxx.com.github.webdriverextensions.internal.Openable;

public abstract class WebPage implements Openable {

  public void initElements(WebDriver driver) {
    PageFactory.initElements(new WebDriverExtensionFieldDecorator(driver), this);
  }

  public void initElements(FieldDecorator decorator) {
    PageFactory.initElements(decorator, this);
  }

  @Override
  public abstract void open(Object... arguments);

  @Override
  public boolean isOpen(Object... arguments) {
    try {
      assertIsOpen(arguments);
      return true;
    } catch (AssertionError e) {
      return false;
    }
  }

  @Override
  public boolean isNotOpen(Object... arguments) {
    return !isOpen(arguments);
  }

  @Override
  public abstract void assertIsOpen(Object... arguments) throws AssertionError;

  @Override
  public void assertIsNotOpen(Object... arguments) throws AssertionError {
    if (isOpen(arguments)) {
      throw new AssertionError(this.getClass().getSimpleName() + " is open when it shouldn't");
    }
  }

}
