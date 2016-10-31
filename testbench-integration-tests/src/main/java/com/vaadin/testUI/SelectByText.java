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
import com.vaadin.ui.VerticalLayout;

/**
 * UI used to validate ComboBox.selectByText(String s) works properly if input
 * String s contains parentheses
 */
@SuppressWarnings("serial")
public class SelectByText extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/SelectByText/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = SelectByText.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        addComponent(layout);

        ComboBox combobox = new ComboBox();
        combobox.addItem("Value 1");
        combobox.addItem("(");
        combobox.addItem("(Value");
        combobox.addItem("Value 2");
        combobox.addItem("Value(");
        combobox.addItem("Value(i)");
        combobox.addItem("((Test ) selectByTest() method(with' parentheses)((");
        combobox.addItem("Value 3");
        layout.addComponent(combobox);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox's selectByText(String text) method should work if text contains parentheses";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14048;
    }

}
