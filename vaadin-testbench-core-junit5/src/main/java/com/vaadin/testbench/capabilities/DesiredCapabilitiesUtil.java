package com.vaadin.testbench.capabilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;

public class DesiredCapabilitiesUtil {

    private static Logger getLogger() {
        return LoggerFactory.getLogger(DesiredCapabilitiesUtil.class);
    }

    /*
     * Returns desired browser capabilities according to browsers defined in the
     * test class, filtered by possible filter parameters. Use
     * {@code @RunLocally} annotation or com.vaadin.testbench.runLocally
     * property to override all capabilities.
     */
    public static Collection<DesiredCapabilities> getDesiredCapabilities(
            ExtensionContext context) {
        if (testRunsLocally(context)) {
            Collection<DesiredCapabilities> desiredCapabilities = new ArrayList<>();
            Class<?> javaTestClass = context.getRequiredTestClass();
            desiredCapabilities.add(BrowserUtil.getBrowserFactory().create(
                    getRunLocallyBrowserName(javaTestClass),
                    getRunLocallyBrowserVersion(javaTestClass)));
            return desiredCapabilities;
        } else {
            return getFilteredCapabilities(context);
        }
    }

    /**
     * Evaluates if test can be executed in terms of current {@link ExtensionContext}.
     * @param context ExtensionContext appropriate for current test method
     * @return {@link ConditionEvaluationResult} enabled if test fulfills requirements, disabled otherwise
     */
    public static ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (!CapabilitiesTest.class
                .isAssignableFrom(context.getRequiredTestClass())) {
            return ConditionEvaluationResult.disabled(context.getRequiredTestClass().getName() + " only supports "
                    + CapabilitiesTest.class.getName());
        }

