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
