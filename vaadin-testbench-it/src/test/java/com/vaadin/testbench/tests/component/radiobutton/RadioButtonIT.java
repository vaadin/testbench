package com.vaadin.testbench.tests.component.radiobutton;

import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.radiobutton.testbench.test.RadioButtonView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.radiobutton.testbench.test.RadioButtonView.NAV;

@VaadinTest(navigateTo = NAV)
public class RadioButtonIT extends AbstractIT {

    @VaadinTest
    public void getOptions(GenericTestPageObject po) {
        final RadioButtonGroupElement def = po.$(RadioButtonGroupElement.class).id(RadioButtonView.DEFAULT);
        final RadioButtonGroupElement preselected = po.$(RadioButtonGroupElement.class)
                .id(RadioButtonView.PRESELECTED);

        Assertions.assertArrayEquals(new String[]{"Item 0", "Item 1", "Item 2",
                "Item 3", "Item 4"}, def.getOptions().toArray());
        Assertions.assertArrayEquals(new String[]{"Item 0", "Item 1", "Item 2",
                "Item 3", "Item 4"}, preselected.getOptions().toArray());
    }

    @VaadinTest
    public void getSetByText(GenericTestPageObject po) {
        final RadioButtonGroupElement def = po.$(RadioButtonGroupElement.class).id(RadioButtonView.DEFAULT);
        final RadioButtonGroupElement preselected = po.$(RadioButtonGroupElement.class)
                .id(RadioButtonView.PRESELECTED);

        Assertions.assertNull(def.getSelectedText());
        Assertions.assertEquals("Item 3", preselected.getSelectedText());

        def.selectByText("Item 2");
        Assertions.assertEquals("Item 2", def.getSelectedText());
        preselected.selectByText("Item 2");
        Assertions.assertEquals("Item 2", preselected.getSelectedText());
    }

}
