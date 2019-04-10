package junit.com.vaadin.testbench.tests.component.orderedlayout;

import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.uitest.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.orderedlayout.testbench.test.VerticalLayoutView.DEFAULT;
import static com.vaadin.flow.component.orderedlayout.testbench.test.VerticalLayoutView.NAV;

@VaadinTest
public class VerticalLayoutIT extends AbstractIT {

    @VaadinTest(navigateAsString = NAV)
    public void findInside(GenericTestPageObject po) throws Exception {
        final VerticalLayoutElement verticalLayout = po.$(VerticalLayoutElement.class)
                .id(DEFAULT);
        Assertions.assertEquals(3, po.$(TextFieldElement.class).all().size());
        Assertions.assertEquals(2,
                verticalLayout.$(TextFieldElement.class).all().size());
    }

}
