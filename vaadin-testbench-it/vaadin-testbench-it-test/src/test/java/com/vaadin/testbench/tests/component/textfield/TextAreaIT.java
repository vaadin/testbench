package com.vaadin.testbench.tests.component.textfield;

import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.INITIAL_VALUE;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.LABEL_EAGER;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.NAV;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.NOLABEL;
import static com.vaadin.flow.component.textfield.testbench.test.TextAreaView.PLACEHOLDER;

@VaadinTest
public class TextAreaIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void getSetValue(GenericTestPageObject po) throws Exception {

        final TextAreaElement labelEager = po.$(TextAreaElement.class).id(LABEL_EAGER);
        final TextAreaElement nolabel = po.$(TextAreaElement.class).id(NOLABEL);
        final TextAreaElement initialValue = po.$(TextAreaElement.class).id(INITIAL_VALUE);
        final TextAreaElement placeholder = po.$(TextAreaElement.class).id(PLACEHOLDER);

        Assertions.assertEquals("", labelEager.getValue());
        Assertions.assertEquals("", nolabel.getValue());
        Assertions.assertEquals("Initial", initialValue.getValue());
        Assertions.assertEquals("", placeholder.getValue());

        labelEager.setValue("Foo");
        assertStringValue(po, labelEager, "Foo");

        nolabel.setValue("Foo");
        assertStringValue(po, nolabel, "Foo");

        initialValue.setValue("Foo");
        assertStringValue(po, initialValue, "Foo");

        placeholder.setValue("Foo");
        assertStringValue(po, placeholder, "Foo");
    }

    @VaadinTest(navigateTo = NAV)
    public void getLabelEager(GenericTestPageObject po) throws Exception {

        final TextAreaElement labelEager = po.$(TextAreaElement.class).id(LABEL_EAGER);
        final TextAreaElement nolabel = po.$(TextAreaElement.class).id(NOLABEL);
        final TextAreaElement initialValue = po.$(TextAreaElement.class).id(INITIAL_VALUE);
        final TextAreaElement placeholder = po.$(TextAreaElement.class).id(PLACEHOLDER);

        Assertions.assertEquals("Label (eager)", labelEager.getLabel());
        Assertions.assertEquals("", nolabel.getLabel());
        Assertions.assertEquals("Has an initial value", initialValue.getLabel());
        Assertions.assertEquals("Has a placeholder", placeholder.getLabel());
    }

    @VaadinTest(navigateTo = NAV)
    public void getPlaceholder(GenericTestPageObject po) throws Exception {

        final TextAreaElement labelEager = po.$(TextAreaElement.class).id(LABEL_EAGER);
        final TextAreaElement nolabel = po.$(TextAreaElement.class).id(NOLABEL);
        final TextAreaElement initialValue = po.$(TextAreaElement.class).id(INITIAL_VALUE);
        final TextAreaElement placeholder = po.$(TextAreaElement.class).id(PLACEHOLDER);

        Assertions.assertEquals("", labelEager.getPlaceholder());
        Assertions.assertEquals("", nolabel.getPlaceholder());
        Assertions.assertEquals("", initialValue.getPlaceholder());
        Assertions.assertEquals("Text goes here", placeholder.getPlaceholder());
    }

}
