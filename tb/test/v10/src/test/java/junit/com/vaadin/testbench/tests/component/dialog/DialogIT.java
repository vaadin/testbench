package junit.com.vaadin.testbench.tests.component.dialog;


import static com.vaadin.flow.component.dialog.testbench.test.DialogView.NAV;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.dialog.testbench.test.DialogView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class DialogIT extends AbstractIT {

  @VaadinTest
  public void openClose(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final DialogElement dialog = po.dialog().id(DialogView.THE_DIALOG);
    Assertions.assertTrue(dialog.isOpen());

    dialog.$(ButtonElement.class).first().click();
    Assertions.assertFalse(dialog.isOpen());
  }

}
