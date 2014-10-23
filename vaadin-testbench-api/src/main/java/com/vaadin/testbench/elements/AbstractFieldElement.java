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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.AbstractField")
public class AbstractFieldElement extends AbstractComponentElement {

    /**
     * Select contents of TextField Element
     *
     * NOTE: When testing with firefox browser window should have focus in it
     *
     * @since
     * @param elem
     *            element which context will be select
     */
    protected void clientSelectElement(WebElement elem) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        String script = "window.focus();" + "var elem=arguments[0];"
                + "elem.select();elem.focus();";
        js.executeScript(script, elem);
    }

    protected void clearElementClientSide(WebElement elem) {
        // clears without triggering an event (on client side)
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        String script = "window.focus(); var elem=arguments[0];"
                + "elem.value=\"\";";
        js.executeScript(script, elem);
    }

}
