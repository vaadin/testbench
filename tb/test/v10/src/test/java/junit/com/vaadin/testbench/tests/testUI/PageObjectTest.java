package junit.com.vaadin.testbench.tests.testUI;

import static com.vaadin.testbench.tests.testUI.PageObjectView.ROUTE;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;

@VaadinTest
public class PageObjectTest {

  @VaadinTest(navigateAsString = ROUTE)
  public void findUsingValueAnnotation(GenericTestPageObject po) {
    List<MyComponentWithIdElement> components = po.$(
        MyComponentWithIdElement.class).all();

    Assertions.assertEquals(1 , components.size());
    Assertions.assertEquals("MyComponentWithId" , components.get(0).getText());
  }

  @VaadinTest(navigateAsString = ROUTE)
  public void findUsingContainsAnnotation(GenericTestPageObject po) {
    List<MyComponentWithClassesElement> components = po.$(
        MyComponentWithClassesElement.class).all();

    Assertions.assertEquals(1 , components.size());
    Assertions.assertEquals("MyComponentWithClasses" ,
                            components.get(0).getText());
  }

}
