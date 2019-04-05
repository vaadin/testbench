package com.vaadin.testbench.addons.junit5.extensions.resolver.extensioncontext;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ExtensionContextParameterResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {

        final Class<?> type = parameterContext.getParameter().getType();
        return ExtensionContext.class.isAssignableFrom(type);

    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        if (ExtensionContext.class.isAssignableFrom(parameterContext.getParameter().getType())) {
            return extensionContext;
        } else {
            throw new ParameterResolutionException("was not able to redirect ExtensionContext instance");
        }
    }
}
