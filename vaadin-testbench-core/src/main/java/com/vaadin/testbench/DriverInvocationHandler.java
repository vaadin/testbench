/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.util.proxy.MethodHandler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.CanWaitForVaadin;

public class DriverInvocationHandler implements MethodHandler {

    private Map<Method, Boolean> proxyMethod = new HashMap<Method, Boolean>();
    private final Object actualObject;

    public DriverInvocationHandler(Object actualObject) {
        this.actualObject = actualObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
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
    private void waitForVaadinIfNecessary(Object proxyObject, String methodName) {
        if (shouldWaitForVaadin(methodName)
                && proxyObject instanceof CanWaitForVaadin) {
            ((CanWaitForVaadin) proxyObject).waitForVaadin();
        }
    }

    private static final List<String> methodsNotNeedingWaitForVaadin = Arrays
            .asList("close", "get", "getRemoteControlName", "manage",
                    "getWrappedDriver", "navigate", "quit", "setTestName",
                    "switchTo", "waitForVaadin", "enableWaitForVaadin",
                    "disableWaitForVaadin");

    private boolean shouldWaitForVaadin(String methodName) {
        return !methodsNotNeedingWaitForVaadin.contains(methodName);
    }

}
