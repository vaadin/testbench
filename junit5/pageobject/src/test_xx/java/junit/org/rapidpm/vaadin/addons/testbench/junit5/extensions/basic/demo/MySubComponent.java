package junit.org.rapidpm.vaadin.addons.testbench.junit5.extensions.basic.demo;

import com.vaadin.ui.Composite;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class MySubComponent extends Composite {

  public MySubComponent(int counter) {

    Label staticLabel = new Label("A label");
    staticLabel.addStyleName("caption");
    Label counterLabel = new Label("count: " + counter);
    counterLabel.addStyleName("counter");
    setCompositionRoot(new CssLayout(staticLabel, counterLabel));
    addStyleName("my-sub-component");
  }

}
