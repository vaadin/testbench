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
 * If not, see <http://vaadin.com/license/cval-3.0>.
 */
package com.vaadin.testbench.elements;

import com.vaadin.testbench.TestBench;

@ServerClass("com.vaadin.ui.AbstractSplitPanel")
public class AbstractSplitPanelElement extends
        AbstractComponentContainerElement {

    /**
     * Gets the first component of a split panel and wraps it in given class.
     * 
     * @param clazz
     *          Components element class
     * @return First component wrapped in given class
     */
    public <T extends AbstractElement> T getFirstComponent(Class<T> clazz) {
        return TestBench.createElement(clazz, $$(AbstractComponentElement.class)
                .first().getWrappedElement(), getCommandExecutor());
    }

    /**
     * Gets the second component of a split panel and wraps it in given class.
     * 
     * @param clazz
     *          Components element class
     * @return Second component wrapped in given class
     */
    public <T extends AbstractElement> T getSecondComponent(Class<T> clazz) {
        return TestBench.createElement(clazz, $$(AbstractComponentElement.class)
                .get(1).getWrappedElement(), getCommandExecutor());
    }

}
