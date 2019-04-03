package com.vaadin.testbench.addons.junit5.extensions.unittest;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectFunctions.PAGE_OBJECT_NAVIGATION_TARGET;
import static com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectFunctions.PAGE_OBJECT_PRELOAD;

public class VaadinPreLoadTargetExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context)  {
        context.getTestMethod()
                .ifPresent(m -> {
                    final VaadinTest annotation = m.getAnnotation(VaadinTest.class);

                    if (annotation != null) {
                        final boolean preLoad = annotation.preLoad();
                        storeMethodPlain().apply(context).put(PAGE_OBJECT_PRELOAD, preLoad);
                        final String target = annotation.navigateAsString();
                        if (target != null && !target.isEmpty()) {
                            storeMethodPlain().apply(context).put(PAGE_OBJECT_NAVIGATION_TARGET, target);
                        }
                    }
                });
    }
}
