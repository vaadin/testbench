package com.vaadin.testbench.addons.webdriver;

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

/**
 * Enum out of the interface BrowserType from Selenium.
 */
public enum BrowserType {

    FIREFOX("firefox"),
    FIREFOX_PROXY("firefoxproxy"),
    FIREFOX_CHROME("firefoxchrome"),
    GOOGLECHROME("googlechrome"),
    SAFARI("safari"),
    /**
     * @deprecated Use OPERA_BLINK
     */
    @Deprecated
    OPERA("opera"),
    OPERA_BLINK("operablink"),
    EDGE("MicrosoftEdge"),
    IEXPLORE("iexplore"),
    IEXPLORE_PROXY("iexploreproxy"),
    SAFARI_PROXY("safariproxy"),
    CHROME("chrome"),
    KONQUEROR("konqueror"),
    MOCK("mock"),
    IE_HTA("iehta"),

    ANDROID("android"),
    HTMLUNIT("htmlunit"),
    IE("internet explorer"),
    IPHONE("iPhone"),
    IPAD("iPad"),
    PHANTOMJS("phantomjs");

    private String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    String browserName() {
        return browserName;
    }
}
