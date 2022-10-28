package com.vaadin.tests;

import java.util.List;

import io.github.bonigarcia.seljup.Browser;
import io.github.bonigarcia.seljup.EnabledIfBrowserAvailable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;

@EnabledIfBrowserAvailable(Browser.CHROME)
public class SeleniumLocalPageObjectIT extends AbstractSeleniumChromeTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
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
