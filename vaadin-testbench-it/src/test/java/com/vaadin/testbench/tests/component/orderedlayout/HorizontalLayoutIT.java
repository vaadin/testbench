package com.vaadin.testbench.tests.component.orderedlayout;

import com.vaadin.flow.component.orderedlayout.testbench.HorizontalLayoutElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.orderedlayout.testbench.test.HorizontalLayoutView.DEFAULT;
import static com.vaadin.flow.component.orderedlayout.testbench.test.HorizontalLayoutView.NAV;

public class HorizontalLayoutIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void findInside(VaadinPageObject po) {
        final HorizontalLayoutElement horizontalLayout = po.$(HorizontalLayoutElement.class)
                .id(DEFAULT);

        Assertions.assertEquals(3, po.$(TextFieldElement.class).all().size());
        Assertions.assertEquals(2,
                horizontalLayout.$(TextFieldElement.class).all().size());
    }

}
