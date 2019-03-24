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
