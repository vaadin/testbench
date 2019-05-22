package com.vaadin.testbench.tests.component.formlayout;

import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.formlayout.testbench.test.FormLayoutView;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.formlayout.testbench.test.FormLayoutView.NAV;

public class FormLayoutIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void findInside(GenericTestPageObject po) {
        final FormLayoutElement formLayout = po.$(FormLayoutElement.class).id(FormLayoutView.DEFAULT);

        Assertions.assertEquals(3, po.$(TextFieldElement.class).all().size());
        Assertions.assertEquals(2,
                formLayout.$(TextFieldElement.class).all().size());
    }

}
