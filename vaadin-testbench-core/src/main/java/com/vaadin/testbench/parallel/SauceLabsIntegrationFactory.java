package com.vaadin.testbench.parallel;

import java.util.logging.Level;
import java.util.logging.Logger;

final class SauceLabsIntegrationFactory {

    static final Logger logger = Logger
            .getLogger(SauceLabsIntegrationFactory.class.getName());
    private static final SauceLabsIntegration INSTANCE;
    
    static {
        SauceLabsIntegration instance = null;
        try {
            Class<?> clazz = SauceLabsIntegration.class.getClassLoader()
                    .loadClass(
                            "com.vaadin.testbench.parallel.DefaultSauceLabsIntegration");
            try {
                instance = (SauceLabsIntegration) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.log(Level.WARNING,
                        "Failed to create SauceLabsIntegration");
            }
        } catch (ClassNotFoundException e) {
            // Not in class path. Carry on
        }
        INSTANCE = instance;
    }



    private SauceLabsIntegrationFactory() {
    }

    static SauceLabsIntegration tryGet() {
        return INSTANCE;
    }
}
