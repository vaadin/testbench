/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.datepicker.testbench.test;

import static com.vaadin.flow.component.datepicker.testbench.test.DatePickerView.NAV;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.common.testbench.test.AbstractIT;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinWebUnitTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinWebUnitTest
public class DatePickerIT extends AbstractIT {

  @VaadinWebUnitTest
  public void setGetDate(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final DatePickerElement datePickerWithText = po.datePicker().id(DatePickerView.TEXT);
    final DatePickerElement datePickerWithNoText = po.datePicker().id(DatePickerView.NOTEXT);
    final DatePickerElement datePickerWithPreSelectedDate = po.datePicker().id(DatePickerView.PRESELECTED);


    Assertions.assertNull(datePickerWithText.getDate());
    Assertions.assertNull(datePickerWithNoText.getDate());
    Assertions.assertEquals(LocalDate.of(2015 , 7 , 13) ,
                            datePickerWithPreSelectedDate.getDate());

    LocalDate newDate = LocalDate.of(2018 , 3 , 22);
    datePickerWithNoText.setDate(newDate);

    Assertions.assertTrue(getLogRow(po,0).contains(
                            "DatePicker 'null' value changed to 2018-03-22"));

    Assertions.assertEquals(newDate , datePickerWithNoText.getDate());

    datePickerWithText.setDate(newDate);
    Assertions.assertTrue(getLogRow(po,0).contains(
                            "DatePicker 'Text' value changed to 2018-03-22"));
    Assertions.assertEquals(newDate , datePickerWithText.getDate());

    datePickerWithPreSelectedDate.setDate(newDate);
    Assertions.assertTrue(getLogRow(po,0).contains(
                            "DatePicker 'Pre selected date (2015-07-13)' value changed to 2018-03-22"));
    Assertions.assertEquals(newDate , datePickerWithPreSelectedDate.getDate());

  }

  @VaadinWebUnitTest
  public void clear(GenericTestPageObject po) {
    po.loadPage(NAV);

    final DatePickerElement datePickerWithText = po.datePicker().id(DatePickerView.TEXT);
    final DatePickerElement datePickerWithNoText = po.datePicker().id(DatePickerView.NOTEXT);
    final DatePickerElement datePickerWithPreSelectedDate = po.datePicker().id(DatePickerView.PRESELECTED);

    datePickerWithText.clear();
    Assertions.assertNull(datePickerWithText.getDate());
    datePickerWithNoText.clear();
    Assertions.assertNull(datePickerWithNoText.getDate());
    datePickerWithPreSelectedDate.clear();
    Assertions.assertNull(datePickerWithPreSelectedDate.getDate());

  }

  @VaadinWebUnitTest
  public void getLabel(GenericTestPageObject po) throws Exception {
    po.loadPage(NAV);

    final DatePickerElement datePickerWithText = po.datePicker().id(DatePickerView.TEXT);
    final DatePickerElement datePickerWithNoText = po.datePicker().id(DatePickerView.NOTEXT);

    Assertions.assertEquals("" , datePickerWithNoText.getLabel());
    Assertions.assertEquals("Text" , datePickerWithText.getLabel());
  }

}
