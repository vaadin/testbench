/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.util.proxy.MethodHandler;

public class DriverInvocationHandler implements MethodHandler {

    private Map<Method, Boolean> proxyMethod = new HashMap<>();
    private final Object actualObject;

    public DriverInvocationHandler(Object actualObject) {
        this.actualObject = actualObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Method proceed,
            Object[] args) throws Throwable {
        try {
            waitForVaadinIfNecessary(proxy, method.getName());
            if (!proxyMethod.containsKey(method)) {
                try {
                    TestBenchDriverProxy.class.getMethod(method.getName(),
                            method.getParameterTypes());
                    proxyMethod.put(method, true);
                } catch (NoSuchMethodException e) {
                    actualObject.getClass().getMethod(method.getName(),
                            method.getParameterTypes());
                    proxyMethod.put(method, false);
                }
            }

            if (proxyMethod.get(method)) {
                return proceed.invoke(proxy, args);
            }
            return method.invoke(actualObject, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Invokes waitForVaadin unless the command is known to not need to wait.
     *
     * @param methodName
     */
    private void waitForVaadinIfNecessary(Object proxyObject,
            String methodName) {
        if (shouldWaitForVaadin(methodName)
                && proxyObject instanceof TestBenchDriverProxy) {
            ((TestBenchDriverProxy) proxyObject).getCommandExecutor()
                    .waitForVaadin();
        }
    }

    private static final List<String> methodsNotNeedingWaitForVaadin = Arrays
            .asList("close", "get", "getRemoteControlName", "manage",
                    "getWrappedDriver", "navigate", "quit", "setTestName",
                    "switchTo", "waitForVaadin", "enableWaitForVaadin",
                    "disableWaitForVaadin", "getCommandExecutor");

    private boolean shouldWaitForVaadin(String methodName) {
        return !methodsNotNeedingWaitForVaadin.contains(methodName);
    }

}
