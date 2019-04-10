package com.vaadin.testbench.addons.junit5.extensions.unittest;

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

import com.vaadin.testbench.addons.junit5.pageobject.PageObject;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

public interface PageObjectFunctions {

    String PAGE_OBJECT_PRELOAD = "preload";
    String PAGE_OBJECT_NAVIGATION_TARGET = "navigation-target";
    String PAGEOBJECT_STORAGE_KEY = "pageobject";

    static PageObject pageObject(ExtensionContext context) {
        return storeMethodPlain(context).get(PAGEOBJECT_STORAGE_KEY, PageObject.class);
    }

    static void storePageObject(PageObject pageObject, ExtensionContext context) {
        storeMethodPlain(context).put(PAGEOBJECT_STORAGE_KEY, pageObject);
    }

    static void removePageObject(ExtensionContext context) {
        storeMethodPlain(context).remove(PAGEOBJECT_STORAGE_KEY);
    }
}
