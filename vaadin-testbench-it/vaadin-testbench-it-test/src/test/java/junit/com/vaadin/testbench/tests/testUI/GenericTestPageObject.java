package junit.com.vaadin.testbench.tests.testUI;

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;
import org.openqa.selenium.WebDriver;

public class GenericTestPageObject
        extends AbstractVaadinPageObject {

    public GenericTestPageObject(WebDriver webdriver,
                                 ContainerInfo containerInfo) {
        super(webdriver, containerInfo);
    }
}
