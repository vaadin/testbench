package com.vaadin.testbench;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.proxy.DriverInvocationHandler;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestBench {

    private static final Map<Class<?>, MethodFilter> methodFilters = new ConcurrentHashMap<>();

    public static TestBenchDriverProxy createDriver(WebDriver driver) {
        TestBenchCommandExecutor commandExecutor = new TestBenchCommandExecutor();
        return createDriver(driver, commandExecutor);
    }

    public static TestBenchDriverProxy createDriver(WebDriver driver,
                                                    TestBenchCommandExecutor commandExecutor) {
        Set<Class<?>> allInterfaces = extractInterfaces(driver);
        Class<TestBenchDriverProxy> driverClass = TestBenchDriverProxy.class;
        allInterfaces.addAll(extractInterfaces(driverClass));
        final Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[0]);

        ProxyFactory pFactory = new ProxyFactory();
        pFactory.setInterfaces(allInterfacesArray);
        pFactory.setSuperclass(driverClass);

        TestBenchDriverProxy proxy;
        try {
            proxy = (TestBenchDriverProxy) pFactory.create(
                    new Class[]{WebDriver.class,
                            TestBenchCommandExecutor.class},
                    new Object[]{driver, commandExecutor},
                    new DriverInvocationHandler(driver));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create proxy for driver",
                    e);
        }

        commandExecutor.setDriver(proxy);
        return proxy;
    }

    public static <T extends TestBenchElement> T wrap(TestBenchElement element,
                                                      Class<T> elementType) {
        return createElement(elementType, element.getWrappedElement(),
                element.getCommandExecutor());
    }

    public static TestBenchElement createElement(WebElement webElement,
                                                 TestBenchCommandExecutor tbCommandExecutor) {
        return createElement(TestBenchElement.class, webElement,
                tbCommandExecutor);
    }

    /**
     * Create new Element of given type. Initialize it with WebElement and
     * TestBenchCommandExecutor. This feature is advanced and potentially
     * dangerous.
     *
     * @param clazz             Class of wanted Element
     * @param webElement        Selenium WebElement to be wrapped into given Class
     * @param tbCommandExecutor TestBenchCommandExecutor instance
     * @return an element of the given class wrapping given the given
     * WebElement, or <code>null</code> if the element is null
     */
    public static <T extends TestBenchElement> T createElement(Class<T> clazz,
                                                               WebElement webElement,
                                                               TestBenchCommandExecutor tbCommandExecutor) {
        if (webElement == null) {
            return null;
        }
        Set<Class<?>> allInterfaces = extractInterfaces(webElement);

        final Class<?>[] allInterfacesArray = allInterfaces.toArray(new Class<?>[0]);

        ProxyFactory pFactory = new ProxyFactory();
        pFactory.setSuperclass(clazz);
        pFactory.setInterfaces(allInterfacesArray);
        pFactory.setFilter(getMethodFilter(clazz));

        Object proxyObject;
        try {
            proxyObject = pFactory.create(new Class[0], new Object[0],
                    new ElementInvocationHandler(webElement));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to create an element of type " + clazz.getName()
                            + " wrapping " + webElement,
                    e);
        }

        @SuppressWarnings("unchecked")
        T proxy = (T) proxyObject;
        proxy.init(webElement, tbCommandExecutor);
        return proxy;
    }

    private static MethodFilter getMethodFilter(
            Class<? extends TestBenchElement> clazz) {
        if (!methodFilters.containsKey(clazz)) {
            methodFilters.put(clazz, new ElementMethodFilter(clazz));
        }
        return methodFilters.get(clazz);
    }

    private static Set<Class<?>> extractInterfaces(final Object object) {
        return extractInterfaces(object.getClass());
    }

    private static Set<Class<?>> extractInterfaces(final Class<?> clazz) {
        final Set<Class<?>> allInterfaces = new HashSet<>();
        extractInterfaces(allInterfaces, clazz);

        return allInterfaces;
    }

    private static void extractInterfaces(final Set<Class<?>> addTo,
                                          final Class<?> clazz) {
        if (clazz == null || Object.class.equals(clazz)) {
            return; // Done
        }

        final Class<?>[] classes = clazz.getInterfaces();
        for (final Class<?> interfaceClass : classes) {
            addTo.add(interfaceClass);
            for (final Class<?> superInterface : interfaceClass
                    .getInterfaces()) {
                addTo.add(superInterface);
                extractInterfaces(addTo, superInterface);
            }
        }

        extractInterfaces(addTo, clazz.getSuperclass());
    }

    private static final class ElementMethodFilter implements MethodFilter {

        private Class<?> proxyClass;
        private Map<Method, Boolean> invocationNeeded;

        public ElementMethodFilter(Class<?> clazz) {
            proxyClass = clazz;
            invocationNeeded = new ConcurrentHashMap<>();
        }

        @Override
        public boolean isHandled(Method method) {
            if (!invocationNeeded.containsKey(method)) {
                try {
                    proxyClass.getMethod(method.getName(),
                            method.getParameterTypes());
                    invocationNeeded.put(method, false);
                } catch (Exception e) {
                    invocationNeeded.put(method, true);
                }
            }

            return invocationNeeded.get(method);
        }
    }

    private static final class ElementInvocationHandler
            implements MethodHandler {

        private Object actualElement;

        public ElementInvocationHandler(Object actualElement) {
            this.actualElement = actualElement;
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed,
                             Object[] args) throws Throwable {
            if (null != proceed) {
                // This is a protected method.
                try {
                    return proceed.invoke(self, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
            return thisMethod.invoke(actualElement, args);
        }
    }
}
