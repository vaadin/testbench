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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.PAGE_OBJECT_NAVIGATION_TARGET;
import static com.vaadin.testbench.addons.junit5.extensions.unittest.PageObjectFunctions.PAGE_OBJECT_PRELOAD;

public class VaadinPreloadTargetExtension implements BeforeEachCallback {

    static final String DEFAULT_NAVIGATION_TARGET = "|||DEFAULT_NAVIGATION_TARGET|||";

    @Override
    public void beforeEach(ExtensionContext context)  {
        context.getTestMethod()
                .ifPresent(method -> {
                    final LoadMode loadMode = findLoadMode(method);
                    final boolean preLoad = loadMode != LoadMode.NO_PRELOAD;
                    storeMethodPlain(context).put(PAGE_OBJECT_PRELOAD, preLoad);

                    final String target = findNavigationTarget(method);
                    if (!DEFAULT_NAVIGATION_TARGET.equals(target)) {
                        storeMethodPlain(context).put(PAGE_OBJECT_NAVIGATION_TARGET, target);
                    }
                });
    }

    private String findNavigationTarget(AnnotatedElement element) {
        if (element == Object.class) {
            return DEFAULT_NAVIGATION_TARGET;
        }

        final VaadinTest annotation = element.getAnnotation(VaadinTest.class);

        if (annotation == null || DEFAULT_NAVIGATION_TARGET.equals(annotation.navigateTo())) {
            return findNavigationTarget(element instanceof Method
                    ? ((Method) element).getDeclaringClass()
                    : ((Class) element).getSuperclass());
        }

        return annotation.navigateTo();
    }

    private LoadMode findLoadMode(AnnotatedElement element) {
        if (element == Object.class) {
            return LoadMode.DEFAULT;
        }

        final VaadinTest annotation = element.getAnnotation(VaadinTest.class);

        if (annotation == null || LoadMode.DEFAULT == annotation.loadMode()) {
            return findLoadMode(element instanceof Method
                    ? ((Method) element).getDeclaringClass()
                    : ((Class) element).getSuperclass());
        }

        return annotation.loadMode();
    }
}
