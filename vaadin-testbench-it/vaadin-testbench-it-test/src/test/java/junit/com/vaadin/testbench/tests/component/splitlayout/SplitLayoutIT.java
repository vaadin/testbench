package junit.com.vaadin.testbench.tests.component.splitlayout;

import com.vaadin.flow.component.splitlayout.testbench.SplitLayoutElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.splitlayout.testbench.test.SplitLayoutView.DEFAULT;
import static com.vaadin.flow.component.splitlayout.testbench.test.SplitLayoutView.NAV;

@VaadinTest
public class SplitLayoutIT extends AbstractIT {

    @VaadinTest(navigateAsString = NAV)
    public void findInside(GenericTestPageObject po) throws Exception {
        final SplitLayoutElement splitLayout = po.$(SplitLayoutElement.class).id(DEFAULT);

        Assertions.assertEquals(3, po.$(TextFieldElement.class).all().size());
        Assertions.assertEquals(2,
                splitLayout.$(TextFieldElement.class).all().size());
    }

    @VaadinTest(navigateAsString = NAV)
    public void findSplitter(GenericTestPageObject po) throws Exception {
        TestBenchElement splitter = po.$(SplitLayoutElement.class).id(DEFAULT).getSplitter();
        Assertions.assertNotNull(splitter);
    }

}
