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

import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.VerticalLayout;

public class MyComponent extends Composite {
  private final VerticalLayout layout = new VerticalLayout();
  private final Button button = new Button("Click me!");
  private int counter = 0;

  public MyComponent() {
    setCompositionRoot(layout);
    addStyleName("my-component");
    button.addClickListener(event -> {
      counter++;
      layout.addComponent(new MySubComponent(counter));
    });
    button.addStyleName("my-button");
    layout.addComponent(button);

  }
}
