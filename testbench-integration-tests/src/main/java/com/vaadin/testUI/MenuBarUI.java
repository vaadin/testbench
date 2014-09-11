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
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 *
 */
@SuppressWarnings("serial")
public class MenuBarUI extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/MenuBarUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MenuBarUI.class)
    public static class Servlet extends VaadinServlet {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createDefaultMenuBar("", ""));
        addComponent(createDefaultMenuBar("2", ""));
        addComponent(createDefaultMenuBar("", "2"));
    }

    private MenuBar createDefaultMenuBar(String topLevelItemSuffix,
            String secondaryLevelItemSuffix) {
        MenuBar menuBar = new MenuBar();
        MenuItem file = menuBar.addItem("File" + topLevelItemSuffix, null);
        file.addItem("Open" + secondaryLevelItemSuffix, new MenuBarCommand());
        file.addItem("Save" + secondaryLevelItemSuffix, new MenuBarCommand());
        file.addItem("Save As.." + secondaryLevelItemSuffix,
                new MenuBarCommand());
        file.addSeparator();

        MenuItem export = file.addItem("Export.." + secondaryLevelItemSuffix,
                null);
        export.addItem("As PDF..." + secondaryLevelItemSuffix,
                new MenuBarCommand());
        export.addItem("As Doc..." + secondaryLevelItemSuffix,
                new MenuBarCommand());

        file.addSeparator();
        file.addItem("Exit" + secondaryLevelItemSuffix, new MenuBarCommand());

        MenuItem edit = menuBar.addItem("Edit" + topLevelItemSuffix, null);
        edit.addItem("Copy" + secondaryLevelItemSuffix, new MenuBarCommand());
        edit.addItem("Cut" + secondaryLevelItemSuffix, new MenuBarCommand());
        edit.addItem("Paste" + secondaryLevelItemSuffix, new MenuBarCommand());

        menuBar.addItem("Help" + topLevelItemSuffix, new MenuBarCommand());
        return menuBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "UI used to validate MenuBarElement API";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13364;
    }

    private class MenuBarCommand implements Command {

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.ui.MenuBar.Command#menuSelected(com.vaadin.ui.MenuBar.
         * MenuItem)
         */
        public void menuSelected(MenuItem selectedItem) {
        }

    }
}
