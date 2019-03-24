package junit.com.vaadin.testbench.tests.component.combobox;

import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.BEANS;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.BEANS_WITH_PRE_SLELECTED_VALUE;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.LAZY;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.LAZY_WITH_PRE_SLELECTED_VALUE;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.NAV;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.NOTEXT;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.NOTEXT_WITH_PRE_SLELECTED_VALUE;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.PRE_SELECTED_PERSON_FOR_COMBOBOX_WITH_BEANS;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.PRE_SELECTED_VALUE_FOR_COMBOBOX_LAZY;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.PRE_SELECTED_VALUE_FOR_COMBOBOX_WITHOUT_TEXT;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.PRE_SELECTED_VALUE_FOR_COMBOBOX_WITH_TEXT;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.TEXT;
import static com.vaadin.flow.component.combobox.testbench.test.ComboBoxView.TEXT_WITH_PRE_SLELECTED_VALUE;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class ComboBoxIT extends AbstractIT {

  public static class PO extends GenericTestPageObject {

    public PO(WebDriver webdriver , ContainerInfo containerInfo) {
      super(webdriver , containerInfo);
    }

    @Override
    public void loadPage() {
      loadPage(NAV);
    }
  }

  @VaadinTest
  public void getLabel(PO po) throws Exception {

    final  ComboBoxElement comboBoxWithText = po.comboBox().id(TEXT);
    final ComboBoxElement comboBoxWithNoText = po.comboBox().id(NOTEXT);
    final ComboBoxElement comboBoxWithBeans = po.comboBox().id(BEANS);
    final ComboBoxElement comboBoxWithLazy = po.comboBox().id(LAZY);

    Assertions.assertEquals("" , comboBoxWithNoText.getLabel());
    Assertions.assertEquals("Text" , comboBoxWithText.getLabel());
    Assertions.assertEquals("Lazy" , comboBoxWithLazy.getLabel());
    Assertions.assertEquals("Persons" , comboBoxWithBeans.getLabel());
  }

  @VaadinTest
  public void selectByText(PO po) throws Exception {

    final  ComboBoxElement comboBoxWithText = po.comboBox().id(TEXT);
    final ComboBoxElement comboBoxWithNoText = po.comboBox().id(NOTEXT);
    final ComboBoxElement comboBoxWithBeans = po.comboBox().id(BEANS);
    final ComboBoxElement comboBoxWithLazy = po.comboBox().id(LAZY);

    Assertions.assertEquals("" , comboBoxWithNoText.getSelectedText());
    Assertions.assertEquals("" , comboBoxWithText.getSelectedText());
    Assertions.assertEquals("" , comboBoxWithLazy.getSelectedText());
    Assertions.assertEquals("" , comboBoxWithBeans.getSelectedText());

    comboBoxWithNoText.selectByText("Item 1");
    Assertions.assertEquals("1. ComboBox 'null' value is now Item 1" ,
                            getLogRow(po,0));
    Assertions.assertEquals("Item 1" , comboBoxWithNoText.getSelectedText());

    comboBoxWithText.selectByText("Item 18");
    Assertions.assertEquals("2. ComboBox 'Text' value is now Item 18" ,
                            getLogRow(po,0));
    Assertions.assertEquals("Item 18" , comboBoxWithText.getSelectedText());

    comboBoxWithLazy.selectByText("Item 400");
    Assertions.assertEquals("3. ComboBox 'Lazy' value is now Item 400" ,
                            getLogRow(po,0));
    Assertions.assertEquals("Item 400" , comboBoxWithLazy.getSelectedText());

    comboBoxWithBeans.selectByText("Doe, John");
    Assertions.assertEquals(
        "4. ComboBox 'Persons' value is now Person [firstName=John, lastName=Doe, age=20]" ,
        getLogRow(po,0));
    Assertions.assertEquals("Doe, John" , comboBoxWithBeans.getSelectedText());
  }

  @VaadinTest
  public void getSelectedText(PO po) {

    final ComboBoxElement comboBoxWithTextWithPreSelectedValue = po.comboBox().id(TEXT_WITH_PRE_SLELECTED_VALUE);
    final ComboBoxElement comboBoxWithNoTextWithPreSelectedValue = po.comboBox().id(NOTEXT_WITH_PRE_SLELECTED_VALUE);
    final ComboBoxElement comboBoxLazyWithPreSelectedValue = po.comboBox().id(LAZY_WITH_PRE_SLELECTED_VALUE);
    final ComboBoxElement comboBoxWithBeansWithPreSelectedValue = po.comboBox().id(BEANS_WITH_PRE_SLELECTED_VALUE);

    Assertions.assertEquals(PRE_SELECTED_VALUE_FOR_COMBOBOX_WITHOUT_TEXT ,
                            comboBoxWithNoTextWithPreSelectedValue.getSelectedText());
    Assertions.assertEquals(PRE_SELECTED_VALUE_FOR_COMBOBOX_WITH_TEXT ,
                            comboBoxWithTextWithPreSelectedValue.getSelectedText());
    Assertions.assertEquals(PRE_SELECTED_VALUE_FOR_COMBOBOX_LAZY ,
                            comboBoxLazyWithPreSelectedValue.getSelectedText());
    Assertions.assertEquals(PRE_SELECTED_PERSON_FOR_COMBOBOX_WITH_BEANS.getLastName() + ", " +
                            PRE_SELECTED_PERSON_FOR_COMBOBOX_WITH_BEANS.getFirstName() ,
                            comboBoxWithBeansWithPreSelectedValue.getSelectedText());
  }

  @VaadinTest
  public void openCloseIsOpenPopup(PO po) throws Exception {

    final  ComboBoxElement comboBoxWithText = po.comboBox().id(TEXT);
    comboBoxWithText.openPopup();
    Assertions.assertTrue(comboBoxWithText.isPopupOpen());
    comboBoxWithText.closePopup();
    Assertions.assertFalse(comboBoxWithText.isPopupOpen());
  }

  @VaadinTest
  public void getPopupSuggestions(PO po) throws Exception {

    final  ComboBoxElement comboBoxWithText = po.comboBox().id(TEXT);
    final ComboBoxElement comboBoxWithNoText = po.comboBox().id(NOTEXT);
    final ComboBoxElement comboBoxWithBeans = po.comboBox().id(BEANS);

    Assertions.assertArrayEquals(
        IntStream.range(0 , 20).mapToObj(i -> "Item " + i).toArray() ,
        comboBoxWithNoText.getOptions().toArray());
    Assertions.assertArrayEquals(
        IntStream.range(0 , 20).mapToObj(i -> "Item " + i).toArray() ,
        comboBoxWithText.getOptions().toArray());
    Assertions.assertArrayEquals(
        new String[]{"Doe, John" , "Johnson, Jeff" , "Meyer, Diana"} ,
        comboBoxWithBeans.getOptions().toArray());
  }

  @VaadinTest
  public void filter(PO po) {

    final  ComboBoxElement comboBoxWithText = po.comboBox().id(TEXT);
    final ComboBoxElement comboBoxWithNoText = po.comboBox().id(NOTEXT);
    final ComboBoxElement comboBoxWithBeans = po.comboBox().id(BEANS);
    Assertions.assertEquals("" , comboBoxWithNoText.getFilter());
    Assertions.assertEquals("" , comboBoxWithText.getFilter());
    Assertions.assertEquals("" , comboBoxWithBeans.getFilter());

    comboBoxWithNoText.setFilter("2");
    Assertions.assertArrayEquals(new String[]{"Item 2" , "Item 12"} ,
                                 comboBoxWithNoText.getOptions().toArray());
    comboBoxWithText.setFilter("2");
    Assertions.assertArrayEquals(new String[]{"Item 2" , "Item 12"} ,
                                 comboBoxWithText.getOptions().toArray());
    comboBoxWithBeans.setFilter("Jo");
    Assertions.assertArrayEquals(new String[]{"Doe, John" , "Johnson, Jeff"} ,
                                 comboBoxWithBeans.getOptions().toArray());

  }

}
