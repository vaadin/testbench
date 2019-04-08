package com.vaadin.testbench.proxy;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverInvocationHandler implements MethodHandler {

    private static final List<String> methodsNotNeedingWaitForVaadin = Arrays
            .asList("close", "get", "getRemoteControlName", "manage",
                    "getWrappedDriver", "navigate", "quit", "setTestName",
                    "switchTo", "waitForVaadin", "enableWaitForVaadin",
                    "disableWaitForVaadin", "getCommandExecutor");
    private final Object actualObject;
    private Map<Method, Boolean> proxyMethod = new HashMap<>();

    public DriverInvocationHandler(Object actualObject) {
        this.actualObject = actualObject;
    }

    @Override
    public Object invoke(Object proxy,
                         Method method,
                         Method proceed,
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

    private boolean shouldWaitForVaadin(String methodName) {
        return !methodsNotNeedingWaitForVaadin.contains(methodName);
    }
}
