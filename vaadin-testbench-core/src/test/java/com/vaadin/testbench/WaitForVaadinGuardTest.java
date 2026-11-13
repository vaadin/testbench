/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.spi.BrowserBackend;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * Fails when a public element, driver or {@code Actions} method reaches the
 * browser without waiting for Vaadin first: with the proxies gone, each method
 * calls {@code waitForVaadin()} itself, and this is what notices a forgotten
 * one.
 * <p>
 * It calls every method against a fake browser that records each call, and
 * checks that a wait comes before the method's last browser call, the one doing
 * its work. Calls before that may skip the wait, as the visibility probes in
 * {@code getText()} and {@code clear()} always have.
 */
public class WaitForVaadinGuardTest {

    /** TestBench has never waited in these; a new entry needs a reason. */
    private static final Set<String> ELEMENT_METHODS_NOT_WAITING = Set.of(
            // compares document.activeElement, never went through the proxy
            "isFocused");

    /** As the javassist proxy's list had it. */
    private static final Set<String> DRIVER_METHODS_NOT_WAITING = Set.of(
            "close", "get", "manage", "navigate", "quit", "switchTo",
            "getCommandExecutor");

    public static class AllMixinsElement extends TestBenchElement
            implements HasLabel, HasPlaceholder, HasHelper, HasValidation,
            HasClearButton, HasStringValueProperty, HasLabelAsText {
    }

    private final List<String> calls = new ArrayList<>();
    private TestBenchCommandExecutor executor;
    private BrowserDriver driver;
    private TestBenchElement sampleElement;

    @Before
    public void setUp() {
        BrowserBackend backend = Mockito.mock(BrowserBackend.class,
                invocation -> {
                    String name = invocation.getMethod().getName();
                    if (name.equals("getBrowserName")) {
                        // known without asking the browser, so not a browser
                        // call
                        return "chrome";
                    }
                    if (name.equals("executeScript")) {
                        if (invocation.<String> getArgument(0)
                                .contains("isActive()")) {
                            calls.add("wait");
                            return true;
                        }
                        calls.add("script");
                        return null;
                    }
                    calls.add("backend:" + name);
                    return Answers.RETURNS_DEFAULTS.answer(invocation);
                });
        executor = new TestBenchCommandExecutor(null, null);
        driver = TestBench.createDriver(backend, executor);
        sampleElement = TestBench.createElement(TestBenchElement.class, node(),
                executor);
    }

    private ElementBackend node() {
        return Mockito.mock(ElementBackend.class, invocation -> {
            calls.add("node:" + invocation.getMethod().getName());
            return Answers.RETURNS_DEFAULTS.answer(invocation);
        });
    }

    @Test
    public void everyElementMethodWaitsBeforeItsBrowserCall() {
        AllMixinsElement element = TestBench
                .createElement(AllMixinsElement.class, node(), executor);

        Set<String> notWaiting = new TreeSet<>();
        for (Method method : AllMixinsElement.class.getMethods()) {
            if (isTestBenchApi(method)
                    && !ELEMENT_METHODS_NOT_WAITING.contains(method.getName())
                    && !waitsBeforeLastBrowserCall(element, method)) {
                notWaiting.add(describe(method));
            }
        }
        Assert.assertEquals("Element methods reaching the browser without "
                + "waiting for Vaadin", Set.of(), notWaiting);
    }

    @Test
    public void everyDriverMethodWaitsBeforeItsBrowserCall() {
        Set<String> notWaiting = new TreeSet<>();
        for (Method method : BrowserDriver.class.getMethods()) {
            if (!DRIVER_METHODS_NOT_WAITING.contains(method.getName())
                    && !waitsBeforeLastBrowserCall(driver, method)) {
                notWaiting.add(describe(method));
            }
        }
        Assert.assertTrue("activeElement() waits", waitsBeforeLastBrowserCall(
                driver.switchTo(),
                method(BrowserDriver.TargetLocator.class, "activeElement")));
        Assert.assertEquals("Driver methods reaching the browser without "
                + "waiting for Vaadin", Set.of(), notWaiting);
    }

    @Test
    public void actionsWaitBeforeTheyPerform() {
        Actions actions = new Actions(driver).click(sampleElement)
                .sendKeys("x");
        Assert.assertTrue(waitsBeforeLastBrowserCall(actions,
                method(Actions.class, "perform")));
    }

    private static Method method(Class<?> type, String name) {
        try {
            return type.getMethod(name);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isTestBenchApi(Method method) {
        return method.getDeclaringClass().getPackageName()
                .equals("com.vaadin.testbench")
                && !Modifier.isStatic(method.getModifiers());
    }

    private boolean waitsBeforeLastBrowserCall(Object target, Method method) {
        calls.clear();
        try {
            method.invoke(target, arguments(method));
        } catch (InvocationTargetException e) {
            // The fake browser answers null and false, which many methods
            // cannot take; only the calls made so far matter.
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        int lastCall = -1;
        for (int i = 0; i < calls.size(); i++) {
            if (!calls.get(i).equals("wait")) {
                lastCall = i;
            }
        }
        return lastCall < 0 || calls.subList(0, lastCall).contains("wait");
    }

    private Object[] arguments(Method method) {
        return Arrays.stream(method.getParameterTypes()).map(this::argument)
                .toArray();
    }

    private Object argument(Class<?> type) {
        if (type == String.class || type == CharSequence.class
                || type == Object.class) {
            return "x";
        } else if (type == int.class || type == Integer.class) {
            return 1;
        } else if (type == long.class || type == Long.class) {
            return 1L;
        } else if (type == double.class || type == Double.class) {
            return 1.0;
        } else if (type == boolean.class || type == Boolean.class) {
            return true;
        } else if (type == String[].class) {
            return new String[] { "x" };
        } else if (type == CharSequence[].class) {
            return new CharSequence[] { "x" };
        } else if (type == Keys[].class) {
            return new Keys[0];
        } else if (type == Keys.class) {
            return Keys.SHIFT;
        } else if (type == TestBenchElement.class) {
            return sampleElement;
        } else if (type == Object[].class) {
            return new Object[0];
        } else if (type == Map.class) {
            return Map.of();
        } else if (type == Class.class) {
            return TestBenchElement.class;
        } else if (type == By.class) {
            return By.id("x");
        } else if (type == File.class) {
            return new File("x");
        }
        // BufferedImage, Predicate and the like
        return null;
    }

    private static String describe(Method method) {
        return method.getName() + Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName).toList();
    }
}
