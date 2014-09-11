/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

public class NotificationElementGetCaption extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/NotificationElementGetCaption/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = NotificationElementGetCaption.class)
    public static class Servlet extends VaadinServlet {
    }

    public final static String CAPTION = "Notification";

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox cb = new ComboBox();
        cb.setCaption("some caption");
        Notification.show(CAPTION);
        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Test getCaption method for vaadin components";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14453;
    }

}
