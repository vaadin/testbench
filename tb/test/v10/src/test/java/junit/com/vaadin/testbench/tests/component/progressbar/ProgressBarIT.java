package junit.com.vaadin.testbench.tests.component.progressbar;

import static com.vaadin.flow.component.progressbar.testbench.test.ProgressBarView.NAV;

import org.junit.jupiter.api.Assertions;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.flow.component.progressbar.testbench.ProgressBarElement;
import com.vaadin.flow.component.progressbar.testbench.test.ProgressBarView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class ProgressBarIT extends AbstractIT {

  @VaadinTest(navigateAsString = NAV)
  public void getValue(GenericTestPageObject po) throws Exception {
    final ProgressBarElement def = po.progressBar().id(ProgressBarView.DEFAULT);
    final ProgressBarElement hundred = po.progressBar().id(ProgressBarView.HUNDRED);

    Assertions.assertEquals(7 , def.getValue() , 0.001);
    Assertions.assertEquals(22 , hundred.getValue() , 0.001);
  }

}
