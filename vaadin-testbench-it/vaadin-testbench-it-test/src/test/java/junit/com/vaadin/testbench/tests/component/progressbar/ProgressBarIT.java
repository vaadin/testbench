package junit.com.vaadin.testbench.tests.component.progressbar;

import com.vaadin.flow.component.progressbar.testbench.ProgressBarElement;
import com.vaadin.flow.component.progressbar.testbench.test.ProgressBarView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.ui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.flow.component.progressbar.testbench.test.ProgressBarView.NAV;

@VaadinTest
public class ProgressBarIT extends AbstractIT {

    @VaadinTest(navigateTo = NAV)
    public void getValue(GenericTestPageObject po) throws Exception {
        final ProgressBarElement def = po.$(ProgressBarElement.class).id(ProgressBarView.DEFAULT);
        final ProgressBarElement hundred = po.$(ProgressBarElement.class).id(ProgressBarView.HUNDRED);

        Assertions.assertEquals(7, def.getValue(), 0.001);
        Assertions.assertEquals(22, hundred.getValue(), 0.001);
    }

}
