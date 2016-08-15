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
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 */
@SuppressWarnings("serial")
public class ButtonUI extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/ButtonUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ButtonUI.class)
    public static class Servlet extends VaadinServlet {
    }

    final TextField testedField= new TextField();
    public static String QUITE_BUTTON_ID="quiteButton";
    public static String QUITE_BUTTON_NO_CAPTION_ID="quiteButton2";
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(testedField);
        testedField.setValue("");

        Button quiteButton= new Button("Quite Button");
        quiteButton.setId(QUITE_BUTTON_ID);
        quiteButton.addStyleName(ValoTheme.BUTTON_QUIET);
        addListener(quiteButton);

        Button quiteButtonNoCaption= new Button("");
        quiteButtonNoCaption.setId(QUITE_BUTTON_NO_CAPTION_ID);
        quiteButtonNoCaption.addStyleName(ValoTheme.BUTTON_QUIET);
        quiteButtonNoCaption.setIcon(FontAwesome.ANDROID);
        addListener(quiteButtonNoCaption);

        addComponent(quiteButton);
        addComponent(quiteButtonNoCaption);

    }

    private void addListener(Button button){
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                testedField.setValue("Clicked");
            }
        });
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test button click, for button with ValoTheme.BUTTON_QUIET style";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 16346;
    }

}
