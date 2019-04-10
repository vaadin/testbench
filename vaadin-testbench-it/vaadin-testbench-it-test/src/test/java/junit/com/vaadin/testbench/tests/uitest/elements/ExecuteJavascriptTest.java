package junit.com.vaadin.testbench.tests.uitest.elements;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.uitest.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.testbench.tests.testUI.ElementQueryView.ROUTE;

@VaadinTest
public class ExecuteJavascriptTest {

    @VaadinTest(navigateAsString = ROUTE)
    public void getProperty(GenericTestPageObject po) throws Exception {
        TestBenchElement button = po.$(NativeButtonElement.class).first();
        Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
        Assertions.assertEquals(Long.valueOf(0), offsetTop);
    }
}
