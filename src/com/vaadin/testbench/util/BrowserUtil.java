package com.vaadin.testbench.util;

import com.thoughtworks.selenium.Selenium;

public class BrowserUtil {

    /**
     * Parses browser name and major version from user agent information
     * 
     * @param selenium
     * @return browserName_majorNumber
     */
    public static BrowserVersion getBrowserVersion(Selenium selenium) {
        String userAgent = selenium.getEval("navigator.userAgent;");
        BrowserVersion bv = new BrowserVersion(userAgent);
        return bv;
    }
}
