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
package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.webdriverextensions.vaadin.VaadinConditions;


public class WaitUtil {
  private final WebDriver webDriver;

  public WaitUtil(WebDriver driver) {
    super();
    this.webDriver = driver;
  }

  public void waitForVaadin() {
    new WebDriverWait(webDriver, 1).until(webDriver -> VaadinConditions.ajaxCallsCompleted().test(webDriver));
  }
}
