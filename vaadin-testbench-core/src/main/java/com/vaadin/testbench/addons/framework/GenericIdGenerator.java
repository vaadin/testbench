package com.vaadin.testbench.addons.framework;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import java.util.Locale;

public interface GenericIdGenerator {

    static String genericId(Class uiClass, Class componentClass, String label) {
        return (uiClass.getSimpleName()
                + "-" + componentClass.getSimpleName()
                + "-" + label.replace(" ", "-"))
                .toLowerCase(Locale.US);
    }
}
