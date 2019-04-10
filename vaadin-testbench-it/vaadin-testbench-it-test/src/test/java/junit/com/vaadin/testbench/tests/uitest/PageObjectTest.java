package junit.com.vaadin.testbench.tests.uitest;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.annotations.Attribute;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.testbench.tests.uitest.PageObjectView.ROUTE;

@VaadinTest
public class PageObjectTest {

    @VaadinTest(navigateAsString = ROUTE)
    public void findUsingValueAnnotation(GenericTestPageObject po) {
        List<MyComponentWithIdElement> components = po.$(
                MyComponentWithIdElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithId", components.get(0).getText());
    }

    @VaadinTest(navigateAsString = ROUTE)
    public void findUsingContainsAnnotation(GenericTestPageObject po) {
        List<MyComponentWithClassesElement> components = po.$(
                MyComponentWithClassesElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithClasses",
                components.get(0).getText());
    }

    @Attribute(name = "class", contains = Attribute.SIMPLE_CLASS_NAME)
    @Attribute(name = "class", contains = "my-component-first")
    public static class MyComponentWithClassesElement extends TestBenchElement {
    }

    @Attribute(name = "id", value = Attribute.SIMPLE_CLASS_NAME)
    public static class MyComponentWithIdElement extends TestBenchElement {
    }
}
