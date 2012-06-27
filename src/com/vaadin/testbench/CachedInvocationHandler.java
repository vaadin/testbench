package com.vaadin.testbench;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.commands.CanWaitForVaadin;

public class CachedInvocationHandler implements InvocationHandler {

    private HashMap<Method, Method> proxiedMethodCache = new HashMap<Method, Method>();
    private HashMap<Method, Method> implementedMethodCache = new HashMap<Method, Method>();
    private final Object proxyObject;
    private final Object actualObject;

    public CachedInvocationHandler(Object proxyObject, Object actualObject) {
        this.proxyObject = proxyObject;
        this.actualObject = actualObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        waitForVaadinIfNecessary(method.getName());
        if (!isMethodCached(method)) {
            Method actualMethod = null;
            try {
                if (actualObject instanceof WebDriver) {
                    // Is it a method in the TestBenchDriver?
                    actualMethod = TestBenchDriver.class.getMethod(
                            method.getName(), method.getParameterTypes());
                } else {
                    actualMethod = TestBenchElement.class.getMethod(
                            method.getName(), method.getParameterTypes());
                }
                implementedMethodCache.put(method, actualMethod);
            } catch (Exception e) {
                // It's probably a method implemented by the actual driver.
                actualMethod = actualObject.getClass().getMethod(
                        method.getName(), method.getParameterTypes());
                proxiedMethodCache.put(method, actualMethod);
            }
        }
        if (proxiedMethodCache.containsKey(method)) {
            return proxiedMethodCache.get(method).invoke(actualObject, args);
        }
        return implementedMethodCache.get(method).invoke(proxyObject, args);
    }

    /**
     * Invokes waitForVaadin unless the command is known to not need to wait.
     * 
     * @param methodName
     */
    private void waitForVaadinIfNecessary(String methodName) {
        if (shouldNotWaitForVaadin(methodName)) {
            return;
        }
        if (proxyObject instanceof CanWaitForVaadin) {
            ((CanWaitForVaadin) proxyObject).waitForVaadin();
        }
    }

    private static final List<String> methodsNotNeedingWaitForVaadin = Arrays
            .asList("close", "get", "getRemoteControlName", "getWrappedDriver",
                    "manage", "navigate", "quit", "setTestName", "switchTo",
                    "waitForVaadin", "enableWaitForVaadin",
                    "disableWaitForVaadin");

    private boolean shouldNotWaitForVaadin(String methodName) {
        return methodsNotNeedingWaitForVaadin.contains(methodName);
    }

    private boolean isMethodCached(Method method) {
        return proxiedMethodCache.containsKey(method)
                || implementedMethodCache.containsKey(method);
    }
}
