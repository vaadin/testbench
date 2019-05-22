package com.vaadin.testbench.tests.ui;

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.AbstractVaadinPageObject;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class GenericTestPageObject
        extends AbstractVaadinPageObject {

    public GenericTestPageObject(WebDriver webdriver,
                                 ContainerInfo containerInfo,
                                 Optional<String> defaultNavigationTarget) {
        super(webdriver, containerInfo, defaultNavigationTarget);
    }
}
