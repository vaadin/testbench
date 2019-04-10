package com.vaadin.testbench.parallel;

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

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.BrowserType;

/**
 * This Util Class is for compatibility only.
 */
@Deprecated
public class BrowserUtil {

    @Deprecated
    public static boolean isEdge(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return BrowserType.EDGE.equals(capabilities.getBrowserName());
    }
}
