package junit.com.vaadin.testbench.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import com.vaadin.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testUI.PageObjectView;

public class PageObjectIT {


  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(PageObjectView.ROUTE);
  }


  @VaadinWebUnitTest
  public void findUsingValueAnnotation(GenericTestPageObject po) {
    openTestURL(po);
    List<MyComponentWithIdElement> components = po.$(
        MyComponentWithIdElement.class).all();

    Assertions.assertEquals(1 , components.size());
    Assertions.assertEquals("MyComponentWithId" , components.get(0).getText());
  }

  @VaadinWebUnitTest
  public void findUsingContainsAnnotation(GenericTestPageObject po) {
    openTestURL(po);
    List<MyComponentWithClassesElement> components = po.$(
        MyComponentWithClassesElement.class).all();

    Assertions.assertEquals(1 , components.size());
    Assertions.assertEquals("MyComponentWithClasses" ,
                            components.get(0).getText());
  }

}
