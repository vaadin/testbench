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

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.InlineDateField")
public class InlineDateFieldElement extends DateFieldElement {

    /**
     * Operation is not supported
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public String getValue() {
        throw new UnsupportedOperationException();
    }

    /**
     * Operation is not supported
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public void setValue(CharSequence chars) throws ReadOnlyException {
        throw new UnsupportedOperationException();
    }
}
