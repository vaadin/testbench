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
package junit.com.vaadin.testbench.tests.component.notification;

import static com.vaadin.flow.component.notification.testbench.test.NotificationView.NAV;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.notification.testbench.test.NotificationView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinTest
public class NotificationIT extends AbstractIT {

  @VaadinTest(navigateAsString = NAV)
  public void getText(GenericTestPageObject po) throws Exception {
    final NotificationElement noText = po.notification().id(NotificationView.NOTEXT);
    final NotificationElement text = po.notification().id(NotificationView.TEXT);

    Assertions.assertEquals("" , noText.getText());
    Assertions.assertEquals("Some text" , text.getText());
  }

  @VaadinTest(navigateAsString = NAV)
  public void isOpen(GenericTestPageObject po) throws Exception {
    final NotificationElement noText = po.notification().id(NotificationView.NOTEXT);
    final NotificationElement text = po.notification().id(NotificationView.TEXT);
    final NotificationElement components = po.notification().id(NotificationView.COMPONENTS);

    Assertions.assertTrue(noText.isOpen());
    Assertions.assertTrue(text.isOpen());
    Assertions.assertTrue(components.isOpen());
    components.$(ButtonElement.class).id("close").click();
    Assertions.assertFalse(components.isOpen());
  }

  @VaadinTest(navigateAsString = NAV)
  public void findAllNotifications(GenericTestPageObject po) throws Exception {
    List<NotificationElement> notifications = po.$(NotificationElement.class).all();
    Assertions.assertEquals(3 , notifications.size());
  }

  @VaadinTest(navigateAsString = NAV)
  public void componentInsideNotification(GenericTestPageObject po) {
    final NotificationElement components = po.notification().id(NotificationView.COMPONENTS);

    ButtonElement hello = components.$(ButtonElement.class).id("hello");
    ButtonElement close = components.$(ButtonElement.class).id("close");
    hello.click();
    Assertions.assertEquals("1. Hello in notification clicked" , getLogRow(po,0));
    close.click();
    Assertions.assertFalse(components.isOpen());
  }

}
