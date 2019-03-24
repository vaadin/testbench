package junit.com.vaadin.testbench.tests.component.radiobutton;

import static com.vaadin.flow.component.radiobutton.testbench.test.RadioButtonView.NAV;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.radiobutton.testbench.test.RadioButtonView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class RadioButtonIT extends AbstractIT {

  @VaadinTest(navigateAsString = NAV)
  public void getOptions(GenericTestPageObject po) throws Exception {
    final RadioButtonGroupElement def = po.radioBtnGrp().id(RadioButtonView.DEFAULT);
    final RadioButtonGroupElement preselected = po.radioBtnGrp()
                                                  .id(RadioButtonView.PRESELECTED);

    Assertions.assertArrayEquals(new String[]{"Item 0" , "Item 1" , "Item 2" ,
        "Item 3" , "Item 4"} , def.getOptions().toArray());
    Assertions.assertArrayEquals(new String[]{"Item 0" , "Item 1" , "Item 2" ,
        "Item 3" , "Item 4"} , preselected.getOptions().toArray());
  }

  @VaadinTest(navigateAsString = NAV)
  public void getSetByText(GenericTestPageObject po) throws Exception {
    final RadioButtonGroupElement def = po.radioBtnGrp().id(RadioButtonView.DEFAULT);
    final RadioButtonGroupElement preselected = po.radioBtnGrp()
                                                  .id(RadioButtonView.PRESELECTED);

    Assertions.assertNull(def.getSelectedText());
    Assertions.assertEquals("Item 3" , preselected.getSelectedText());

    def.selectByText("Item 2");
    Assertions.assertEquals("Item 2" , def.getSelectedText());
    preselected.selectByText("Item 2");
    Assertions.assertEquals("Item 2" , preselected.getSelectedText());
  }

}