        Collection<DesiredCapabilities> desiredCapabilities = getDesiredCapabilities(
                context);
        if (desiredCapabilities.isEmpty()
                || categoryIsExcludedOrNotExcplicitlyIncluded(context)) {
            return ConditionEvaluationResult
                    .disabled("Desired capabilities not present.");
        }
        return ConditionEvaluationResult
                .enabled("Desired capabilities present.");
    }

    private static boolean categoryIsExcludedOrNotExcplicitlyIncluded(
            ExtensionContext context) {
        Class<?> c = context.getRequiredTestClass();

        if (categoryIsExcluded(c)) {
            return true;
        }

        if (explicitInclusionIsUsed()) {
            return !categoryIsIncluded(c);
        }

        return false;
    }

    private static boolean categoryIsIncluded(Class<?> c) {
        String include = System.getProperty("categories.include");
        if (include != null && include.trim().length() > 0) {
            return hasCategoryFor(c, include.toLowerCase().trim());
        }

        return false;
    }

    private static boolean explicitInclusionIsUsed() {
        String include = System.getProperty("categories.include");

        return include != null && include.trim().length() > 0;
    }

    private static boolean categoryIsExcluded(Class<?> c) {
        String exclude = System.getProperty("categories.exclude");
        if (exclude != null && exclude.trim().length() > 0) {
            return hasCategoryFor(c, exclude.toLowerCase().trim());
        }

        return false;
    }

    private static boolean hasCategoryFor(Class<?> c, String searchString) {
        if (hasCategory(c)) {
            return searchString.contains(getCategory(c).toLowerCase());
        }

        return false;
    }

    private static boolean hasCategory(Class<?> c) {
        return c.getAnnotation(TestCategory.class) != null;
    }

    private static String getCategory(Class<?> c) {
        return c.getAnnotation(TestCategory.class).value();
    }

    public static Browser getRunLocallyBrowserName(Class<?> testClass) {

        String runLocallyBrowserName = Parameters.getRunLocallyBrowserName();
        if (runLocallyBrowserName != null) {
            return Browser.valueOf(runLocallyBrowserName.toUpperCase());
        }
        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return null;
        }
        return runLocally.value();
    }

    public static String getRunLocallyBrowserVersion(Class<?> testClass) {
        String runLocallyBrowserVersion = Parameters
                .getRunLocallyBrowserVersion();
        if (runLocallyBrowserVersion != null) {
            return runLocallyBrowserVersion;
        }

        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return "";
        }
        return runLocally.version();
    }

    private static boolean testRunsLocally(ExtensionContext context) {
        if (Parameters.getRunLocallyBrowserName() != null) {
            return true;
        }

        RunLocally runLocally = context.getRequiredTestClass()
                .getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return false;
        }
        return true;
    }

    /*
     * Takes the desired browser capabilities defined in the test class and
     * returns a browser capabilities filtered browsers.include and
     * browsers.exclude system properties. (if present)
     */
    private static Collection<DesiredCapabilities> getFilteredCapabilities(
            ExtensionContext context) {

        Collection<DesiredCapabilities> desiredCapabilites = getBrowsersConfiguration(
                context);

        ArrayList<DesiredCapabilities> filteredCapabilities = new ArrayList<>();

        String include = System.getProperty("browsers.include");
        String exclude = System.getProperty("browsers.exclude");

        for (DesiredCapabilities d : desiredCapabilites) {
            String browserName = (d.getBrowserName() + d.getBrowserVersion())
                    .toLowerCase();
            if (include != null && include.trim().length() > 0) {
                if (include.trim().toLowerCase().contains(browserName)) {
                    filteredCapabilities.add(d);
                }
            } else {
                filteredCapabilities.add(d);
            }

            if (exclude != null && exclude.trim().length() > 0) {
                if (exclude.trim().toLowerCase().contains(browserName)) {
                    filteredCapabilities.remove(d);
                }
            }

        }
        return filteredCapabilities;
    }

    private static Collection<DesiredCapabilities> getBrowsersConfiguration(
            ExtensionContext context) {

        Class<?> klass = context.getRequiredTestClass();

        while (klass != null) {
            Method[] declaredMethods = klass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                // TODO if already found one annotated method in class, warn
                // user?

                if (method.isAnnotationPresent(BrowserConfiguration.class)) {
                    boolean methodSignatureIsValid = validateBrowserConfigurationAnnotatedSignature(
                            method);

                    if (!methodSignatureIsValid) {
                        /*
                         * ignore this method and searches for another
                         * BrowserConfiguration annotated method in this class'
                         * superclasses
                         */
                        break;
                    }

                    try {
                        return (Collection<DesiredCapabilities>) method
                                .invoke(getTestClassInstance(context));
                    } catch (Exception e) {
                        // Handle possible exceptions.

                        String errMsg = String.format(
                                "Error occurred while invoking BrowserConfiguration method %s.%s(). Method was ignored, searching BrowserConfiguration method in superclasses",
                                method.getDeclaringClass().getName(),
                                method.getName());
                        getLogger().info(errMsg, e);

                        /*
                         * ignore this method and searches for another
                         * BrowserConfiguration annotated method in this class'
                         * superclasses
                         */
                        break;
                    }
                }
            }
            klass = klass.getSuperclass();
        }

        // No valid BrowserConfiguration annotated method was found
        return CapabilitiesTest.getDefaultCapabilities();
    }

    /**
     * Validates the signature of a BrowserConfiguration annotated method.
     *
     * @param method
     *            BrowserConfiguration annotated method
     * @return true if method signature is valid. false otherwise.
     */
    private static boolean validateBrowserConfigurationAnnotatedSignature(
            Method method) {
        String genericErrorMessage = "Error occurred while invoking BrowserConfigurationMethod %s.%s()."
                + " %s. Method was ignored, searching BrowserConfiguration method in superclasses";

        if (method.getParameterTypes().length != 0) {
            String errMsg = String.format(genericErrorMessage,
                    method.getDeclaringClass().getName(), method.getName(),
                    "BrowserConfiguration annotated method must not require any arguments");
            getLogger().info(errMsg);
            return false;
        }
        if (!Collection.class.isAssignableFrom(method.getReturnType())) {
            /*
             * Validates if method's return type is Collection.
             * ClassCastException may still occur if method's return type is not
             * Collection<DesiredCapabilities>
             */
            String errMsg = String.format(genericErrorMessage,
                    method.getDeclaringClass().getName(), method.getName(),
                    "BrowserConfiguration annotated method must return a Collection<DesiredCapabilities>");
            getLogger().info(errMsg);
            return false;
        }
        return true;
    }

    private static CapabilitiesTest getTestClassInstance(ExtensionContext context)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        CapabilitiesTest testClassInstance = (CapabilitiesTest) context
                .getRequiredTestClass().getConstructor().newInstance();
        return testClassInstance;
    }

}
