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

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.AbstractComponent")
public class AbstractComponentElement extends AbstractElement {
    /**
     * Returns the caption of the Component element
     *
     * @since
     * @return
     */
    public String getCaption() {
        final String GWT_ID_ATTRIBUTE = "aria-labelledby";
        WebElement captElem = null;
        String captionId = null;
        captionId = getAttribute(GWT_ID_ATTRIBUTE);
        // IE8 getAttribute returns empty string instead of null
        // when there is no attribute with specified name
        if (captionId == null || captionId.equals("")) {
            WebElement elem = findElement(By.xpath(".//*[@" + GWT_ID_ATTRIBUTE
                    + "]"));
            captionId = elem.getAttribute(GWT_ID_ATTRIBUTE);
        }
        // element ids are unique, we can search the whole page
        captElem = getDriver().findElement(By.id(captionId));
        return captElem.getText();
    }

    public String getHTML() {
        return getWrappedElement().getAttribute("innerHTML");
    }

    public boolean isReadOnly() {
        final String READONLY_CSS_CLASS = "v-readonly";
        String readonlyClass = getAttribute("class");
        // lookin for READONLY_CSS_CLASS string
        String[] cssSelectors = readonlyClass.split("\\s");
        for (String selector : cssSelectors) {
            if (selector.equals(READONLY_CSS_CLASS)) {
                return true;
            }
        }
        return false;
    }

    public class ReadOnlyException extends RuntimeException {

    }
}
