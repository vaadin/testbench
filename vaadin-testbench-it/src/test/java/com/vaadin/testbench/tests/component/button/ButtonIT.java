package com.vaadin.testbench.tests.component.button;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.button.testbench.test.ButtonView.NAV;
import static com.vaadin.flow.component.button.testbench.test.ButtonView.NOTEXT;
import static com.vaadin.flow.component.button.testbench.test.ButtonView.TEXT;

@VaadinTest(navigateTo = NAV)
class ButtonIT extends AbstractIT {

    @VaadinTest
    void click(VaadinPageObject po) {
        final ButtonElement buttonWithText = po.$(ButtonElement.class).id(TEXT);
        final ButtonElement buttonWithNoText = po.$(ButtonElement.class).id(NOTEXT);

        buttonWithNoText.click();
        Assertions.assertEquals("1. Button without text clicked", getLogRow(po, 0));
        buttonWithText.click();
        Assertions.assertEquals("2. Button with text clicked", getLogRow(po, 0));
    }

    @VaadinTest
    void getText(VaadinPageObject po) {
        final ButtonElement buttonWithText = po.$(ButtonElement.class).id(TEXT);
        final ButtonElement buttonWithNoText = po.$(ButtonElement.class).id(NOTEXT);
        Assertions.assertEquals("", buttonWithNoText.getText());
        Assertions.assertEquals("Text", buttonWithText.getText());
    }

}
