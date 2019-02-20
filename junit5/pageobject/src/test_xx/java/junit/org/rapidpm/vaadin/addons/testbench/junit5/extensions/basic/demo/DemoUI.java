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
package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo;

import org.vaadin.addonhelpers.AbstractTest;
import com.vaadin.ui.Component;


public class DemoUI extends AbstractTest {

  public static final String COMPONENT_ID = "componentID";

  @Override
  public Component getTestComponent() {
    final MyComponent component = new MyComponent();
    component.setId(COMPONENT_ID);
    return component;
  }
}
