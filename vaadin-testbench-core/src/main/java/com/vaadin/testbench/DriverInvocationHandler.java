/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
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
