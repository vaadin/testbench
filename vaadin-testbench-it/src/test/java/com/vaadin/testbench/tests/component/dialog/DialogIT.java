package com.vaadin.testbench.tests.component.dialog;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.dialog.testbench.test.DialogView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.dialog.testbench.test.DialogView.NAV;

public class DialogIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void openClose(VaadinPageObject po) {
        final DialogElement dialog = po.$(DialogElement.class).id(DialogView.THE_DIALOG);
        Assertions.assertTrue(dialog.isOpen());

        dialog.$(ButtonElement.class).first().click();
        Assertions.assertFalse(dialog.isOpen());
    }

}
