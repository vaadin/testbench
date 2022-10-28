package com.vaadin.tests;

import java.util.List;

import io.github.bonigarcia.seljup.DriverCapabilities;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;

@ExtendWith(SeleniumJupiter.class)
public class SeleniumPageObjectIT extends SeleniumAbstractTB9Test {

    @DriverCapabilities
    ChromeOptions options = new ChromeOptions();

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @BeforeEach
    public void setDriver(ChromeDriver driver) {
        super.setDriver(driver);
    }

    @Test
    public void findUsingValueAnnotation() {
        openTestURL();
        List<MyComponentWithIdElement> components = $(
                MyComponentWithIdElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithId",
                components.get(0).getText());
    }

    @Test
    public void findUsingContainsAnnotation() {
        openTestURL();
        List<MyComponentWithClassesElement> components = $(
                MyComponentWithClassesElement.class).all();

        Assertions.assertEquals(1, components.size());
        Assertions.assertEquals("MyComponentWithClasses",
                components.get(0).getText());
    }

}
