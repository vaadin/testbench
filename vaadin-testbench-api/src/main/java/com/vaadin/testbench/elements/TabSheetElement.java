/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.TabSheet")
public class TabSheetElement extends AbstractComponentContainerElement {

    protected org.openqa.selenium.By byTabCell = By
            .className("v-tabsheet-tabitem");
    private static org.openqa.selenium.By byCaption = By
            .className("v-captiontext");
    private static org.openqa.selenium.By byClosable = By
            .className("v-tabsheet-caption-close");

    /**
     * Gets a list of Tabs inside the Tab container.
     * 
     * @return List of tabs
     */
    public List<String> getTabCaptions() {
        List<String> tabCaptions = new ArrayList<String>();
        for (WebElement tab : findElements(byTabCell)) {
            tabCaptions.add(tab.findElement(byCaption).getText());
        }
        return tabCaptions;
    }

    /**
     * Opens a Tab that has caption equal to given tabCaption.
     * 
     * @param tabCaption
     *            Caption of the tab to be opened
     */
    public void openTab(String tabCaption) {
        for (WebElement tabCell : findElements(byTabCell)) {
            WebElement tab = tabCell.findElement(byCaption);
            if (tabCaption.equals(tab.getText())) {
                tab.click();
            }
        }
    }

    /**
     * If tab with given caption is closable, closes it.
     * 
     * @param tabCaption
     *            Caption of the tab to be opened
     */
    public void closeTab(String tabCaption) {
        for (WebElement tabCell : findElements(byTabCell)) {
            WebElement tab = tabCell.findElement(byCaption);
            if (tabCaption.equals(tab.getText())) {
                try {
                    tabCell.findElement(byClosable).click();
                    // Going further causes a StaleElementReferenceException.
                    return;
                } catch (NoSuchElementException e) {
                    // Do nothing.
                }
            }
        }
    }

    /**
     * Gets TabSheet content and wraps it in given class.
     * 
     * @param clazz
     *            Components element class
     * @return TabSheet content wrapped in given class
     */
    public <T extends AbstractElement> T getContent(Class<T> clazz) {
        return TestBench.createElement(clazz,
                $$(AbstractComponentElement.class).first().getWrappedElement(),
                getCommandExecutor());
    }
}
