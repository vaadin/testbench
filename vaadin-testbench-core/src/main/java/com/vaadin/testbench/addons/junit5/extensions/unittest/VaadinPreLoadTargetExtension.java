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

import com.vaadin.testbench.LoadMode;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.PAGE_OBJECT_NAVIGATION_TARGET;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.PAGE_OBJECT_PRELOAD;

public class VaadinPreLoadTargetExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context)  {
        context.getTestMethod()
                .ifPresent(m -> {
                    final VaadinTest annotation = m.getAnnotation(VaadinTest.class);

                    if (annotation != null) {
                        final boolean preLoad = annotation.loadMode() != LoadMode.NO_PRELOAD;
                        storeMethodPlain(context).put(PAGE_OBJECT_PRELOAD, preLoad);

                        final String target = annotation.navigateTo();
                        if (!"|||DEFAULT_NAVIGATION_TARGET|||".equals(target)) {
                            storeMethodPlain(context).put(PAGE_OBJECT_NAVIGATION_TARGET, target);
                        }
                    }
                });
    }
}
