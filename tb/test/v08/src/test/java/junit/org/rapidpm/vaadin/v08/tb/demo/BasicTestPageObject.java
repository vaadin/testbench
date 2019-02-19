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
package junit.org.rapidpm.vaadin.v08.tb.demo;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import org.openqa.selenium.WebDriver;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ContainerInfo;
import org.rapidpm.vaadin.addons.testbench.junit5.pageobject.AbstractVaadinPageObject;
import org.rapidpm.vaadin.v08.tb.demo.BasicTestUI;


/**
 *
 */
public class BasicTestPageObject extends AbstractVaadinPageObject {

  public BasicTestPageObject(WebDriver webDriver, ContainerInfo containerInfo) {
    super(webDriver, containerInfo);
  }

  public ButtonElement button() {
    return btn().id(BasicTestUI.BUTTON_ID);
  }

  public LabelElement counterLabel() {
    return label().id(BasicTestUI.LABEL_ID);
  }


}
