package junit.com.vaadin.testbench.tests.component.checkbox;

import static com.vaadin.flow.component.checkbox.testbench.test.CheckboxView.*;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest()
public class CheckboxIT extends AbstractIT {


  @VaadinTest(navigateAsString = NAV)
  public void checkUncheck(GenericTestPageObject po) throws Exception {

    final CheckboxElement checkboxWithText = po.$(CheckboxElement.class).id(TEXT);
    final CheckboxElement checkboxWithNoText = po.$(CheckboxElement.class).id(NOTEXT);
    final CheckboxElement checkboxInitiallyChecked = po.$(CheckboxElement.class).id(CHECKED);

    Assertions.assertFalse(checkboxWithNoText.isChecked());
    Assertions.assertFalse(checkboxWithText.isChecked());
    Assertions.assertTrue(checkboxInitiallyChecked.isChecked());

    checkboxWithNoText.setChecked(true);
    Assertions.assertTrue(checkboxWithNoText.isChecked());
    checkboxWithText.setChecked(true);
    Assertions.assertTrue(checkboxWithText.isChecked());
    checkboxInitiallyChecked.setChecked(false);
    Assertions.assertFalse(checkboxInitiallyChecked.isChecked());

    checkboxWithNoText.setChecked(false);
    Assertions.assertFalse(checkboxWithNoText.isChecked());
    checkboxWithText.setChecked(false);
    Assertions.assertFalse(checkboxWithText.isChecked());
    checkboxInitiallyChecked.setChecked(true);
    Assertions.assertTrue(checkboxInitiallyChecked.isChecked());
  }

  @VaadinTest(navigateAsString = NAV)
  public void getLabel(GenericTestPageObject po) throws Exception {

    final CheckboxElement checkboxWithText = po.$(CheckboxElement.class).id(TEXT);
    final CheckboxElement checkboxWithNoText = po.$(CheckboxElement.class).id(NOTEXT);
    final CheckboxElement checkboxInitiallyChecked = po.$(CheckboxElement.class).id(CHECKED);

    Assertions.assertEquals("Text" , checkboxWithText.getLabel());
    Assertions.assertEquals("" , checkboxWithNoText.getLabel());
    Assertions.assertEquals("Checked initially" ,
                            checkboxInitiallyChecked.getLabel());
  }

}
