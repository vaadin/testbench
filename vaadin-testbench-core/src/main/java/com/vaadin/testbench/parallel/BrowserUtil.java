package com.vaadin.testbench.parallel;

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
