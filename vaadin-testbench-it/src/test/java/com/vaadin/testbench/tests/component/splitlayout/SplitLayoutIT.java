package com.vaadin.testbench.tests.component.splitlayout;

import com.vaadin.flow.component.splitlayout.testbench.SplitLayoutElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.splitlayout.testbench.test.SplitLayoutView.DEFAULT;
import static com.vaadin.flow.component.splitlayout.testbench.test.SplitLayoutView.NAV;

@VaadinTest(navigateTo = NAV)
public class SplitLayoutIT extends AbstractIT {

    @VaadinTest
    public void findInside(VaadinPageObject po) {
        final SplitLayoutElement splitLayout = po.$(SplitLayoutElement.class).id(DEFAULT);

        Assertions.assertEquals(3, po.$(TextFieldElement.class).all().size());
        Assertions.assertEquals(2,
                splitLayout.$(TextFieldElement.class).all().size());
    }

    @VaadinTest
    public void findSplitter(VaadinPageObject po) {
        TestBenchElement splitter = po.$(SplitLayoutElement.class).id(DEFAULT).getSplitter();
        Assertions.assertNotNull(splitter);
    }

}
