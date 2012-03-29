package com.vaadin.testbench;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
                // Is it a method in the TestBenchDriver?
                actualMethod = TestBenchDriver.class.getMethod(
                        method.getName(), method.getParameterTypes());
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
        if (proxyObject instanceof TestBenchDriver) {
            ((TestBenchDriver) proxyObject).waitForVaadin();
        }
    }

    private static final List<String> methodsNotNeedingWaitForVaadin = Arrays
            .asList("close", "getRemoteControlName", "getWrappedDriver",
                    "navigate", "quit", "setTestName");

    private boolean shouldNotWaitForVaadin(String methodName) {
        return methodsNotNeedingWaitForVaadin.contains(methodName);
    }

    private boolean isMethodCached(Method method) {
        return proxiedMethodCache.containsKey(method)
                || implementedMethodCache.containsKey(method);
    }
}
